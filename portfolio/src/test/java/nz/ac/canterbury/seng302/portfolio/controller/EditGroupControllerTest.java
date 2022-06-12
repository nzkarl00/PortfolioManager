package nz.ac.canterbury.seng302.portfolio.controller;

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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

        when(groupsService.getGroup(1)).thenReturn(response);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/editGroup").param("id", "1"))
            .andExpect(status().isOk())
            .andExpect(view().name("editGroup"));
    }

    GroupDetailsResponse response = GroupDetailsResponse.newBuilder()
        .setGroupId(1)
        .setLongName("Test Long")
        .setShortName("Test Short").build();

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

        when(groupsService.getGroup(1)).thenReturn(response);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/editGroup").param("id", "1"))
            .andExpect(status().isOk())
            .andExpect(view().name("editGroup"));
    }

    /**
     * test the get as a student, make sure they are redirected to the
     * correct page as they are not authenticated
     */
    @Test
    void getEditGroup_asStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext =
            Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(
                new PreAuthenticatedAuthenticationToken(studentAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(studentAuthState))
            .thenReturn("student");

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/editGroup").param("id", "1"))
            .andExpect(
                status().is3xxRedirection()) // doesn't have permission to edit so get sent back to groups
            .andExpect(view().name("redirect:groups"));
    }

    ModifyGroupDetailsResponse modifyResponse = ModifyGroupDetailsResponse.newBuilder()
        .setMessage("built")
        .setIsSuccess(true).build();

    /**
     * tests the post as an admin, to edit a group
     * make sure it calls the modify service
     */
    @Test
    void postEditGroups_asAdmin() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext =
            Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(
                new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthState))
            .thenReturn("admin");
        when(groupsService.modifyGroup(1,"Long", "Short")).thenReturn(modifyResponse);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(post("/modifyGroup")
                .param("id", "1")
                .param("longName", "Long")
                .param("shortName", "Short"))
            .andExpect(
                status().is3xxRedirection()) // redirect back to edit to see any error messages
            .andExpect(view().name("redirect:editGroup?id=1"));
        verify(groupsService).modifyGroup(1,"Long", "Short");
    }

    /**
     * tests the post as an admin, to edit a group
     * make sure it calls the modify service
     */
    @Test
    void postEditGroups_asTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext =
            Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(
                new PreAuthenticatedAuthenticationToken(teacherAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(teacherAuthState))
            .thenReturn("teacher");
        when(groupsService.modifyGroup(1,"Long", "Short")).thenReturn(modifyResponse);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(post("/modifyGroup")
                .param("id", "1")
                .param("longName", "Long")
                .param("shortName", "Short"))
            .andExpect(
                status().is3xxRedirection()) // redirect back to edit to see any error messages
            .andExpect(view().name("redirect:editGroup?id=1"));
        verify(groupsService).modifyGroup(1,"Long", "Short");
    }

    /**
     * tests the post as an student, to edit a group
     * redirects them to the groups page
     */
    @Test
    void postEditGroups_asStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext =
            Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(
                new PreAuthenticatedAuthenticationToken(studentAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(studentAuthState))
            .thenReturn("student");
        when(groupsService.modifyGroup(1,"Long", "Short")).thenReturn(modifyResponse);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(post("/modifyGroup")
                .param("id", "1")
                .param("longName", "Long")
                .param("shortName", "Short"))
            .andExpect(
                status().is3xxRedirection()) // doesn't have permission to edit so get sent back to groups
            .andExpect(view().name("redirect:groups"));
        verify(groupsService, never()).modifyGroup(1,"Long", "Short");
    }
}