package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.LinkedCommit;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkCommitTest {
    LocalDate date;
    static Project exampleProject;
    Evidence evidence;
    Evidence evidence2; // A second evidence to test against
    GroupRepo groupRepo;
    LinkedCommit linkedCommit;

    @BeforeEach
    void beforeEach() {
        date = LocalDate.of(2022, 1, 25);
        exampleProject = new Project(
            "Name",
            "Desc",
            LocalDate.of(2022, 1, 20),
            LocalDate.of(2022, 1, 27)
        );
        evidence = new Evidence(1, null, "Title", "Desc", date);
        evidence2 = new Evidence(1, null, "Title2", "Desc2", date);
        groupRepo = new GroupRepo(1, "hre56", "hre56/cosc368", "m82xFXnuhBAfD9yp_5zd");
        linkedCommit = new LinkedCommit(evidence, groupRepo, "TODO?GETAREALHASHMAYBE", "Hugo Reeves", "First Commit Title", date);
    }

    @Test
    public void getParentEvidence() {
        assertEquals(evidence, linkedCommit.getParentEvidence());
    }

    @Test
    public void getParentGroupRepo() {
        assertEquals(groupRepo, linkedCommit.getParentGroupRepo());
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
        LinkedCommit linkedCommit2 = new LinkedCommit(evidence2, groupRepo, "TODO?GETAREALHASHMAYBE", "Hugo Reeves", "First Commit Title", date);
        Assertions.assertEquals(evidence, linkedCommit.getParentEvidence());
        Assertions.assertEquals(evidence2, linkedCommit2.getParentEvidence());
    }
}
