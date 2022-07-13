package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

public class DeadlineTest {
    Deadline deadline;

    @Test
    public void validateProperties_blueSky() {
        String deadlineName = "A".repeat(5);
        String deadlineDescription = "A".repeat(5);
        Assertions.assertDoesNotThrow(() -> {
            deadline.validateProperties(deadlineName, deadlineDescription);
        });
    }

    @Test
    public void validateProperties_longName_throwsError() {
        String deadlineName = "A".repeat(61);
        String deadlineDescription = "A".repeat(5);
        String expectedMessage = "Name length must not exceed 60 characters";
        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            deadline.validateProperties(deadlineName, deadlineDescription);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_longDesc_throwsError() {
        String deadlineName = "A".repeat(5);
        String deadlineDescription = "A".repeat(241);
        String expectedMessage = "Description length must not exceed 240 characters";
        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            deadline.validateProperties(deadlineName, deadlineDescription);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }



    // Test getters and setters
    @Test
    public void getSetName() {
        String itemName = "Name";
        String itemDescription = "Desc";
        String expectedMessage = "Description length must not exceed 60 characters";
        Deadline item = new Deadline(
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
        Deadline item = new Deadline(
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
        Deadline item = new Deadline(
                new Project(), itemName, itemDescription, initialTime
        );
        Assertions.assertEquals(initialTime, item.getStartDate());
        Assertions.assertEquals(initialTime, item.getEndDate());

        LocalDateTime newTime = initialTime.plusHours(5);
        item.setStartDate(newTime); // Try set start
        Assertions.assertEquals(newTime, item.getStartDate());
        Assertions.assertEquals(newTime, item.getEndDate());

        newTime = initialTime.plusHours(2);
        item.setEndDate(newTime); // Try set end
        Assertions.assertEquals(newTime, item.getStartDate());
        Assertions.assertEquals(newTime, item.getEndDate());
    }
}
