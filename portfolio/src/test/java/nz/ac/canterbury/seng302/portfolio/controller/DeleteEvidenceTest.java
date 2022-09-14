package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepoRepository;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.security.Principal;
import java.time.LocalDate;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.invalidAuthState;
import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.validAuthStateTeacher;
import static nz.ac.canterbury.seng302.portfolio.common.CommonProjectItems.getValidProject;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EvidenceListController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DeleteEvidenceTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EvidenceRepository evidenceRepository;
    @MockBean
    private EvidenceTagRepository evidenceTagRepository;
    @MockBean
    private SkillTagRepository skillRepository;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private AccountClientService accountClientService;
    @MockBean
    private NavController navController;
    @MockBean
    private EvidenceService evidenceService;
    @MockBean
    private WebLinkRepository webLinkRepository;
    @MockBean
    private SprintService sprintService;
    @MockBean
    private EvidenceUserRepository evidenceUserRepository;
    @MockBean
    private GroupsClientService groupsClientService;
    @MockBean
    private GroupRepoRepository groupRepoRepository;
    @MockBean
    private GitlabClient gitlabClient;
    @MockBean
    private LinkedCommitRepository linkedCommitRepository;

    private static final Project testProject = getValidProject();
    private static final LocalDate may4 = LocalDate.parse("2022-05-04");
    private static final Evidence testEvidence = new Evidence(
            123456,
            testProject,
            "Evidence One",
            "This evidence is the first to be submitted",
            may4,
            Evidence.SERVICE
    );

    static MockedStatic<AuthStateInformer> utilities;
    private static final MultiValueMap<String, String> DeleteEvidenceParams = new LinkedMultiValueMap<>();

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(EvidenceListController.class)
                .build();

    }

    @BeforeAll
    public static void open() {
        utilities = Mockito.mockStatic(AuthStateInformer.class );
        DeleteEvidenceParams.add("projectId", String.valueOf(testProject.getId()));
        DeleteEvidenceParams.add("evidenceId", "1");
    }

    @AfterAll
    public static void close() {
        utilities.close();
    }

    /**
     * Test for the case where a user attempts to delete a piece of evidence they own
     * @throws Exception in the case where the mock mvc fails or project is not found
     */
    @Test
    public void deleteValidEvidence_OwnedByUser() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(123456); // Owns test evidence
        when(evidenceRepository.findById(1)).thenReturn(testEvidence);
        when(projectService.getProjectById(0)).thenReturn(testProject);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/delete-evidence").params(DeleteEvidenceParams))
                .andExpect(status().is3xxRedirection())
                // Redirected to evidence page for project
                .andExpect(view().name("redirect:evidence?pi=0"));
        verify(evidenceService).deleteEvidence(refEq(testEvidence));
    }

    /**
     * Test for the case where a user attempts to delete a piece of evidence they do not own
     * @throws Exception in the case where the mock mvc fails or project is not found
     */
    @Test
    public void deleteValidEvidence_NotOwnedByUser() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(654321); // Doesn't own test evidence
        when(evidenceRepository.findById(1)).thenReturn(testEvidence);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/delete-evidence").params(DeleteEvidenceParams))
                .andExpect(status().is3xxRedirection())
                // Redirected to evidence page for project
                .andExpect(view().name("redirect:evidence?pi=0"));
        verifyNoInteractions(evidenceService);
    }

        /**
         * Test for the case where a user attempts to delete a piece of evidence that doesn't exist
         * @throws Exception in the case where the mock mvc fails or project is not found
         */
    @Test
    public void deleteInvalidEvidence() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(123456); // Owns test evidence
        when(projectService.getProjectById(0)).thenReturn(testProject);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/delete-evidence").params(DeleteEvidenceParams))
                .andExpect(status().is3xxRedirection())
                // Redirected to evidence page for project
                .andExpect(view().name("redirect:evidence?pi=0"));
        verifyNoInteractions(evidenceService);
    }

        /**
         * Test for the case where a user attempts to delete a piece of evidence they do not own
         * @throws Exception in the case where the mock mvc fails or project is not found
         */
    @Test
    public void deleteValidEvidence_WithInvalidPermissions() throws Exception {
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(invalidAuthState, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        when(evidenceRepository.findById(1)).thenReturn(testEvidence);
        when(projectService.getProjectById(0)).thenReturn(testProject);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/delete-evidence").params(DeleteEvidenceParams))
                .andExpect(status().is3xxRedirection())
                // Redirected to evidence page for project
                .andExpect(view().name("redirect:evidence?pi=0"));
        verifyNoInteractions(evidenceService);
    }
}
