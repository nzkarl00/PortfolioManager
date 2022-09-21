package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepo;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepoRepository;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GitlabClient;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.validAuthStateAdmin;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CommitSearchController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommitSearchControllerTest {

    private final GroupRepo exampleRepo = new GroupRepo(
            1,
            "owner",
            "name",
            "api-key"
    );
    private static List<Commit> exampleCommits;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    GroupRepoRepository groupRepoRepository;

    @MockBean
    GitlabClient gitlabClient;

    @MockBean
    Logger logger;

    @Before
     void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(CommitSearchController.class)
                .build();
    }

    // setting up and closing the mocked static authStateInformer
    static MockedStatic<AuthStateInformer> utilities;

    @BeforeAll
    static void open() {
        utilities = Mockito.mockStatic(AuthStateInformer.class);

        Commit commit = new Commit();
        commit.setAuthorName("John");
        commit.setAuthorEmail("john@example.com");
        commit.setId("223ac74e33ba54500c135f32fb29a23f38b73442");
        commit.setTitle("Title");
        commit.setMessage("Title\n");
        // Monday, August 8, 2022
        commit.setCreatedAt(Date.from(Instant.ofEpochSecond(1660000000)));

        Commit commit2 = new Commit();
        commit.setAuthorName("Not Same User");
        commit.setAuthorEmail("notjohn@example.com");
        commit.setId("f89ba2707192e1077956a48494b6b61bd635214d");
        commit.setTitle("Not title");
        commit.setMessage("Not title\n");
        // Friday, September 2, 2022
        commit.setCreatedAt(Date.from(Instant.ofEpochSecond(1662100000)));
        exampleCommits = List.of(commit);
    }

    @AfterAll
    static void close() {
        utilities.close();
    }

    @Test
    void validateDetailsParameters_allPresentAndValid() {
        Assertions.assertDoesNotThrow(() -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.of("a1"),
                    Optional.of("John"),
                    Optional.of("john@example.com"),
                    Optional.of("2022-01-01"),
                    Optional.of("2022-01-09")
            );
        });
    }

    @Test
    void validateDetailsParameters_allEmpty() {
        Assertions.assertDoesNotThrow(() -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            );
        });
    }

    @Test
    void validateDetailsParameters_emptyStrings() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.of(""),
                    Optional.of("John"),
                    Optional.of("john@example.com"),
                    Optional.of("2022-01-01"),
                    Optional.of("2022-01-09")
            );
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.of("a"),
                    Optional.of(""),
                    Optional.of("john@example.com"),
                    Optional.of("2022-01-01"),
                    Optional.of("2022-01-09")
            );
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.of("a"),
                    Optional.of("John"),
                    Optional.of(""),
                    Optional.of("2022-01-01"),
                    Optional.of("2022-01-09")
            );
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.of("a"),
                    Optional.of("John"),
                    Optional.of("john@example.com"),
                    Optional.of(""),
                    Optional.of("2022-01-09")
            );
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.of("a"),
                    Optional.of("John"),
                    Optional.of("john@example.com"),
                    Optional.of("2022-01-01"),
                    Optional.of("")
            );
        });
    }

    @Test
    void validateDetailsParameters_commitHash() {
        // Too long
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.of("A".repeat(41)),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            );
        });

        // Non-hex
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.of("z"),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            );
        });
    }

    @Test
    void validateDetailsParameters_authorName() {
        // Non alpha
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.empty(),
                    Optional.of("9091!"),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            );
        });
    }

    @Test
    void validateDetailsParameters_authorEmail() {
        // Invalid email
        Assertions.assertThrows(IllegalArgumentException.class, () -> CommitSearchController.validateDetailsParameters(
                Optional.empty(),
                Optional.empty(),
                Optional.of("this-is-not-an-email"),
                Optional.empty(),
                Optional.empty()
        ));
    }

    @Test
    void validateDetailsParameters_dateRange() {
        // Empty strings
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        CommitSearchController.validateDetailsParameters(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(""),
                Optional.of("")
        ));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(""),
                    Optional.of("1990-01-01")
            );
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("1990-01-01"),
                    Optional.of("")
            );
        });

        // Invalid strings
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("2022-no-date"),
                    Optional.of("1990-01-01")
            );
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("1990-01-01"),
                    Optional.of("1990-no-date")
            );
        });

        // Both valid
        Assertions.assertDoesNotThrow(() -> {
            CommitSearchController.validateDetailsParameters(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("1990-01-01"),
                    Optional.of("1990-02-01")
            );
        });
    }

    @Test
    void searchCommits_invalidParameter() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

//        utilities.when(() -> AuthStateInformer.getId(validAuthStateAdmin)).thenReturn(1);
//        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("role");

//        when(accountClientService.getUserById(1)).thenReturn(testUserTeacher);
//
//        when(projectService.getProjectById(1)).thenReturn(testProject);


        mockMvc.perform(get("/evidence/search-commits").param(
                "group-id", String.valueOf(1)).param("author-email", "not-email")
                ).andExpect(model().attribute("errorMessage", "Parameters passed to searchForCommit are invalid, rejecting: Author email must be a valid email"));
    }

    @Test
    void searchCommits_missingGroupID() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        mockMvc.perform(get("/evidence/search-commits"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void searchCommits_groupNotFound() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        // Return no match for group id
        when(groupRepoRepository.findByParentGroupId(-1)).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/evidence/search-commits")
                        .param("group-id", "-1")
        ).andExpect(model().attribute("errorMessage", "No repository is configured for group with ID=-1"));
    }

    @Test
    void searchCommits_matchingCommitsNoFilters() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        // Return no match for group id
        when(groupRepoRepository.findByParentGroupId(1)).thenReturn(Optional.of(exampleRepo));
        Mockito.doReturn(exampleCommits).when(gitlabClient).getFilteredCommits(
                anyString(),
                anyString(),
                anyString(),
                any(),
                any(),
                any(),
                any(),
                any()
        );

        mockMvc.perform(
                        get("/evidence/search-commits")
                                .param("group-id", "1")
                )
                .andExpect(status().isOk());
    }

    @Test
    void searchCommits_gitlabApiException() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        // Return no match for group id
        when(groupRepoRepository.findByParentGroupId(1)).thenReturn(Optional.of(exampleRepo));
        Mockito.doThrow(new GitLabApiException("Some exception")).when(gitlabClient).getFilteredCommits(
                anyString(),
                anyString(),
                anyString(),
                any(),
                any(),
                any(),
                any(),
                any()
        );

        mockMvc.perform(
                        get("/evidence/search-commits")
                                .param("group-id", "1")
                ).andExpect(model().attribute("errorMessage", "Communicating with the Gitlab API failed, please try again"));
    }
}
