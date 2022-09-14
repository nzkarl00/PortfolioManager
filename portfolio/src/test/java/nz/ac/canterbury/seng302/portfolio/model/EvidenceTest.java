package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceTag;
import nz.ac.canterbury.seng302.portfolio.model.evidence.SkillTag;
import nz.ac.canterbury.seng302.portfolio.model.evidence.WebLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvidenceTest {
    static Project exampleProject;
    Evidence evidence;
    String testUrl = "https://example.com";
    WebLink link;
    SkillTag skillTag;
    EvidenceTag evidenceTag;

    @BeforeEach
    void beforeEach() throws MalformedURLException {
        exampleProject = new Project(
                "Name",
                "Desc",
                LocalDate.of(2022, 1, 20),
                LocalDate.of(2022, 1, 27)
        );

        evidence = new Evidence(1, exampleProject, "Title", "Desc", LocalDate.of(2022, 1, 25), 0);

        link = new WebLink(testUrl, evidence);
        ReflectionTestUtils.setField(evidence, "links",
            new ArrayList(List.of(link)));

        skillTag = new SkillTag(exampleProject, "SkillA");
        evidenceTag = new EvidenceTag(skillTag, evidence);
        ReflectionTestUtils.setField(evidence, "evidenceTags",
            new ArrayList(List.of(evidenceTag)));
    }

    @Test
    public void getLinks_valid() {
        List<WebLink> linkList = new ArrayList<>();
        linkList.add(link);
        assertEquals(linkList, evidence.getLinks());
    }

    @Test
    public void getEvidenceTags_valid() {
        List<EvidenceTag> evidenceTagList = new ArrayList<>();
        evidenceTagList.add(evidenceTag);
        assertEquals(evidenceTagList, evidence.getEvidenceTags());
    }

    @Test
    public void validateProperties_allValidDateInRange() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);

        Assertions.assertDoesNotThrow(() -> Evidence.validateProperties(exampleProject, title, desc, date));
    }

    @Test
    public void validateProperties_evidenceBeforeSprint() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 19);
        String expectedMessage = "Evidence date is before parent project start date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> Evidence.validateProperties(exampleProject, title, desc, date));
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_evidenceAfterSprint() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 28);

        String expectedMessage = "Evidence date is after parent project end date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> Evidence.validateProperties(exampleProject, title, desc, date));
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_evidenceOnSprintStart() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 20);

        Assertions.assertDoesNotThrow(() -> Evidence.validateProperties(exampleProject, title, desc, date));
    }

    @Test
    public void validateProperties_evidenceOnSprintEnd() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 27);

        Assertions.assertDoesNotThrow(() -> Evidence.validateProperties(exampleProject, title, desc, date));
    }

    @Test
    public void validateProperties_titleTooLong() {
        String title = "A".repeat(Evidence.MAX_TITLE_LENGTH + 1);
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);

        String expectedMessage = "Title length must not exceed 100 characters";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> Evidence.validateProperties(exampleProject, title, desc, date));
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_descriptionTooLong() {
        String title = "Title";
        String desc = "A".repeat(Evidence.MAX_DESCRIPTION_LENGTH + 1);
        LocalDate date = LocalDate.of(2022, 1, 25);

        String expectedMessage = "Description length must not exceed 2000 characters";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> Evidence.validateProperties(exampleProject, title, desc, date));
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_newAssociatedProjectValidDates() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date, 0);

        exampleProject = new Project(
                "Name",
                "Desc",
                LocalDate.of(2022, 1, 20),
                LocalDate.of(2022, 1, 26)
        );

        Assertions.assertDoesNotThrow(() -> evidence.setAssociatedProject(exampleProject));
    }

    @Test
    public void validateProperties_newAssociatedProjectEndsBeforeEvidence() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date, 0);

        exampleProject = new Project(
                "Name",
                "Desc",
                LocalDate.of(2022, 1, 20),
                LocalDate.of(2022, 1, 24)
        );

        String expectedMessage = "New project ends before evidence date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> evidence.setAssociatedProject(exampleProject));
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_newAssociatedProjectStartsAfterEvidence() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date, 0);

        exampleProject = new Project(
                "Name",
                "Desc",
                LocalDate.of(2022, 1, 27),
                LocalDate.of(2022, 1, 26)
        );

        String expectedMessage = "New project starts after evidence date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> evidence.setAssociatedProject(exampleProject));
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_newValidDate() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date, 0);

        final LocalDate newDate = LocalDate.of(2022, 1, 24);

        Assertions.assertDoesNotThrow(() -> evidence.setDate(newDate));
    }

    @Test
    public void validateProperties_newDateBeforeProjectStart() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date, 0);

        final LocalDate newDate = LocalDate.of(2022, 1, 15);

        String expectedMessage = "New evidence date is before parent project start date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> evidence.setDate(newDate));
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_newDateAfterProjectEnd() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date, 0);

        final LocalDate newDate = LocalDate.of(2022, 1, 29);

        String expectedMessage = "New evidence date is after parent project end date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> evidence.setDate(newDate));
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_basicGetSet() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date, 0);

        assertEquals(1, evidence.getParentUserId());
        assertEquals(title, evidence.getTitle());
        assertEquals(desc, evidence.getDescription());
        assertEquals(date, evidence.getDate());
        assertEquals(exampleProject, evidence.getAssociatedProject());

        Project newProject = new Project(
                "Name",
                "Desc",
                LocalDate.of(2022, 1, 20),
                LocalDate.of(2022, 1, 26)
        );

        evidence.setAssociatedProject(newProject);
        title = "New";
        desc = "New Desc";
        date = LocalDate.of(2022, 1, 24);
        evidence.setTitle(title);
        evidence.setDescription(desc);
        evidence.setDate(date);

        assertEquals(title, evidence.getTitle());
        assertEquals(desc, evidence.getDescription());
        assertEquals(date, evidence.getDate());
    }

    @Test
    public void getQualitativeSkillsCatFromInt() {
        evidence.setCategories(1);
        List<String> categoryString = evidence.getCategoryStrings();
        assertEquals(List.of("Qualitative Skills"), categoryString);
    }

    @Test
    public void getQuantitativeSkillsCatFromInt() {
        evidence.setCategories(2);
        List<String> categoryString = evidence.getCategoryStrings();
        assertEquals(List.of("Quantitative Skills"), categoryString);
    }

    @Test
    public void getServiceCatFromInt() {
        evidence.setCategories(4);
        List<String> categoryString = evidence.getCategoryStrings();
        assertEquals(List.of("Service"), categoryString);
    }

    @Test
    public void getQualitativeSkillsAndServiceCatsFromInt() {
        evidence.setCategories(5);
        List<String> categoryString = evidence.getCategoryStrings();
        assertEquals(List.of("Qualitative Skills", "Service"), categoryString);
    }

    @Test
    public void getQuantitativeSkillsAndServiceCatsFromInt() {
        evidence.setCategories(6);
        List<String> categoryString = evidence.getCategoryStrings();
        assertEquals(List.of("Quantitative Skills", "Service"), categoryString);
    }

    @Test
    public void getAllCatsFromInt() {
        evidence.setCategories(7);
        List<String> categoryString = evidence.getCategoryStrings();
        assertEquals(List.of("Qualitative Skills", "Quantitative Skills", "Service"), categoryString);
    }

    @Test
    public void quantitativeSkillsCatToInt() {
        int categoryInt = evidence.categoryStringToInt("Qualitative Skills");
        assertEquals(1, categoryInt);
    }

    @Test
    public void qualitativeSkillsCatToInt() {
        int categoryInt = evidence.categoryStringToInt("Quantitative Skills");
        assertEquals(2, categoryInt);
    }

    @Test
    public void serviceCatToInt() {
        int categoryInt = evidence.categoryStringToInt("Service");
        assertEquals(4, categoryInt);
    }

    @Test
    public void qualitativeSkillsAndQuantitativeSkillsCatToInt() {
        int categoryInt = evidence.categoryStringToInt("Qualitative Skills~Quantitative Skills");
        assertEquals(3, categoryInt);
    }

    @Test
    public void allCatsToInt() {
        int categoryInt = evidence.categoryStringToInt("Qualitative Skills~Quantitative Skills~Service");
        assertEquals(7, categoryInt);
    }
}
