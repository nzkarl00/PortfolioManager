package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceTagRepository;
import nz.ac.canterbury.seng302.portfolio.model.evidence.SkillTagRepository;
import nz.ac.canterbury.seng302.portfolio.service.*;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.*;
import static nz.ac.canterbury.seng302.portfolio.common.CommonProjectItems.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EvidenceListController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EvidenceListControllerTest {

    private static final Project testProject = getValidProject();
    private static final LocalDate may4 = LocalDate.parse("2022-05-04");
    private static final Evidence testEvidence = new Evidence(0, testProject, "Evidence One", "This evidence is the first to be submitted", may4);



    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvidenceRepository evidenceRepository;
    @MockBean
    private EvidenceTagRepository evidencetagrepository;
    @MockBean
    private SkillTagRepository skillRepository;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private AccountClientService accountClientService;
    @MockBean
    private NavController navController;


    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(EvidenceListController.class)
                .build();

    }

    // setting up and closing the mocked static authStateInformer
    static MockedStatic<AuthStateInformer> utilities;
    private static MultiValueMap<String, String> validParamsEvidenceRequired = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> InvalidParamsEvidenceRequired = new LinkedMultiValueMap<>();
    private static MultiValueMap<String, String> InvalidParamsEvidenceDate = new LinkedMultiValueMap<>();
    @BeforeAll
    public static void open() {

        utilities = Mockito.mockStatic(AuthStateInformer.class );
        validParamsEvidenceRequired.add("titleInput","Evidence One" );
        validParamsEvidenceRequired.add("dateInput", "2022-05-04");
        validParamsEvidenceRequired.add("projectId", String.valueOf(testProject.getId()));
        validParamsEvidenceRequired.add("descriptionInput", "This evidence is the first to be submitted");

        InvalidParamsEvidenceRequired.add("titleInput","" );
        InvalidParamsEvidenceRequired.add("dateInput", "2022-05-04");
        InvalidParamsEvidenceRequired.add("projectId", String.valueOf(testProject.getId()));
        InvalidParamsEvidenceRequired.add("descriptionInput", "This evidence is the first to be submitted");

        InvalidParamsEvidenceDate.add("titleInput","" );
        InvalidParamsEvidenceDate.add("dateInput", "2000-10-22");
        InvalidParamsEvidenceDate.add("projectId", String.valueOf(testProject.getId()));
        InvalidParamsEvidenceDate.add("descriptionInput", "This evidence is the first to be submitted");
    }

    @AfterAll
    public static void close() {
        utilities.close();
    }


    /**
     * Tests to see if a valid evidence can be created. Only the required variables are passed in and then
     * the params get checks inside the post controller. The test verifies if the evidence created is the one saved
     * in the evidence repo
     * @throws Exception
     *
    @Test
    public void postValidEvidence() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");

        when(projectService.getProjectById(0)).thenReturn(testProject);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-evidence").params(validParamsEvidenceRequired))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:evidence?pi=" + 0)); // Redirected to add dates page
        //verify(evidencerepository, times(1)).save(Mockito.any(Evidence.class)); // Verifies evidence was saved
        verify(evidenceRepository).save(refEq(testEvidence));
    }

    /**
     * Tests to see if an invalid evidence can not be created. Only the required variables (with errors) are passed in and then
     * the params get checks inside the post controller. The test verifies if the evidence created is not saved
     * in the evidence repo
     * @throws Exception
     *
    @Test
    public void postInValidEvidence_RequiredError() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");

        when(projectService.getProjectById(0)).thenReturn(testProject);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-evidence").params(InvalidParamsEvidenceRequired))
                .andExpect(view().name("redirect:evidence?pi=" + 0)); // Redirected to add dates page
        verify(evidenceRepository, never()).save(Mockito.any(Evidence.class)); // Verifies evidence was not saved

    }

    /**
     * Tests to see if an invalid evidence can not be created. Only the required variables
     * (with date error - date is outside project dates) are passed in and then
     * the params get checks inside the post controller. The test verifies if the evidence created is not saved
     * in the evidence repo
     * @throws Exception
     *
    @Test
    public void postInValidEvidence_DateError() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");

        when(projectService.getProjectById(0)).thenReturn(testProject);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-evidence").params(InvalidParamsEvidenceDate))
                .andExpect(view().name("redirect:evidence?pi=" + 0)); // Redirected to add dates page
        verify(evidenceRepository, never()).save(Mockito.any(Evidence.class)); // Verifies evidence was not saved

    }

    */

}
