package nz.ac.canterbury.seng302.portfolio.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;

import com.google.protobuf.Timestamp;
import com.google.rpc.context.AttributeContext;
import nz.ac.canterbury.seng302.portfolio.authentication.AuthenticationClientInterceptor;
import nz.ac.canterbury.seng302.portfolio.authentication.JwtAuthenticationFilter;
import nz.ac.canterbury.seng302.portfolio.authentication.JwtAuthenticationToken;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DetailsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DetailsControllerTest {
    public AuthState validAuthState = AuthState.newBuilder()
        .setIsAuthenticated(true)
        .setNameClaimType("name")
        .setRoleClaimType("role")
        .addClaims(ClaimDTO.newBuilder().setType("role").setValue("ADMIN").build()) // Set the mock user's role
        .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
        .setAuthenticationType("AuthenticationTypes.Federation")
        .setName("validtesttoken")
        .build();

    public AuthState validAuthStateTeacher = AuthState.newBuilder()
        .setIsAuthenticated(true)
        .setNameClaimType("name")
        .setRoleClaimType("role")
        .addClaims(ClaimDTO.newBuilder().setType("role").setValue("TEACHER").build()) // Set the mock user's role
        .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
        .setAuthenticationType("AuthenticationTypes.Federation")
        .setName("validtesttoken")
        .build();

    public AuthState validAuthStateStudent = AuthState.newBuilder()
        .setIsAuthenticated(true)
        .setNameClaimType("name")
        .setRoleClaimType("role")
        .addClaims(ClaimDTO.newBuilder().setType("role").setValue("STUDENT").build()) // Set the mock user's role
        .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
        .setAuthenticationType("AuthenticationTypes.Federation")
        .setName("validtesttoken")
        .build();


    private final UserResponse testUser = UserResponse.newBuilder()
            .setBio("testbio")
            .setCreated(Timestamp.newBuilder().setSeconds(10))
            .setEmail("test@email")
            .setFirstName("testfirstname")
            .setLastName("testlastname")
            .setMiddleName("testmiddlename")
            .setNickname("testnickname")
            .setPersonalPronouns("test/test")
            .addRoles(UserRole.TEACHER)
            .build();

    private final Project testProject = new Project("testName", "testDescription", new Date(), new Date());

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    SprintRepository sprintRepo;

    @MockBean
    ProjectService projectService;

    @MockBean
    SprintService sprintService;

    @MockBean
    NavController navController;

    @MockBean
    SimpMessagingTemplate template;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(DetailsController.class)
            .build();
    }

    // setting up and closing the mocked static authStateInformer
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
    public void getDetailsWithValidCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getId(validAuthState)).thenReturn(1);
        utilities.when(() -> AuthStateInformer.getRole(validAuthState)).thenReturn("role");

        when(accountClientService.getUserById(1)).thenReturn(testUser);

        when(projectService.getProjectById(1)).thenReturn(testProject);


        mockMvc.perform(get("/details").param("id", String.valueOf(1)))
            .andExpect(status().isOk()) // Whether to return the status "200 OK"
            .andExpect(view().name("userProjectDetails"));
    }

    @Test
    public void getDetailsWithTeacherCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(1);

        when(projectService.getProjectById(1)).thenReturn(testProject);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);


        mockMvc.perform(get("/details").param("id", String.valueOf(1)))
            .andExpect(status().isOk()); // Whether to return the status "200 OK"
        //
    }

    @Test
    public void getDetailsWithStudentCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        when(projectService.getProjectById(1)).thenReturn(testProject);

        mockMvc.perform(get("/details").param("id", String.valueOf(1)))
            .andExpect(status().isOk()) // Whether to return the status "200 OK"
            .andExpect(view().name("userProjectDetails")) // Whether to return the template "account"
            .andExpect(model().attribute("errorShow", "display:none;"))
            .andExpect(model().attribute("roleName", "student"))
            .andExpect(model().attribute("sprints", sprintService.getSprintByParentId(1)));
    }

    @Test
    public void postDetailsDeleteSprintAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        when(projectService.getProjectById(1)).thenReturn(testProject);

        mockMvc.perform(post("/delete-sprint").param("deleteprojectId", String.valueOf(1)).param("sprintId", String.valueOf(1)))
                .andExpect(status().is3xxRedirection()) // given this should move to details once deleted redirection is expected
                .andExpect(view().name("redirect:details?id=1")); // page is moved
    }

    @Test
    public void postDetailsDeleteSprintAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");
        utilities.when(() -> AuthStateInformer.getId(validAuthStateStudent)).thenReturn(1);
        when(projectService.getProjectById(1)).thenReturn(testProject);

        mockMvc.perform(post("/new-sprint").param("projectId", String.valueOf(1)));

        mockMvc.perform(post("/delete-sprint").param("deleteprojectId", String.valueOf(1)).param("sprintId", String.valueOf(1)))
                .andExpect(status().is3xxRedirection()) // given this should redirect to the details once attempted redirection is expected
                .andExpect(view().name("redirect:details?id=1"));
    }
}
