package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.text.ParseException;
import java.util.*;

import static nz.ac.canterbury.seng302.portfolio.common.CommonProjectItems.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EvidenceServiceTest {

    @MockBean
    EvidenceRepository evidenceRepository = mock(EvidenceRepository.class);
    @MockBean
    EvidenceTagRepository evidenceTagRepository = mock(EvidenceTagRepository.class);
    @MockBean
    ProjectService projectService = mock(ProjectService.class);
    @MockBean
    EvidenceUserRepository evidenceUserRepository = mock(EvidenceUserRepository.class);
    @MockBean
    WebLinkRepository webLinkRepository = mock(WebLinkRepository.class);
    @MockBean
    AccountClientService accountClientService = mock(AccountClientService.class);
    @MockBean
    GroupRepoRepository groupRepoRepository = mock(GroupRepoRepository.class);
    @MockBean
    GitlabClient gitlabClient = mock(GitlabClient.class);
    @MockBean
    SkillTagRepository skillTagRepository = mock(SkillTagRepository.class);

    @Autowired
    EvidenceService evidenceService = new EvidenceService();

    static SkillTag skillTagCSharp, skillTagC, skillTagCPlusPlus, skillTagCSS, skillTagReactNative;

    static List<SkillTag> testSkillTagList;

    @BeforeEach
    public void init() {
        evidenceService.evidenceRepository = evidenceRepository;
        evidenceService.evidenceTagRepository = evidenceTagRepository;
        evidenceService.projectService = projectService;
        evidenceService.evidenceUserRepository = evidenceUserRepository;
        evidenceService.webLinkRepository = webLinkRepository;
        evidenceService.gitlabClient = gitlabClient;
        evidenceService.groupRepoRepository = groupRepoRepository;
        evidenceService.accountClientService = accountClientService;
        evidenceService.skillTagRepository = skillTagRepository;
    }


    @BeforeAll
    static void setup() throws ParseException {
        skillTagCSharp = new SkillTag(new Project(), "C#");
        skillTagC = new SkillTag(new Project(), "C");
        skillTagCPlusPlus = new SkillTag(new Project(), "C++");
        skillTagCSS = new SkillTag(new Project(), "CSS");
        skillTagReactNative = new SkillTag(new Project(), "React_Native");
        testSkillTagList = new ArrayList<>();
        testSkillTagList.add(skillTagCSharp);
        testSkillTagList.add(skillTagCSharp);
        testSkillTagList.add(skillTagC);
        testSkillTagList.add(skillTagCPlusPlus);
        testSkillTagList.add(skillTagCSS);
        testSkillTagList.add(skillTagCSS);
        testSkillTagList.add(skillTagReactNative);
    }

    @Test
    void skillListContainsAllItems() {
        Mockito.when(skillTagRepository.findAll()).thenReturn(testSkillTagList);
        Set<String> skills = evidenceService.getAllUniqueSkills();
        assertTrue(skills.containsAll(List.of("C#", "C", "C++", "CSS")));
    }

    @Test
    void skillListHasNoDuplicates() {
        Mockito.when(skillTagRepository.findAll()).thenReturn(testSkillTagList);
        Set<String> skills = evidenceService.getAllUniqueSkills();
        assertEquals(5, skills.size());
    }

    /*
      When passing a duplicate skill tag the duplicated tag should not be used to save to the repo
      as tag already exists in the database
    */
    @Test
    void skillListHasDuplicatesCaseInsensitive(){
        Mockito.when(skillTagRepository.findByTitleIgnoreCase("CsS")).thenReturn(skillTagCSS);
        SkillTag skillFromRepo = skillTagRepository.findByTitleIgnoreCase("CsS");
        assertEquals(skillTagCSS, skillFromRepo);
    }

    /*
       When passing a duplicate skill tag the duplicated tag should not be used to save to the repo
       as there is already one but with space/underscore "React_Native"
    */
    @Test
    void skillListHasDuplicateWithUnderScore(){
        Mockito.when(skillTagRepository.findByTitleIgnoreCase("REACT_native")).thenReturn(skillTagReactNative);
        SkillTag skillFromRepo = skillTagRepository.findByTitleIgnoreCase("REACT_native");
        assertEquals(skillTagReactNative, skillFromRepo);
    }

    /*
     When passing a unique skill tag with space, check repo if it exists. Returns null as it is unique
    */
    @Test
    void skillListHasNoDuplicateWithUnderScore(){
        Mockito.when(skillTagRepository.findByTitleIgnoreCase("React_Native-Beta")).thenReturn(null);
        SkillTag skillFromRepo = skillTagRepository.findByTitleIgnoreCase("React_Native-Beta");
        assertNull(skillFromRepo);
    }

    /*
      When passing a unique skill tag, check repo if it exists. Returns null as it is unique
    */
    @Test
    void skillListHasNoDuplicate(){
        Mockito.when(skillTagRepository.findByTitleIgnoreCase("React_Native-Beta")).thenReturn(null);
        SkillTag skillFromRepo = skillTagRepository.findByTitleIgnoreCase("React_Native-Beta");
        assertNull(skillFromRepo);
    }

    @Test
    void deleteEvidenceWithNoSkills() {
        Evidence testEvidence = getValidEvidence();
        testEvidence.setEvidenceTags(List.of(getNoSkillsEvidenceTag(testEvidence)));
        evidenceService.deleteEvidence(testEvidence);
        Mockito.verifyNoInteractions(skillTagRepository);
    }

    @Test
    void deleteEvidenceWithOneSkill() {
        Evidence testEvidence = getValidEvidence();
        List<EvidenceTag> evidenceList = new ArrayList<>(List.of(getEvidenceTagA(testEvidence)));
        when(evidenceTagRepository.findAllByParentEvidenceId(testEvidence.getId())).thenReturn(evidenceList);
        evidenceService.deleteEvidence(testEvidence);
        verify(skillTagRepository, times(1)).delete(evidenceList.get(0).getParentSkillTag());
    }

    @Test
    void deleteEvidenceWithMultipleSkills() {
        Evidence testEvidence = getValidEvidence();
        List<EvidenceTag> evidenceList = new ArrayList<>(List.of(getEvidenceTagA(testEvidence), getEvidenceTagB(testEvidence), getEvidenceTagC(testEvidence)));
        when(evidenceTagRepository.findAllByParentEvidenceId(testEvidence.getId())).thenReturn(evidenceList);
        evidenceService.deleteEvidence(testEvidence);
        for (EvidenceTag evidenceTag: evidenceList) {
            verify(skillTagRepository, times(1)).delete(evidenceTag.getParentSkillTag());
        }
        verifyNoMoreInteractions(skillTagRepository);
    }

    @Test
    void deleteEvidenceWithOneSkillNotUnique() {
        Evidence testEvidence = getValidEvidence();
        testEvidence.setEvidenceTags(List.of(getEvidenceTagANotUnique(testEvidence)));
        evidenceService.deleteEvidence(testEvidence);
        verifyNoInteractions(skillTagRepository);
    }

    @Test
    void getSkillTagStringsByEvidenceIdNoSkills() {
        Evidence testEvidence = getValidEvidence();
        List<EvidenceTag> tags = new ArrayList<>(List.of(new EvidenceTag(getNoSkillsSkillTag(), testEvidence)));
        testEvidence.setEvidenceTags(tags);
        List<String> result = evidenceService.getSkillTagStringsByEvidenceId(testEvidence);
        Assertions.assertEquals(new ArrayList<>(List.of("No_skills")), result);
    }

    @Test
    void getSkillTagStringsByEvidenceIdBlueSky() {
        Evidence testEvidence = getValidEvidence();
        List<EvidenceTag> tags = new ArrayList<>(
            List.of(new EvidenceTag(getSkillTagA(), testEvidence)));
        tags.add(new EvidenceTag(getSkillTagB(), testEvidence));
        tags.add(new EvidenceTag(getSkillTagC(), testEvidence));
        testEvidence.setEvidenceTags(tags);
        List<String> result =
            evidenceService.getSkillTagStringsByEvidenceId(testEvidence);
        Assertions.assertEquals(new ArrayList<>(List.of("A", "B", "C")),
            result);
    }

    @Test
    void getSkillIgnoreUsersCase_perfectMatch() {
        List<SkillTag> skills = new ArrayList<>(List.of(getSkillTagA()));
        SkillTag actual = evidenceService.getSkillIgnoreUsersCase("A", skills);
        Assertions.assertEquals(getSkillTagA().getTitle(), actual.getTitle());
    }

    @Test
    void getSkillIgnoreUsersCase_letterMatch() {
        List<SkillTag> skills = new ArrayList<>(List.of(getSkillTagA()));
        SkillTag actual = evidenceService.getSkillIgnoreUsersCase("a", skills);
        Assertions.assertEquals(getSkillTagA().getTitle(), actual.getTitle());
    }

    /**
     * If there are multiple case-insensitive matches. It should return the first.
     */
    @Test
    void getSkillIgnoreUsersCase_multiplePossibleMatches_returnsFirstInList() {
        List<SkillTag> skills = new ArrayList<>(List.of(getSkillTagAlpha(), getSkillTagAlphaLowercase()));
        SkillTag actual = evidenceService.getSkillIgnoreUsersCase(
                "alpha",
                skills
            );
        Assertions.assertEquals(getSkillTagAlpha().getTitle(), actual.getTitle());

        SkillTag actualRepeated = evidenceService.getSkillIgnoreUsersCase(
                "Alpha",
                skills
        );
        Assertions.assertEquals(getSkillTagAlpha().getTitle(), actualRepeated.getTitle());
    }

    @Test
    void getSkillIgnoreUsersCase_noMatch() {
        List<SkillTag> skills = new ArrayList<>(List.of(getSkillTagA()));
        SkillTag actual = evidenceService.getSkillIgnoreUsersCase("b", skills);
        assertNull(actual);
    }

    @Test
    void getSkillIgnoreUsersCase_emptyList() {
        List<SkillTag> skills = List.of();
        SkillTag actual = evidenceService.getSkillIgnoreUsersCase("b", skills);
        assertNull(actual);
    }

    @Test
    void deleteLastSkillAndApplyNoSkillsTag() {
        Evidence testEvidence = getValidEvidence();
        SkillTag noSkillsTag = getNoSkillsSkillTag();
        testEvidence.setEvidenceTags(Collections.emptyList());
        when(evidenceRepository.findById(any(Integer.class))).thenReturn(Optional.of(testEvidence));
        when(skillTagRepository.findByTitle("No_skills")).thenReturn(noSkillsTag);
        when(evidenceTagRepository.findAllByParentEvidenceId(any(Integer.class))).thenReturn(Collections.emptyList());
        when(evidenceTagRepository.findByParentEvidenceIdAndParentSkillTagId(any(Integer.class), any(Integer.class)))
                .thenReturn(getEvidenceTagA(testEvidence));
        evidenceService.handleSkillTagEditsForEvidence(getParsedEditSkillsRemoveSkill(), testEvidence);
        verify(evidenceTagRepository).save(refEq(new EvidenceTag(noSkillsTag, testEvidence)));
    }
}
