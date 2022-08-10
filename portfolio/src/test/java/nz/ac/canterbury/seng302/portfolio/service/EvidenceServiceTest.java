package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.SkillTag;
import nz.ac.canterbury.seng302.portfolio.model.evidence.SkillTagRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class EvidenceServiceTest {

    @Mock
    SkillTagRepository skillTagRepository;

    @InjectMocks
    EvidenceService evidenceService = new EvidenceService();

    static SkillTag skillTagCSharp;
    static SkillTag skillTagC;
    static SkillTag skillTagCPlusPlus;
    static SkillTag skillTagCSS;

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
        testSkillTagList = new ArrayList<>();
        testSkillTagList.add(skillTagCSharp);
        testSkillTagList.add(skillTagCSharp);
        testSkillTagList.add(skillTagC);
        testSkillTagList.add(skillTagCPlusPlus);
        testSkillTagList.add(skillTagCSS);
        testSkillTagList.add(skillTagCSS);
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
        assertEquals(4, skills.size());
    }
}
