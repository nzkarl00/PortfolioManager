package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.DeadlineRepository;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LandingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LandingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    ProjectRepository projectRepository;

    @MockBean
    ProjectService projectService;

    @MockBean
    SprintService sprintService;

    @MockBean
    NavController navController;

    @MockBean
    SimpMessagingTemplate template;

    @MockBean
    DeadlineRepository deadlineRepository;

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
    public void getLandingPageWithValidAdminCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getId(validAuthStateAdmin)).thenReturn(1);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");

        when(accountClientService.getUserById(1)).thenReturn(testUserAdmin);
        Assertions.assertTrue(projectService.getAllProjects().isEmpty());
        mockMvc.perform(get("/landing"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("landing"))
                .andExpect(model().attribute("projects", projectService.getAllProjects()));
        verify(projectRepository, times(1)).save(any(Project.class));
    }
    @Test
    public void getLandingPageWithValidTeacherCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(1);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");

        when(accountClientService.getUserById(1)).thenReturn(testUserTeacher);
        Assertions.assertTrue(projectService.getAllProjects().isEmpty());
        mockMvc.perform(get("/landing"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("landing"))
                .andExpect(model().attribute("projects", projectService.getAllProjects()));
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void getLandingPageWithValidStudentCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getId(validAuthStateStudent)).thenReturn(1);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");

        when(accountClientService.getUserById(1)).thenReturn(testUserStudent);
        Assertions.assertTrue(projectService.getAllProjects().isEmpty());
        mockMvc.perform(get("/landing"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("landing"))
                .andExpect(model().attribute("projects", projectService.getAllProjects()))
                .andExpect(model().attribute("display", "display:none;"));
        verify(projectRepository, times(1)).save(any(Project.class));
    }


    @Test
    public void postProjectAsValidTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(1);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");

        when(accountClientService.getUserById(1)).thenReturn(testUserTeacher);
        Assertions.assertTrue(projectService.getAllProjects().isEmpty());
        mockMvc.perform(post("/new-project"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:edit-project?id=" + 0));;
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void postProjectValidAsAdmin() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getId(validAuthStateAdmin)).thenReturn(1);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");

        when(accountClientService.getUserById(1)).thenReturn(testUserAdmin);
        Assertions.assertTrue(projectService.getAllProjects().isEmpty());
        mockMvc.perform(post("/new-project"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:edit-project?id=" + 0));;
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void postProjectAsValidStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getId(validAuthStateStudent)).thenReturn(1);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");

        when(accountClientService.getUserById(1)).thenReturn(testUserStudent);
        Assertions.assertTrue(projectService.getAllProjects().isEmpty());
        mockMvc.perform(post("/new-project"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:landing"));;
        verify(projectRepository, never()).save(any(Project.class));
    }

}
