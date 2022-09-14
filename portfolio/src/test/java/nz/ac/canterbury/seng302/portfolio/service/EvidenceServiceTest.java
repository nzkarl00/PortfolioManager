package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Any;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static nz.ac.canterbury.seng302.portfolio.common.CommonProjectItems.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EvidenceServiceTest {

    @Mock
    SkillTagRepository skillTagRepository;

    @Mock
    EvidenceRepository evidenceRepository;

    @InjectMocks
    EvidenceService evidenceService = new EvidenceService();

    static SkillTag skillTagCSharp, skillTagC, skillTagCPlusPlus, skillTagCSS, skillTagReactNative;

    static List<SkillTag> testSkillTagList;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this); // This is required for Mockito annotations to work
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
        testEvidence.setEvidenceTags(List.of(getEvidenceTagA(testEvidence)));
        evidenceService.deleteEvidence(testEvidence);
        verify(skillTagRepository, times(1)).delete(testEvidence.getEvidenceTags().get(0).getParentSkillTag());
    }

    @Test
    void deleteEvidenceWithMultipleSkills() {
        Evidence testEvidence = getValidEvidence();
        testEvidence.setEvidenceTags(List.of(getEvidenceTagA(testEvidence), getEvidenceTagB(testEvidence), getEvidenceTagC(testEvidence)));
        evidenceService.deleteEvidence(testEvidence);
        for (EvidenceTag evidenceTag: testEvidence.getEvidenceTags()) {
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
}
