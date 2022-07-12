package nz.ac.canterbury.seng302.portfolio.controller;

import io.cucumber.java.Before;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.Date;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.*;
import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.validAuthStateTeacher;
import static nz.ac.canterbury.seng302.portfolio.common.CommonProjectItems.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EditDatesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EditDatesControllerTest {

    private static final Date may1 = DateParser.stringToDate("2022-05-01");
    private static final Date june1 = DateParser.stringToDate("2022-06-01");
    private static final LocalDateTime may4 = DateParser.stringToLocalDateTime("2022-05-04", "16:20");
    private static final LocalDateTime june4 = DateParser.stringToLocalDateTime("2022-06-04", "16:20");

    private final Project testProject = new Project("testName", "testDescription", may1, june1);
    private final Deadline testDeadline = new Deadline(testProject, "Deadline 1", "This is a deadline for project 1", may4);
    private final Deadline validDeadline = new Deadline(testProject, "Deadline 1", "This is a deadline for project 1", may4);

    private final Event validEvent= new Event(testProject, "Event 1", "This is an event for project 1", may4, june4);


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    DeadlineRepository deadlineRepo;

    @MockBean
    ProjectService projectService;

    @MockBean
    DeadlineService deadlineService;

    @MockBean
    NavController navController;

    @MockBean
    DateSocketService dateSocketService;

    @MockBean
    ProjectItemService projectItemService;

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

    /**
     * Testing if a course admin can load the edit dates page with a deadline, result should be the edit dates page loads
     * @throws Exception
     */
    @Test
    public void getEditDatesForDeadlineWithAdminCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getId(validAuthStateAdmin)).thenReturn(1);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
        // Declaring mockito when conditions
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getProjectItemByIdAndType("Deadline", 0)).thenReturn(testDeadline);
        // Executing the mocked get request, checking that the page is displayed
        mockMvc.perform(get("/edit-date").param("projectId", String.valueOf(0)).param("dateId", String.valueOf(0)).param("itemType", "Deadline"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("editDates")).andExpect(model().attribute("type", "Deadline"));
    }

    /**
     * Testing if a teacher can load the date editing page with a deadline, result should be a redirect to the editing page.
     * @throws Exception
     */
    @Test
    public void getEditDatesWithTeacherCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Declaring mockito when conditions
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(1);
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getProjectItemByIdAndType("Deadline", 0)).thenReturn(testDeadline);
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        // Executing the mocked get request, checking that the page is displayed
        mockMvc.perform(get("/edit-date").param("projectId", String.valueOf(0)).param("dateId", String.valueOf(0)).param("itemType", "Deadline"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("editDates"));
    }

    /**
     * Testing if a student can load the date editing page, result should be a redirect to the error page instead
     * @throws Exception
     */
    @Test
    public void getEditDatesWithStudentCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        // Declaring mockito when conditions
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getProjectItemByIdAndType("Deadline", 0)).thenReturn(testDeadline);
        // Executing the mocked get request, checking that the page is displayed
        mockMvc.perform(get("/edit-date").param("projectId", String.valueOf(0)).param("dateId", String.valueOf(0)).param("itemType", "Deadline"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("error")); // Returns the error page as a student should not be able to access date editing
    }

    /**
     * Testing a valid deadline edit with teacher permissions, result should be the deadline is saved and redirect to the project details page
     * @throws Exception
     */
    @Test
    public void validDeadlineEditAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getDeadlineById(0)).thenReturn(testDeadline);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/edit-date").params(validParamsDeadline))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(projectItemService).saveDeadlineEdit(refEq(validDeadline)); // Verifies deadline was saved with correct details
    }

    /**
     * Testing a valid deadline edit with course admin permissions, result should be the edit is saved and a redirect to the project details page
     * @throws Exception
     */
    @Test
    public void validDeadlineEditAsAdmin() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getDeadlineById(0)).thenReturn(testDeadline);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/edit-date").params(validParamsDeadline))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(projectItemService).saveDeadlineEdit(refEq(validDeadline)); // Verifies deadline was saved with correct details
    }

    /**
     * Testing a deadline edit where the new date is before the start of the project, result should be a redirect back to the edit page
     * @throws Exception
     */
    @Test
    public void deadlineEditBeforeProjectAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getDeadlineById(0)).thenReturn(testDeadline);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/edit-date").params(deadlineBeforeProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:edit-date?id=0&ids=0")); // Redirected to edit dates page
        verify(projectItemService, never()).saveDeadlineEdit(any(Deadline.class)); // Verifies deadline was not saved
    }

    /**
     * Testing a deadline edit where the new date is after the end of the project, result should be a redirect back to the edit page.
     * @throws Exception
     */
    @Test
    public void deadlineEditAfterProjectAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getDeadlineById(0)).thenReturn(testDeadline);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/edit-date").params(deadlineAfterProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:edit-date?id=0&ids=0")); // Redirected to edit dates page
        verify(projectItemService, never()).saveDeadlineEdit(any(Deadline.class)); // Verifies dealdine was not saved
    }

    /**
     * Testing a valid deadline edit as a student, result should be no changes are saved.
     * @throws Exception
     */
    @Test
    public void validDeadlinesEditAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getDeadlineById(0)).thenReturn(testDeadline);
        mockMvc.perform(post("/edit-date").params(validParamsDeadline))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(projectItemService, never()).saveDeadlineEdit(any(Deadline.class)); // Verifies deadline was not saved
    }

    /**
     * Testing if a course admin can load the edit dates page with a event, result should be the edit dates page loads
     * @throws Exception
     */
    @Test
    public void getEditDatesForEventWithAdminCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getId(validAuthStateAdmin)).thenReturn(1);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
        // Declaring mockito when conditions
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getProjectItemByIdAndType("Event", 0)).thenReturn(validEvent);
        // Executing the mocked get request, checking that the page is displayed
        mockMvc.perform(get("/edit-date").param("projectId", String.valueOf(0)).param("dateId", String.valueOf(0)).param("itemType", "Event"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("editDates")).andExpect(model().attribute("type", "Event"));
    }

    /**
     * Testing if a teacher can load the date editing page with a event, result should be a redirect to the editing page.
     * @throws Exception
     */
    @Test
    public void getEditDatesForEventWithTeacherCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Declaring mockito when conditions
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(1);
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getProjectItemByIdAndType("Event", 0)).thenReturn(validEvent);
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        // Executing the mocked get request, checking that the page is displayed
        mockMvc.perform(get("/edit-date").param("projectId", String.valueOf(0)).param("dateId", String.valueOf(0)).param("itemType", "Event"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("editDates"));
    }

    /**
     * Testing if a student can load the date editing page for event, result should be a redirect to the error page instead
     * @throws Exception
     */
    @Test
    public void getEditDatesForEventWithStudentCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        // Declaring mockito when conditions
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getProjectItemByIdAndType("Event", 0)).thenReturn(validEvent);
        // Executing the mocked get request, checking that the page is displayed
        mockMvc.perform(get("/edit-date").param("projectId", String.valueOf(0)).param("dateId", String.valueOf(0)).param("itemType", "Event"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("error")); // Returns the error page as a student should not be able to access date editing
    }

    /**
     * Testing a valid event edit with teacher permissions, result should be the event is saved and redirect to the project details page
     * @throws Exception
     */
    @Test
    public void validEventEditAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getEventById(0)).thenReturn(validEvent);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/edit-date").params(validParamsEvent))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(projectItemService).saveEventEdit(refEq(validEvent)); // Verifies event was saved with correct details
    }

    /**
     * Testing a valid event edit with course admin permissions, result should be the edit is saved and a redirect to the project details page
     * @throws Exception
     */
    @Test
    public void validEventEditAsAdmin() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getEventById(0)).thenReturn(validEvent);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/edit-date").params(validParamsEvent))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(projectItemService).saveEventEdit(refEq(validEvent)); // Verifies event was saved with correct details
    }

    /**
     * Testing an event edit where the new date is before the start of the project, result should be a redirect back to the edit page
     * @throws Exception
     */
    @Test
    public void eventEditBeforeProjectAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getEventById(0)).thenReturn(validEvent);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/edit-date").params(deadlineBeforeProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:edit-date?id=0&ids=0")); // Redirected to edit dates page
        verify(projectItemService, never()).saveEventEdit(any(Event.class)); // Verifies event was not saved
    }

    /**
     * Testing an event edit where the new date is after the end of the project, result should be a redirect back to the edit page.
     * @throws Exception
     */
    @Test
    public void eventEditAfterProjectAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getEventById(0)).thenReturn(validEvent);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/edit-date").params(eventAfterProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:edit-date?projectId=0&dateId=0&itemType=Event")); // Redirected to edit dates page
        verify(projectItemService, never()).saveEventEdit(any(Event.class)); // Verifies event was not saved
    }

    /**
     * Testing an event edit where the end date is before the start date of the event, result should be a redirect back to the edit page.
     * @throws Exception
     */
    @Test
    public void eventEditStartDateAfterEndDateAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getEventById(0)).thenReturn(validEvent);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/edit-date").params(eventStartDateAfterEndDate))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:edit-date?projectId=0&dateId=0&itemType=Event")); // Redirected to edit dates page
        verify(projectItemService, never()).saveEventEdit(any(Event.class)); // Verifies event was not saved
    }

    /**
     * Testing a valid event edit as a student, result should be no changes are saved.
     * @throws Exception
     */
    @Test
    public void validEventEditAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(projectItemService.getEventById(0)).thenReturn(validEvent);
        mockMvc.perform(post("/edit-date").params(validParamsEvent))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(projectItemService, never()).saveEventEdit(any(Event.class)); // Verifies event was not saved
    }
}
