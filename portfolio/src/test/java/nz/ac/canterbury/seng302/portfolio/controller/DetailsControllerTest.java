package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.DeadlineRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.*;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DetailsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DetailsControllerTest {


    private final Project testProject = new Project("testName", "testDescription", new Date(), new Date());
    private final Deadline deadline = new Deadline(testProject, "Deadline 1", "This is a deadline for project 1", DateParser.stringToLocalDateTime("2022-05-04", "16:20"));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    SprintRepository sprintRepo;

    @MockBean
    DeadlineService deadlineService;

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
    public void getDetailsWithValidCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getId(validAuthStateAdmin)).thenReturn(1);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("role");

        when(accountClientService.getUserById(1)).thenReturn(testUserTeacher);

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

    /**
     * Mocks the post from details and trys to delete a deadline as a teacher
     * @throws Exception
     */
    @Test
    public void postDetailsDeleteDeadlineAsTeacher() throws Exception {

        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");

        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(deadlineRepository.findById(0)).thenReturn(deadline);
        mockMvc.perform(post("/delete-deadline").param("projectId", String.valueOf(0)).param("deadlineId", String.valueOf(0)))
                .andExpect(status().is3xxRedirection()) // given this should move to details once deleted redirection is expected
                .andExpect(view().name("redirect:details?id=0")); // page is moved
        verify(deadlineRepository).deleteById(deadline.getId()); // Just checks that the deadline repo was called to delete the deadline given the id
    }

    /**
     * Mocks the post from details and trys to delete a deadline as an admin
     * @throws Exception
     */
    @Test
    public void postDetailsDeleteDeadlineAsAdmin() throws Exception {

        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");

        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(deadlineRepository.findById(0)).thenReturn(deadline);
        mockMvc.perform(post("/delete-deadline").param("projectId", String.valueOf(0)).param("deadlineId", String.valueOf(0)))
                .andExpect(status().is3xxRedirection()) // given this should move to details once deleted redirection is expected
                .andExpect(view().name("redirect:details?id=0")); // page is moved
        verify(deadlineRepository).deleteById(deadline.getId()); // Just checks that the deadline repo was called to delete the deadline given the id
    }

    /**
     * Mocks the post from details and trys to delete a deadline as a student - should not delete the deadline
     * @throws Exception
     */
    @Test
    public void postDetailsDeleteDeadlineAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");
        utilities.when(() -> AuthStateInformer.getId(validAuthStateStudent)).thenReturn(1);
        when(projectService.getProjectById(1)).thenReturn(testProject);

        mockMvc.perform(post("/delete-deadline").param("projectId", String.valueOf(1)).param("deadlineId", String.valueOf(1)))
                .andExpect(status().is3xxRedirection()) // given this should redirect to the details once attempted redirection is expected
                .andExpect(view().name("redirect:details?id=1"));
        verify(deadlineRepository, never()).deleteById(deadline.getId()); // Just checks that the deadline repo was never called
    }
}
