package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

public class MilestoneTest {
    Milestone milestone;

    @Test
    public void validateProperties_blueSky() {
        String milestoneName = "A".repeat(5);
        String milestoneDescription = "A".repeat(5);
        Assertions.assertDoesNotThrow(() -> {
            milestone.validateProperties(milestoneName, milestoneDescription);
        });
    }

    @Test
    public void validateProperties_longName_throwsError() {
        String milestoneName  = "A".repeat(61);
        String milestoneDescription = "A".repeat(5);
        String expectedMessage = "Name length must not exceed 60 characters";
        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            milestone.validateProperties(milestoneName, milestoneDescription);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_longDesc_throwsError() {
        String milestoneName = "A".repeat(5);
        String milestoneDescription = "A".repeat(61);
        String expectedMessage = "Description length must not exceed 60 characters";
        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            milestone.validateProperties(milestoneName, milestoneDescription);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }



    // Test getters and setters
    @Test
    public void getSetName() {
        String itemName = "Name";
        String itemDescription = "Desc";
        String expectedMessage = "Description length must not exceed 60 characters";
        Milestone item = new Milestone(
                new Project(), itemName, itemDescription, LocalDateTime.now()
        );
        Assertions.assertEquals(itemName, item.getName());
        item.setName("New name");
        Assertions.assertEquals("New name", item.getName());
    }

    @Test
    public void getSetDescription() {
        String itemName = "Name";
        String itemDescription = "Desc";
        String expectedMessage = "Description length must not exceed 60 characters";
        Milestone item = new Milestone(
                new Project(), itemName, itemDescription, LocalDateTime.now()
        );
        Assertions.assertEquals(itemDescription, item.getDescription());
        item.setDescription("New desc");
        Assertions.assertEquals("New desc", item.getDescription());
    }

    // The start and end properties should always be equal. Assert this holds.
    @Test
    public void getSetStartAndEndTime() {
        String itemName = "Name";
        String itemDescription = "Desc";
        String expectedMessage = "Description length must not exceed 60 characters";
        LocalDateTime initialTime = LocalDateTime.now();
        Milestone item = new Milestone(
                new Project(), itemName, itemDescription, initialTime
        );
        Assertions.assertEquals(initialTime, item.getStartDate());
        Assertions.assertEquals(initialTime, item.getEndDate());

    }
}