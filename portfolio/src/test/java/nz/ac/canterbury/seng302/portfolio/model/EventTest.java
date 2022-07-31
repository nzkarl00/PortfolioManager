package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class EventTest {
    Event event;

    @Test
    public void validateProperties_blueSky() {
        String deadlineName = "A".repeat(5);
        String deadlineDescription = "A".repeat(5);
        Assertions.assertDoesNotThrow(() -> {
            event.validateProperties(deadlineName, deadlineDescription);
        });
    }

    @Test
    public void validateProperties_longName_throwsError() {
        String deadlineName = "A".repeat(61);
        String deadlineDescription = "A".repeat(5);
        String expectedMessage = "Name length must not exceed 60 characters";
        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            event.validateProperties(deadlineName, deadlineDescription);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_longDesc_throwsError() {
        String deadlineName = "A".repeat(5);
        String deadlineDescription = "A".repeat(241);
        String expectedMessage = "Description length must not exceed 240 characters";
        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            event.validateProperties(deadlineName, deadlineDescription);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }



    // Test getters and setters
    @Test
    public void getSetName() {
        String itemName = "Name";
        String itemDescription = "Desc";
        String expectedMessage = "Description length must not exceed 60 characters";
        Event item = new Event(
            new Project(), itemName, itemDescription, LocalDateTime.now(), LocalDateTime.now()
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
        Event item = new Event(
            new Project(), itemName, itemDescription, LocalDateTime.now(), LocalDateTime.now()
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
        LocalDateTime initialStartTime = LocalDateTime.now();
        LocalDateTime initialEndTime = initialStartTime.plusHours(5);
        Event item = new Event(
            new Project(), itemName, itemDescription, initialStartTime, initialEndTime
        );
        Assertions.assertEquals(initialStartTime, item.getStartDate());
        Assertions.assertEquals(initialEndTime, item.getEndDate());

        item.setStartDate(initialEndTime); // Try set start
        Assertions.assertEquals(initialEndTime, item.getStartDate());
        Assertions.assertEquals(initialEndTime, item.getEndDate());

        LocalDateTime newTime = initialStartTime.plusHours(2);
        item.setEndDate(newTime); // Try set end
        Assertions.assertEquals(initialEndTime, item.getStartDate());
        Assertions.assertEquals(newTime, item.getEndDate());
    }
}