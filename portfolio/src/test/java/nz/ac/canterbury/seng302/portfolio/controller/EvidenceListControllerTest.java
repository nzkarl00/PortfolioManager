package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepoRepository;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.validAuthStateTeacher;
import static nz.ac.canterbury.seng302.portfolio.common.CommonProjectItems.getValidProject;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EvidenceListController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EvidenceListControllerTest {

    private static final Project testProject = getValidProject();
    private static final LocalDate may4 = LocalDate.parse("2022-05-04");
    private static final Evidence testEvidence = new Evidence(
        123456,
        testProject,
        "Evidence One",
        "This evidence is the first to be submitted",
        may4,
    Evidence.SERVICE
    );

    private static final Evidence testEvidenceAllCategories = new Evidence(
        123456,
            testProject,
            "Evidence One",
            "This evidence is the first to be submitted",
            may4,
            Evidence.SERVICE + Evidence.QUALITATIVE_SKILLS + Evidence.QUANTITATIVE_SKILLS
    );

    private static final SkillTag testSkillTag = new SkillTag(testProject, "SkillA");
    private static final SkillTag testSkillTagDuplicate = new SkillTag(testProject, "SKILLA");
    private static final EvidenceTag testEvidenceTag = new EvidenceTag(testSkillTag, testEvidence);
    private static final EvidenceTag testEvidenceTagDuplicate = new EvidenceTag(testSkillTagDuplicate, testEvidence);
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EvidenceRepository evidenceRepository;
    @MockBean
    private EvidenceTagRepository evidenceTagRepository;
    @MockBean
    private SkillTagRepository skillRepository;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private AccountClientService accountClientService;
    @MockBean
    private NavController navController;
    @MockBean
    private EvidenceService evidenceService;
    @MockBean
    private SprintService sprintService;
    @MockBean
    private WebLinkRepository webLinkRepository;
    @MockBean
    private EvidenceUserRepository evidenceUserRepository;
    @MockBean
    private GroupsClientService groupsClientService;
    @MockBean
    private GroupRepoRepository groupRepoRepository;
    @MockBean
    private GitlabClient gitlabClient;
    @MockBean
    private LinkedCommitRepository linkedCommitRepository;
    @MockBean
    private HighFiveRepository highFiveRepository;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(EvidenceListController.class)
                .build();

    }

    // setting up and closing the mocked static authStateInformer
    static MockedStatic<AuthStateInformer> utilities;
    static MockedStatic<EvidenceService> listReader;
    private static final MultiValueMap<String, String> validParamsEvidenceRequired = new LinkedMultiValueMap<>();
    private static final MultiValueMap<String, String> validParamsNoSkill = new LinkedMultiValueMap<>();
    private static final MultiValueMap<String, String> validParamsMultipleSkills = new LinkedMultiValueMap<>();
    private static final MultiValueMap<String, String> validParamsAllCategories = new LinkedMultiValueMap<>();
    private static final MultiValueMap<String, String> InvalidParamsEvidenceRequired = new LinkedMultiValueMap<>();
    private static final MultiValueMap<String, String> InvalidParamsEvidenceDate = new LinkedMultiValueMap<>();
    @BeforeAll
    public static void open() {

        utilities = Mockito.mockStatic(AuthStateInformer.class );
        listReader = Mockito.mockStatic(EvidenceService.class);
        validParamsEvidenceRequired.add("titleInput","Evidence One" );
        validParamsEvidenceRequired.add("dateInput", "2022-05-04");
        validParamsEvidenceRequired.add("projectId", String.valueOf(testProject.getId()));
        validParamsEvidenceRequired.add("descriptionInput", "This evidence is the first to be submitted");
        validParamsEvidenceRequired.add("categoryInput", "Service");
        validParamsEvidenceRequired.add("userID", "1");
        validParamsEvidenceRequired.add("skillInput", "SkillA~SkillB~SkillC");
        validParamsEvidenceRequired.add("userInput", "123456:Timmy Little");

        validParamsAllCategories.add("titleInput","Evidence One" );
        validParamsAllCategories.add("dateInput", "2022-05-04");
        validParamsAllCategories.add("projectId", String.valueOf(testProject.getId()));
        validParamsAllCategories.add("descriptionInput", "This evidence is the first to be submitted");
        validParamsAllCategories.add("categoryInput", "Service~Quantitative Skills~Qualitative Skills");
        validParamsAllCategories.add("userID", "1");
        validParamsAllCategories.add("skillInput", "SkillA~SkillB~SkillC");
        validParamsAllCategories.add("userInput", "123456:Timmy Little");

        validParamsNoSkill.add("titleInput","Evidence One" );
        validParamsNoSkill.add("dateInput", "2022-05-04");
        validParamsNoSkill.add("projectId", String.valueOf(testProject.getId()));
        validParamsNoSkill.add("descriptionInput", "This evidence is the first to be submitted");
        validParamsNoSkill.add("userID", "1");
        validParamsNoSkill.add("categoryInput", "Service");
        validParamsNoSkill.add("skillInput", "SkillA~SkillB~SkillC");
        validParamsNoSkill.add("userInput", "123456:Timmy Little");

        validParamsMultipleSkills.add("titleInput","Evidence One" );
        validParamsMultipleSkills.add("dateInput", "2022-05-04");
        validParamsMultipleSkills.add("projectId", String.valueOf(testProject.getId()));
        validParamsMultipleSkills.add("userID", "1");
        validParamsMultipleSkills.add("descriptionInput", "This evidence is the first to be submitted");
        validParamsMultipleSkills.add("categoryInput", "Service");
        validParamsMultipleSkills.add("skillInput", "SkillA~SKILLA");
        validParamsMultipleSkills.add("userInput", "123456:Timmy Little");

        InvalidParamsEvidenceRequired.add("titleInput","" );
        InvalidParamsEvidenceRequired.add("dateInput", "2022-05-04");
        InvalidParamsEvidenceRequired.add("projectId", String.valueOf(testProject.getId()));
        InvalidParamsEvidenceRequired.add("userID", "1");
        InvalidParamsEvidenceRequired.add("descriptionInput", "This evidence is the first to be submitted");
        InvalidParamsEvidenceRequired.add("categoryInput", "Service");
        InvalidParamsEvidenceRequired.add("skillInput", "SkillA~SkillB~SkillC");
        InvalidParamsEvidenceRequired.add("userInput", "123456:Timmy Little");

        InvalidParamsEvidenceDate.add("titleInput","" );
        InvalidParamsEvidenceDate.add("dateInput", "2000-10-22");
        InvalidParamsEvidenceDate.add("projectId", String.valueOf(testProject.getId()));
        InvalidParamsEvidenceDate.add("descriptionInput", "This evidence is the first to be submitted");
        InvalidParamsEvidenceDate.add("categoryInput", "Service");
        InvalidParamsEvidenceDate.add("userID", "1");
        InvalidParamsEvidenceDate.add("skillInput", "SkillA~SkillB~SkillC");
        InvalidParamsEvidenceDate.add("userInput", "123456:Timmy Little");
    }

    @AfterAll
    public static void close() {
        utilities.close();
    }


    /**
     * Tests to see if a valid evidence can be created. All variables are passed in and then
     * the params get checks inside the post controller. The test verifies if the evidence created is the one saved
     * in the evidence repo
     * @throws Exception
     */
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
        listReader.when(() -> EvidenceService.extractListFromHTMLStringWithTilda("123456:Timmy Little")).thenReturn(new ArrayList<>(List.of("123456:Timmy Little")));
        when(evidenceService.generateEvidenceForUsers(new ArrayList<>(List.of("123456:Timmy Little")), testProject, "Evidence One", "This evidence is the first to be submitted", may4, 4)).thenReturn(new ArrayList<>(List.of(testEvidence)));


        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-evidence").params(validParamsEvidenceRequired))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:evidence?pi=0&ui=0")); // Redirected to add dates page
        //verify(evidencerepository, times(1)).save(Mockito.any(Evidence.class)); // Verifies evidence was saved
        verify(evidenceService).addSkillsToRepo(Mockito.any(Project.class), refEq(testEvidence), Mockito.any(String.class));
    }

    /**
     * Tests to see if a valid evidence can be created. All variables are passed in along with all categories and then
     * the params get checks inside the post controller. The test verifies if the evidence created is the one saved
     * in the evidence repo
     * @throws Exception
     */
    @Test
    public void postValidEvidenceAllCategories() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        listReader.when(() -> EvidenceService.extractListFromHTMLStringWithTilda("123456:Timmy Little")).thenReturn(new ArrayList<>(List.of("123456:Timmy Little")));
        when(evidenceService.generateEvidenceForUsers(new ArrayList<>(List.of("123456:Timmy Little")), testProject, "Evidence One", "This evidence is the first to be submitted", may4, 7)).thenReturn(new ArrayList<>(List.of(testEvidenceAllCategories)));

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-evidence").params(validParamsAllCategories))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:evidence?pi=0&ui=0")); // Redirected to add dates page
        // Verifies evidence was saved
        verify(evidenceService).addSkillsToRepo(Mockito.any(Project.class), refEq(testEvidenceAllCategories), Mockito.any(String.class));
    }

    /**
     * Tests to see if a valid evidence can be created with no skills. Only the required variables are passed in and then
     * the params get checks inside the post controller. The test verifies if the evidence created is the one saved
     * in the evidence repo
     * @throws Exception
     */
    @Test
    public void postValidEvidenceNoSkills() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        listReader.when(() -> EvidenceService.extractListFromHTMLStringWithTilda("123456:Timmy Little")).thenReturn(new ArrayList<>(List.of("123456:Timmy Little")));
        when(evidenceService.generateEvidenceForUsers(new ArrayList<>(List.of("123456:Timmy Little")), testProject, "Evidence One", "This evidence is the first to be submitted", may4, 4)).thenReturn(new ArrayList<>(List.of(testEvidence)));

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-evidence").params(validParamsNoSkill))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:evidence?pi=0&ui=0")); // Redirected to add dates page
        //verify(evidencerepository, times(1)).save(Mockito.any(Evidence.class)); // Verifies evidence was saved

        verify(evidenceTagRepository, times(1)).save(Mockito.any(EvidenceTag.class));
    }

    /**
     * Tests to see if an invalid evidence can not be created. Only the required variables (with errors) are passed in and then
     * the params get checks inside the post controller. The test verifies if the evidence created is not saved
     * in the evidence repo
     * @throws Exception
     */
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
                .andExpect(view().name("redirect:evidence?pi=0&ui=0")); // Redirected to add dates page
        verify(evidenceRepository, never()).save(Mockito.any(Evidence.class)); // Verifies evidence was not saved

    }

    /**
     * Tests to see if an invalid evidence can not be created. Only the required variables
     * (with date error - date is outside project dates) are passed in and then
     * the params get checks inside the post controller. The test verifies if the evidence created is not saved
     * in the evidence repo
     * @throws Exception
     */
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
                .andExpect(view().name("redirect:evidence?pi=0&ui=0")); // Redirected to add dates page
        verify(evidenceRepository, never()).save(Mockito.any(Evidence.class)); // Verifies evidence was not saved

    }

    /**
     * Tests to I can add multiple skill tags
     * @throws Exception
     */
    @Test
    public void postValidEvidence_WithSkillTags() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        when(projectService.getProjectById(0)).thenReturn(testProject);
        listReader.when(() -> EvidenceService.extractListFromHTMLStringWithTilda("123456:Timmy Little")).thenReturn(new ArrayList<>(List.of("123456:Timmy Little")));
        when(evidenceService.generateEvidenceForUsers(new ArrayList<>(List.of("123456:Timmy Little")), testProject, "Evidence One", "This evidence is the first to be submitted", may4, 4)).thenReturn(new ArrayList<>(List.of(testEvidence)));


        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-evidence").params(validParamsNoSkill))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:evidence?pi=0&ui=0")); // Redirected to add dates page
        verify(evidenceTagRepository, times(1)).save(Mockito.any(EvidenceTag.class));
    }

    /**
     * Tests to I can add multiple skill tags but not duplicated
     * @throws Exception
     */
    @Test
    public void postValidEvidence_WithMultipleSkillTags() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");
        List<EvidenceTag> evidenceTagList = new ArrayList<>();
        evidenceTagList.add(testEvidenceTag);
        when(projectService.getProjectById(0)).thenReturn(testProject);
        when(evidenceService.generateEvidenceForUsers(new ArrayList<>(List.of("123456:Timmy Little")), testProject, "Evidence One", "This evidence is the first to be submitted", may4, 4)).thenReturn(new ArrayList<>(List.of(testEvidence)));

        when(evidenceTagRepository.findAllByParentEvidenceId(0)).thenReturn(evidenceTagList);

        // Executing the mocked post request, checking that the page is displayed
        mockMvc.perform(post("/add-evidence").params(validParamsMultipleSkills))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:evidence?pi=0&ui=0")); // Redirected to add dates page

        verify(evidenceTagRepository, atMostOnce()).save(refEq(testEvidenceTag));
        verify(evidenceTagRepository, never()).save(refEq(testEvidenceTagDuplicate));
    }

}
