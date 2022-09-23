package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.LinkedCommit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkCommitTest {
    LocalDate date;
    LocalDateTime timestamp;
    static Project exampleProject;
    Evidence evidence;
    Evidence evidence2; // A second evidence to test against
    LinkedCommit linkedCommit;

    @BeforeEach
    void beforeEach() {
        date = LocalDate.of(2022, 1, 25);
        timestamp = LocalDateTime.of(2021, 9, 15, 11, 43);
        exampleProject = new Project(
            "Name",
            "Desc",
            LocalDate.of(2021, 1, 20),
            LocalDate.of(2022, 1, 27)
        );
        evidence = new Evidence(1, exampleProject, "Title", "Desc", date, 0);
        evidence2 = new Evidence(1, exampleProject, "Title2", "Desc2", date, 0);
        linkedCommit = new LinkedCommit(evidence, "yyu69", "yyu69/google-map-tutorial", "5b30f44383d8cbbf97beeb63a1b90443b871c95e", "Yiyang (Jessie) Yu", "Initial commit", timestamp);
    }

    @Test
    public void getParentEvidence() {
        assertEquals(evidence, linkedCommit.getParentEvidence());
    }

    @Test
    public void validateProperties_titleTooLong() {
        String title = "A".repeat(LinkedCommit.MAX_TITLE_LENGTH + 1);

        String expectedMessage = "Title length must not exceed " + LinkedCommit.MAX_TITLE_LENGTH + " characters";

        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            linkedCommit.validateProperties(title);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    /**
     * Testing if having a commit associated with two or more difference evidences works
     */
    @Test
    public void setLinkedCommitToSecondEvidence_pass() {
        LinkedCommit linkedCommit2 = new LinkedCommit(evidence2, "yyu69", "yyu69/google-map-tutorial", "5b30f44383d8cbbf97beeb63a1b90443b871c95e", "Yiyang (Jessie) Yu", "Initial commit", timestamp);
        Assertions.assertEquals(evidence, linkedCommit.getParentEvidence());
        Assertions.assertEquals(evidence2, linkedCommit2.getParentEvidence());
    }
}
