package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GroupClientService;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AddGroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class AddGroupControllerTest {
    private AuthState validAuthState = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("ADMIN").build()) // Set the mock user's role
            .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    private AuthState teacherAuthState = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("teacher").build()) // Set the mock user's role
            .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    private AuthState studentAuthState = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("student").build()) // Set the mock user's role
            .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    CreateGroupResponse response = CreateGroupResponse.newBuilder().setIsSuccess(true).setMessage("test message").build();

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
        mockMvc = MockMvcBuilders.standaloneSetup(AddGroupController.class)
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

    @Test
    void getGroupsAsAdmin() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthState)).thenReturn("admin");

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/addGroup"))
                .andExpect(status().isOk())
                .andExpect(view().name("addGroup"));
    }

    @Test
    void getGroupsAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(teacherAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(teacherAuthState)).thenReturn("teacher");

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/addGroup"))
                .andExpect(status().isOk())
                .andExpect(view().name("addGroup"));
    }

    @Test
    void getGroupsAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(studentAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(studentAuthState)).thenReturn("student");

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/addGroup"))
                .andExpect(status().is3xxRedirection()) // gets redirected as they are a student not admin or teacher
                .andExpect(view().name("redirect:groups"));
    }

    @Test
    void postGroupsAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(studentAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(studentAuthState)).thenReturn("student");

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(post("/createGroup")
                        .param("longName", "LONG")
                        .param("shortName", "SHORT"))
                .andExpect(status().is3xxRedirection()) // gets redirected as they are a student not admin or teacher
                .andExpect(view().name("redirect:groups"));
    }

    @Test
    void postGroupsAsAdmin() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthState)).thenReturn("admin");
        when(groupsService.create("SHORT", "LONG")).thenReturn(response);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(post("/createGroup")
                        .param("longName", "LONG")
                        .param("shortName", "SHORT"))
                .andExpect(status().is3xxRedirection()) // gets redirected to addGroup with possible messages to read
                .andExpect(view().name("redirect:addGroup"));
    }

    @Test
    void postGroupsAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(teacherAuthState, ""));

        utilities.when(() -> AuthStateInformer.getRole(teacherAuthState)).thenReturn("teacher");
        when(groupsService.create("SHORT", "LONG")).thenReturn(response);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(post("/createGroup")
                        .param("longName", "LONG")
                        .param("shortName", "SHORT"))
                .andExpect(status().is3xxRedirection()) // gets redirected to addGroup with possible messages to read
                .andExpect(view().name("redirect:addGroup"));
    }
}