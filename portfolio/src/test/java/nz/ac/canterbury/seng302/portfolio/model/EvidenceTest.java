package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EvidenceTest {
    Evidence model;
    static Project exampleProject;

    @BeforeEach
    void beforeEach() {
        exampleProject = new Project(
                "Name",
                "Desc",
                LocalDate.of(2022, 1, 20),
                LocalDate.of(2022, 1, 27)
        );
    }

    @Test
    public void validateProperties_allValidDateInRange() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);

        Assertions.assertDoesNotThrow(() -> {
            model.validateProperties(exampleProject, title, desc, date);
        });
    }

    @Test
    public void validateProperties_evidenceBeforeSprint() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 19);
        String expectedMessage = "Evidence date is before parent project start date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            model.validateProperties(exampleProject, title, desc, date);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_evidenceAfterSprint() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 28);

        String expectedMessage = "Evidence date is after parent project end date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            model.validateProperties(exampleProject, title, desc, date);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_evidenceOnSprintStart() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 20);

        Assertions.assertDoesNotThrow(() -> {
            model.validateProperties(exampleProject, title, desc, date);
        });
    }

    @Test
    public void validateProperties_evidenceOnSprintEnd() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 27);

        Assertions.assertDoesNotThrow(() -> {
            model.validateProperties(exampleProject, title, desc, date);
        });
    }

    @Test
    public void validateProperties_titleTooLong() {
        String title = "A".repeat(Evidence.MAX_TITLE_LENGTH + 1);
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);

        String expectedMessage = "Title length must not exceed 100 characters";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            model.validateProperties(exampleProject, title, desc, date);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_descriptionTooLong() {
        String title = "Title";
        String desc = "A".repeat(Evidence.MAX_DESCRIPTION_LENGTH + 1);
        LocalDate date = LocalDate.of(2022, 1, 25);

        String expectedMessage = "Description length must not exceed 2000 characters";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            model.validateProperties(exampleProject, title, desc, date);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_newAssociatedProjectValidDates() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date);

        exampleProject = new Project(
                "Name",
                "Desc",
                LocalDate.of(2022, 1, 20),
                LocalDate.of(2022, 1, 26)
        );

        Assertions.assertDoesNotThrow(() -> {
            evidence.setAssociatedProject(exampleProject);
        });
    }

    @Test
    public void validateProperties_newAssociatedProjectEndsBeforeEvidence() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date);

        exampleProject = new Project(
                "Name",
                "Desc",
                LocalDate.of(2022, 1, 20),
                LocalDate.of(2022, 1, 24)
        );

        String expectedMessage = "New project ends before evidence date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evidence.setAssociatedProject(exampleProject);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_newAssociatedProjectStartsAfterEvidence() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date);

        exampleProject = new Project(
                "Name",
                "Desc",
                LocalDate.of(2022, 1, 27),
                LocalDate.of(2022, 1, 26)
        );

        String expectedMessage = "New project starts after evidence date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evidence.setAssociatedProject(exampleProject);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_newValidDate() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date);

        final LocalDate newDate = LocalDate.of(2022, 1, 24);

        Assertions.assertDoesNotThrow(() -> {
            evidence.setDate(newDate);
        });
    }

    @Test
    public void validateProperties_newDateBeforeProjectStart() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date);

        final LocalDate newDate = LocalDate.of(2022, 1, 15);

        String expectedMessage = "New evidence date is before parent project start date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evidence.setDate(newDate);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_newDateAfterProjectEnd() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date);

        final LocalDate newDate = LocalDate.of(2022, 1, 29);

        String expectedMessage = "New evidence date is after parent project end date";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            evidence.setDate(newDate);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_basicGetSet() {
        String title = "Evidence Title";
        String desc = "Description";
        LocalDate date = LocalDate.of(2022, 1, 25);
        Evidence evidence = new Evidence(1, exampleProject, title, desc, date);

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
}
