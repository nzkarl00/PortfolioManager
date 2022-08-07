package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepo;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepoRepository;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyGroupDetailsResponse;
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

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EditGroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class EditGroupControllerTest {

    private AuthState validAuthState =
        nz.ac.canterbury.seng302.shared.identityprovider.AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("ADMIN")
                .build()) // Set the mock user's role
            .addClaims(
                ClaimDTO.newBuilder().setType("nameid").setValue("123456")
                    .build()) // Set the mock user's ID
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    private AuthState teacherAuthState =
        nz.ac.canterbury.seng302.shared.identityprovider.AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("teacher")
                .build()) // Set the mock user's role
            .addClaims(
                ClaimDTO.newBuilder().setType("nameid").setValue("123456")
                    .build()) // Set the mock user's ID
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    private AuthState studentAuthState =
        nz.ac.canterbury.seng302.shared.identityprovider.AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("student")
                .build()) // Set the mock user's role
            .addClaims(
                ClaimDTO.newBuilder().setType("nameid").setValue("123456")
                    .build()) // Set the mock user's ID
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    GroupsClientService groupsService;

    @MockBean
    NavController navController;

    @MockBean
    GroupRepoRepository groupRepoRepository;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(EditGroupController.class)
            .build();
    }

    static MockedStatic<AuthStateInformer> utilities;

    @BeforeAll
    public static void open() {
        utilities = Mockito.mockStatic(AuthStateInformer.class);
    }

    @AfterAll
    public static void close() {
        utilities.close();
    }

    /**
     * test the get as an admin, make sure it is the correct page
     */
    @Test
    void getEditGroup_asAdmin() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext =
            Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(
                new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthState))
            .thenReturn("admin");

        when(groupsService.getGroup(3)).thenReturn(response);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/editGroup").param("id", "3"))
            .andExpect(status().isOk());
    }

    GroupDetailsResponse response = GroupDetailsResponse.newBuilder()
        .setGroupId(3)
        .setLongName("Test Long")
        .setShortName("Test Short")
        .build();

    /**
     * test the get as a teacher, make sure it is the correct page
     */
    @Test
    void getEditGroup_asTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext =
            Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(
                new PreAuthenticatedAuthenticationToken(teacherAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(teacherAuthState))
            .thenReturn("teacher");

        when(groupsService.getGroup(3)).thenReturn(response);
        when(groupRepoRepository.findByParentGroupId(3)).thenReturn(Optional.of(new GroupRepo(
                1,
                "a",
                "b",
                "c"
        )));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/editGroup").param("id", "3"))
            .andExpect(status().isOk());
    }



    ModifyGroupDetailsResponse modifyResponse = ModifyGroupDetailsResponse.newBuilder()
        .setMessage("built")
        .setIsSuccess(true).build();

}
