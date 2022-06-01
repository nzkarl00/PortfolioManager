package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import io.cucumber.java.Before;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
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

import java.util.Date;
import java.util.Map;

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
        private AuthState validAuthState = AuthState.newBuilder()
                .setIsAuthenticated(true)
                .setNameClaimType("name")
                .setRoleClaimType("role")
                .addClaims(ClaimDTO.newBuilder().setType("role").setValue("ADMIN").build()) // Set the mock user's role
                .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
                .setAuthenticationType("AuthenticationTypes.Federation")
                .setName("validtesttoken")
                .build();

        private AuthState validAuthStateTeacher = AuthState.newBuilder()
                .setIsAuthenticated(true)
                .setNameClaimType("name")
                .setRoleClaimType("role")
                .addClaims(ClaimDTO.newBuilder().setType("role").setValue("TEACHER").build()) // Set the mock user's role
                .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
                .setAuthenticationType("AuthenticationTypes.Federation")
                .setName("validtesttoken")
                .build();

        private AuthState validAuthStateStudent = AuthState.newBuilder()
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


        private static final Date april1 = DateParser.stringToDate("22-04-01");
        private static final Date may1 = DateParser.stringToDate("22-05-01");
        private static final Date june1 = DateParser.stringToDate("22-06-01");
        private static final Date july1 = DateParser.stringToDate("22-07-01");

        private static final Sprint validSprint = new Sprint(1, "test", "test", "test", may1, june1);
        private static final Sprint startsBeforeProjectSprint = new Sprint(1, "test", "test", "test", april1, june1);
        private static final Sprint endsAfterProjectSprint = new Sprint(1, "test", "test", "test", may1, july1);


    private static MultiValueMap<String, String> validParams = new LinkedMultiValueMap<String, String>();
        private static MultiValueMap<String, String> startsBeforeProjectParams = new LinkedMultiValueMap<String, String>();
        private static MultiValueMap<String, String> endsAfterProjectParams = new LinkedMultiValueMap<String, String>();

    private final Project testProject = new Project("testName", "testDescription", may1, june1);

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
            validParams.add("projectId", String.valueOf(1));
            validParams.add("eventName", "test");
            validParams.add("eventStartDate", "22-05-01");
            validParams.add("eventEndDate", "22-06-01");
            validParams.add("eventDescription", "test");
            startsBeforeProjectParams.add("projectId", String.valueOf(1));
            startsBeforeProjectParams.add("eventName", "test");
            startsBeforeProjectParams.add("eventStartDate", "22-04-01");
            startsBeforeProjectParams.add("eventEndDate", "22-06-01");
            startsBeforeProjectParams.add("eventDescription", "test");
            endsAfterProjectParams.add("projectId", String.valueOf(1));
            endsAfterProjectParams.add("eventName", "test");
            endsAfterProjectParams.add("eventStartDate", "22-05-01");
            endsAfterProjectParams.add("eventEndDate", "22-07-01");
            endsAfterProjectParams.add("eventDescription", "test");

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
                    .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));
            // Configuring Spring to use the mocked SecurityContext
            SecurityContextHolder.setContext(mockedSecurityContext);
            utilities.when(() -> AuthStateInformer.getId(validAuthState)).thenReturn(1);
            utilities.when(() -> AuthStateInformer.getRole(validAuthState)).thenReturn("admin");
            // Declaring mockito when conditions
            when(accountClientService.getUserById(1)).thenReturn(testUser);
            when(projectService.getProjectById(1)).thenReturn(testProject);
            // Executing the mocked get request, checking that the page is displayed
            mockMvc.perform(get("/add-dates").param("projectId", String.valueOf(1)))
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
            when(projectService.getProjectById(1)).thenReturn(testProject);
            // Configuring Spring to use the mocked SecurityContext
            SecurityContextHolder.setContext(mockedSecurityContext);
            // Executing the mocked get request, checking that the page is displayed
            mockMvc.perform(get("/add-dates").param("projectId", String.valueOf(1)))
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
            when(projectService.getProjectById(1)).thenReturn(testProject);
            // Executing the mocked get request, checking that the page is displayed
            mockMvc.perform(get("/add-dates").param("projectId", String.valueOf(1)))
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
            when(projectService.getProjectById(1)).thenReturn(testProject);
            when(sprintService.areNewSprintDatesValid(may1, june1, 1)).thenReturn(true);
            // Executing the mocked post request, checking that the page is displayed
            mockMvc.perform(post("/add-dates").params(validParams))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(view().name("redirect:details?id=" + 1)); // Redirected to details page
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
        when(projectService.getProjectById(1)).thenReturn(testProject);
        when(sprintService.areNewSprintDatesValid(april1, june1, 1)).thenReturn(true);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(startsBeforeProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:addDates?projectId=" + 1)); // Redirected to add dates page
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
        when(projectService.getProjectById(1)).thenReturn(testProject);
        when(sprintService.areNewSprintDatesValid(may1, july1, 1)).thenReturn(true);
        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-dates").params(endsAfterProjectParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:addDates?projectId=" + 1)); // Redirected to add dates page
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
        when(sprintService.areNewSprintDatesValid(may1, june1, 1)).thenReturn(true);
        when(projectService.getProjectById(1)).thenReturn(testProject);
        mockMvc.perform(post("/add-dates").params(validParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:details?id=" + 1)); // Redirected to details page
        verify(sprintRepo, never()).save(any(Sprint.class)); // Verifies sprint was not saved
    }
}
