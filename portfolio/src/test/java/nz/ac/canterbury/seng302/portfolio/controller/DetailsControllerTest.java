package nz.ac.canterbury.seng302.portfolio.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;

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


    private UserResponse testUser = UserResponse.newBuilder()
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

    public Sprint sprint = new Sprint();

    public class CustomArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType().isAssignableFrom(AuthState.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            return validAuthState;
        }
    }

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

    private Principal principal = new JwtAuthenticationToken("token", new User("username", "password", true, true, true, true, new ArrayList<>()), new ArrayList<>());

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(DetailsController.class)
            .setCustomArgumentResolvers(new CustomArgumentResolver())
            .addInterceptors((HandlerInterceptor) new AuthenticationClientInterceptor())
            .build();

        mockMvc.perform(get("/landing"));
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
    public void getDetailsWithValidCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getId(validAuthState)).thenReturn(1);
        when(accountClientService.getUserById(1)).thenReturn(testUser);


        mockMvc.perform(get("/details").requestAttr("id", 1))
            .andExpect(status().isOk()) // Whether to return the status "200 OK"
            .andExpect(view().name("teacherProjectDetails")); // Whether to return the template "account"
            //Model test.
            //.andExpect(model().attribute("sprints", sprintList));
    }

    @Test
    public void getDetailsWithTeacherCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));


        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);


        mockMvc.perform(get("/details").requestAttr("id", 1))
            .andExpect(status().isOk()) // Whether to return the status "200 OK"
            .andExpect(view().name("teacherProjectDetails")) // Whether to return the template "account"
            .andExpect(model().attribute("errorcode", ""))
            .andExpect(model().attribute("errorShow", "display:none;"))
            .andExpect(model().attribute("roleName", "teacher"))
            .andExpect(model().attribute("sprints", sprintService.getSprintByParentId(1)));
    }

    @Test
    public void getDetailsWithStudentCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);


        mockMvc.perform(get("/details").requestAttr("id", 1))
            .andExpect(status().isOk()) // Whether to return the status "200 OK"
            .andExpect(view().name("userProjectDetails")) // Whether to return the template "account"
            .andExpect(model().attribute("errorcode", ""))
            .andExpect(model().attribute("errorShow", "display:none;"))
            .andExpect(model().attribute("roleName", "student"))
            .andExpect(model().attribute("sprints", sprintService.getSprintByParentId(1)));
    }

    @Test
    public void postDetailsNewSprintAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        Integer size = sprintService.getSprintByParentId(1).size();

        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("teacherProjectDetails"))
                .andExpect(model().attribute("errorcode", ""))
                .andExpect(model().attribute("errorShow", "display:none;"));
        assertEquals(size+1, sprintService.getSprintByParentId(1).size());
    }

    @Test
    public void postDetailsNewSprintMaximumAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        // Fill the project dates
        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));
        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));
        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));
        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));
        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));
        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));
        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));
        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));
        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));
        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));
        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));

        Integer size = sprintService.getSprintByParentId(1).size();

        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("teacherProjectDetails"))
                .andExpect(model().attribute("errorcode", "There is not enough time in your project for another sprint"))
                .andExpect(model().attribute("errorShow", ""));
        assertEquals(size, sprintService.getSprintByParentId(1).size());
    }

    @Test
    public void postDetailsNewSprintAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        Integer size = sprintService.getSprintByParentId(1).size();

        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("teacherProjectDetails"))
                .andExpect(model().attribute("errorcode", ""))
                .andExpect(model().attribute("errorShow", "display:none;"));
        assertEquals(size, sprintService.getSprintByParentId(1).size());
    }

    @Test
    public void postDetailsDeleteSprintAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));

        Integer size = sprintService.getSprintByParentId(1).size();

        mockMvc.perform(post("/delete-sprint").requestAttr("deleteprojectId", 1).requestAttr("sprintId",1))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("teacherProjectDetails"))
                .andExpect(model().attribute("errorcode", ""))
                .andExpect(model().attribute("errorShow", "display:none;"));
        assertEquals(size-1, sprintService.getSprintByParentId(1).size());
    }

    @Test
    public void postDetailsDeleteSprintAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        mockMvc.perform(post("/new-sprint").requestAttr("projectId", 1));

        Integer size = sprintService.getSprintByParentId(1).size();

        mockMvc.perform(post("/delete-sprint").requestAttr("deleteprojectId", 1).requestAttr("sprintId",1))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("userProjectDetails"))
                .andExpect(model().attribute("errorcode", ""))
                .andExpect(model().attribute("errorShow", "display:none;"));
        assertEquals(size, sprintService.getSprintByParentId(1).size());
    }
}
