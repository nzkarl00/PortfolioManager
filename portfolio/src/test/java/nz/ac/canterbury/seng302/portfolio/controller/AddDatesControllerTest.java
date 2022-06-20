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
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AddDatesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AddDatesControllerTest {

    private static final Date april1 = DateParser.stringToDate("22-04-01");
    private static final Date may1 = DateParser.stringToDate("22-05-01");
    private static final Date june1 = DateParser.stringToDate("22-06-01");
    private static final Date july1 = DateParser.stringToDate("22-07-01");
    private static final Date eventMay1 = DateParser.stringToDate("2022-05-01");
    private static final Date eventJune1 = DateParser.stringToDate("2022-06-01");

    private static final Sprint validSprint = new Sprint(0, "test", "test", "test", may1, june1);
    private static MultiValueMap<String, String> validParams = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> validParamsDeadline = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> validParamsMilestone = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> validParamsEvent = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> deadlineStartsBeforeProjectParams = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> deadlineEndsAfterProjectParams = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> milestoneStartsBeforeProjectParams = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> milestoneEndsAfterProjectParams = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> eventStartsBeforeProjectParams = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> eventEndsAfterProjectParams = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> startsBeforeProjectParams = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> endsAfterProjectParams = new LinkedMultiValueMap<>();

    private final Project testProject = new Project("testName", "testDescription", may1, june1);
    private final Project testProjectForEvents = new Project("testName", "testDescription", eventMay1, eventJune1);

    private static final LocalDateTime may4 = DateParser.stringToLocalDateTime("2022-05-04", "16:20");
    private static final LocalDateTime milestoneMay4 = DateParser.stringToLocalDateTime("2022-05-04", "");

    private final Deadline validDeadline = new Deadline(testProjectForEvents, "Deadline 1", "This is a deadline for project 1", may4);
    private final Milestone validMilestone = new Milestone(testProjectForEvents, "Milestone 1", "This is a milestone for project 1", milestoneMay4);
    private final Event validEvent = new Event(testProjectForEvents, "Event 1", "This is a Event for project 1", milestoneMay4, milestoneMay4);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    SimpMessagingTemplate template;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    SprintRepository sprintRepo;

    @MockBean
    EventRepository eventRepo;

    @MockBean
    DeadlineRepository deadlineRepo;

    @MockBean
    MilestoneRepository milestoneRepo;

    @MockBean
    ProjectService projectService;

    @MockBean
    SprintService sprintService;

    @MockBean
    NavController navController;

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
        validParams.add("projectId", String.valueOf(0));
        validParams.add("eventName", "test");
        validParams.add("eventStartDate", "22-05-01");
        validParams.add("eventEndDate", "22-06-01");
        validParams.add("eventDescription", "test");

        validParamsDeadline.add("projectId", String.valueOf(0));
        validParamsDeadline.add("eventName", "Deadline 1");
        validParamsDeadline.add("eventType", "Deadline");
        validParamsDeadline.add("eventStartDate", "2022-05-04");
        validParamsDeadline.add("eventEndDate", "16:20");
        validParamsDeadline.add("eventDescription", "This is a deadline for project 1");

        validParamsMilestone.add("projectId", String.valueOf(0));
        validParamsMilestone.add("eventName", "Milestone 1");
        validParamsMilestone.add("eventType", "Milestone");
        validParamsMilestone.add("eventStartDate", "2022-05-04");
        validParamsMilestone.add("eventEndDate", "");
        validParamsMilestone.add("eventDescription", "This is a milestone for project 1");

        validParamsEvent.add("projectId", String.valueOf(0));
        validParamsEvent.add("eventName", "Event 1");
        validParamsEvent.add("eventType", "Event");
        validParamsEvent.add("eventStartDate", "2022-05-04");
        validParamsEvent.add("eventEndDate", "2022-05-04");
        validParamsEvent.add("eventDescription", "This is a Event for project 1");

        startsBeforeProjectParams.add("projectId", String.valueOf(0));
        startsBeforeProjectParams.add("eventName", "test");
        startsBeforeProjectParams.add("eventStartDate", "22-04-01");
        startsBeforeProjectParams.add("eventEndDate", "22-06-01");
        startsBeforeProjectParams.add("eventDescription", "test");
        endsAfterProjectParams.add("projectId", String.valueOf(0));
        endsAfterProjectParams.add("eventName", "test");
        endsAfterProjectParams.add("eventStartDate", "22-05-01");
        endsAfterProjectParams.add("eventEndDate", "22-07-01");
        endsAfterProjectParams.add("eventDescription", "test");

        deadlineStartsBeforeProjectParams.add("projectId", String.valueOf(0));
        deadlineStartsBeforeProjectParams.add("eventName", "Deadline 1");
        deadlineStartsBeforeProjectParams.add("eventType", "Deadline");
        deadlineStartsBeforeProjectParams.add("eventStartDate", "2022-04-01");
        deadlineStartsBeforeProjectParams.add("eventEndDate", "16:20");
        deadlineStartsBeforeProjectParams.add("eventDescription", "This is a deadline for project 1");
        deadlineEndsAfterProjectParams.add("projectId", String.valueOf(0));
        deadlineEndsAfterProjectParams.add("eventName", "Deadline 1");
        deadlineEndsAfterProjectParams.add("eventType", "Deadline");
        deadlineEndsAfterProjectParams.add("eventStartDate", "2022-07-01");
        deadlineEndsAfterProjectParams.add("eventEndDate", "16:20");
        deadlineEndsAfterProjectParams.add("eventDescription", "This is a deadline for project 1");

        milestoneStartsBeforeProjectParams.add("projectId", String.valueOf(0));
        milestoneStartsBeforeProjectParams.add("eventName", "Milestone 1");
        milestoneStartsBeforeProjectParams.add("eventType", "Milestone");
        milestoneStartsBeforeProjectParams.add("eventStartDate", "2022-04-01");
        milestoneStartsBeforeProjectParams.add("eventEndDate", "");
        milestoneStartsBeforeProjectParams.add("eventDescription", "This is a milestone for project 1");
        milestoneEndsAfterProjectParams.add("projectId", String.valueOf(0));
        milestoneEndsAfterProjectParams.add("eventName", "Milestone 1");
        milestoneEndsAfterProjectParams.add("eventType", "Milestone");
        milestoneEndsAfterProjectParams.add("eventStartDate", "2022-07-01");
        milestoneEndsAfterProjectParams.add("eventEndDate", "");
        milestoneEndsAfterProjectParams.add("eventDescription", "This is a milestone for project 1");

        eventStartsBeforeProjectParams.add("projectId", String.valueOf(0));
        eventStartsBeforeProjectParams.add("eventName", "Event 1");
        eventStartsBeforeProjectParams.add("eventType", "Event");
        eventStartsBeforeProjectParams.add("eventStartDate", "2022-04-01");
        eventStartsBeforeProjectParams.add("eventEndDate", "2022-04-01");
        eventStartsBeforeProjectParams.add("eventDescription", "This is a Event for project 1");
        eventEndsAfterProjectParams.add("projectId", String.valueOf(0));
        eventEndsAfterProjectParams.add("eventName", "Event 1");
        eventEndsAfterProjectParams.add("eventType", "Event");
        eventEndsAfterProjectParams.add("eventStartDate", "2022-07-01");
        eventEndsAfterProjectParams.add("eventEndDate", "2022-07-01");
        eventEndsAfterProjectParams.add("eventDescription", "This is a Event for project 1");

    }

    @AfterAll
    public static void close() {
        utilities.close();
    }

        @Test
        public void getAddDatesWithAdminCredentials() throws Exception {
            //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
            SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
            when(mockedSecurityContext.getAuthentication())
                    .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));
            // Configuring Spring to use the mocked SecurityContext
            SecurityContextHolder.setContext(mockedSecurityContext);
            utilities.when(() -> AuthStateInformer.getId(validAuthStateAdmin)).thenReturn(1);
            utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
            // Declaring mockito when conditions
            when(accountClientService.getUserById(1)).thenReturn(testUserAdmin);
            when(projectService.getProjectById(0)).thenReturn(testProject);
            // Executing the mocked get request, checking that the page is displayed
            mockMvc.perform(get("/add-dates").param("projectId", String.valueOf(0)))
                    .andExpect(status().isOk()) // Whether to return the status "200 OK"
                    .andExpect(view().name("addDates"));
        }

    @Test
    public void getAddDatesWithTeacherCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Declaring mockito when conditions
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(1);
        when(projectService.getProjectById(0)).thenReturn(testProject);
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        // Executing the mocked get request, checking that the page is displayed
        mockMvc.perform(get("/add-dates").param("projectId", String.valueOf(0)))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("addDates"));
    }

    @Test
    public void getAddDatesWithStudentCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        // Declaring mockito when conditions
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        // Executing the mocked get request, checking that the page is displayed
        mockMvc.perform(get("/add-dates").param("projectId", String.valueOf(0)))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("userProjectDetails")); // Returns user project details page instead of add dates"
    }

    @Test
    public void postValidSprintAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(sprintService.areNewSprintDatesValid(may1, june1, 0)).thenReturn(true);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(validParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(sprintRepo).save(refEq(validSprint)); // Verifies sprint was saved with correct details
    }

    @Test
    public void postStartsBeforeProjectSprintAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(sprintService.areNewSprintDatesValid(april1, june1, 0)).thenReturn(true);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(startsBeforeProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add-dates?projectId=" + 0)); // Redirected to add dates page
        verify(sprintRepo, never()).save(any(Sprint.class)); // Verifies sprint was not saved
    }

    @Test
    public void postEndsAfterProjectSprintAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(sprintService.areNewSprintDatesValid(may1, july1, 0)).thenReturn(true);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(endsAfterProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add-dates?projectId=" + 0)); // Redirected to add dates page
        verify(sprintRepo, never()).save(any(Sprint.class)); // Verifies sprint was not saved
    }

    @Test
    public void postValidSprintAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");
        when(sprintService.areNewSprintDatesValid(may1, june1, 0)).thenReturn(true);
        when(projectService.getProjectById(0)).thenReturn(testProject);
        mockMvc.perform(post("/add-dates").params(validParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(sprintRepo, never()).save(any(Sprint.class)); // Verifies sprint was not saved
    }

    @Test
    public void postValidDeadlineAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(validParamsDeadline))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(deadlineRepo).save(refEq(validDeadline)); // Verifies deadline was saved with correct details
    }

    @Test
    public void postValidDeadlineAsAdmin() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(validParamsDeadline))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(deadlineRepo).save(refEq(validDeadline)); // Verifies deadline was saved with correct details
    }

    @Test
    public void postDeadlineStartsBeforeProjectAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(deadlineStartsBeforeProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add-dates?projectId=" + 0)); // Redirected to add dates page
        verify(deadlineRepo, never()).save(any(Deadline.class)); // Verifies deadline was not saved
    }

    @Test
    public void postDeadlineEndsAfterProjectAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(deadlineEndsAfterProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add-dates?projectId=" + 0)); // Redirected to add dates page
        verify(deadlineRepo, never()).save(any(Deadline.class)); // Verifies dealdine was not saved
    }

    @Test
    public void postValidDeadlinesAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);
        mockMvc.perform(post("/add-dates").params(validParamsDeadline))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(deadlineRepo, never()).save(any(Deadline.class)); // Verifies deadline was not saved
    }


    @Test
    public void postValidMilestoneAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);
        mockMvc.perform(post("/add-dates").params(validParamsMilestone))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(milestoneRepo, never()).save(any(Milestone.class)); // Verifies milestone was not saved
    }

    @Test
    public void postValidMilestoneAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(validParamsMilestone))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(milestoneRepo).save(refEq(validMilestone)); // Verifies milestone was saved with correct details
    }

    @Test
    public void postValidMilestoneAsAdmin() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(validParamsMilestone))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(milestoneRepo).save(refEq(validMilestone)); // Verifies milestone was saved with correct details
    }

    @Test
    public void postMilestoneStartsBeforeProjectAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(milestoneStartsBeforeProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add-dates?projectId=" + 0)); // Redirected to add dates page
        verify(milestoneRepo, never()).save(any(Milestone.class)); // Verifies deadline was not saved
    }

    @Test
    public void postMilestoneEndsAfterProjectAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(eventEndsAfterProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add-dates?projectId=" + 0)); // Redirected to add dates page
        verify(milestoneRepo, never()).save(any(Milestone.class)); // Verifies milestone was not saved

    }

    @Test
    public void postValidEventAsStudent() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);
        mockMvc.perform(post("/add-dates").params(validParamsEvent))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(eventRepo, never()).save(any(Event.class)); // Verifies milestone was not saved
    }

    @Test
    public void postValidEventAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(validParamsEvent))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(eventRepo).save(refEq(validEvent)); // Verifies Event was saved with correct details
    }

    @Test
    public void postValidEventAsAdmin() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(validParamsEvent))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:details?id=" + 0)); // Redirected to details page
        verify(eventRepo).save(refEq(validEvent)); // Verifies Event was saved with correct details
    }

    @Test
    public void postEventStartsBeforeProjectAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(eventStartsBeforeProjectParams))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:add-dates?projectId=" + 0)); // Redirected to add dates page
        verify(eventRepo, never()).save(any(Event.class)); // Verifies Event was not saved
    }

    @Test
    public void postEventEndsAfterProjectAsTeacher() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProjectForEvents);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(eventEndsAfterProjectParams))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:add-dates?projectId=" + 0)); // Redirected to add dates page
        verify(milestoneRepo, never()).save(any(Milestone.class)); // Verifies Event was not saved

    }
}