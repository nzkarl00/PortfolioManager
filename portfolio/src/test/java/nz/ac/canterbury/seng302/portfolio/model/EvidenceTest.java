package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EvidenceTest {
    Evidence model;
    static Project exampleProject;

    @BeforeAll
    static void beforeAll() {
        exampleProject = new Project(
                "Name",
                "Desc",
                new Date(2022, 1, 20),
                new Date(2022, 1, 27)
        );
    }

    @Test
    public void validateProperties_allValidDateInRange() {
        String title = "Evidence Title";
        String desc = "Description";
        exampleProject = new Project(
                "Name",
                "Desc",
                new Date(2022, 1, 20),
                new Date(2022, 1, 27)
        );
        LocalDate date = LocalDate.of(2022, 1, 25);
        Assertions.assertEquals(new Date(2022, 1, 20).getYear(), exampleProject.getStartDate().getYear());
        Assertions.assertEquals(2022, exampleProject.getLocalStartDate().getYear());
        Assertions.assertEquals(date, exampleProject.getLocalStartDate());
        Assertions.assertEquals(date, exampleProject.getLocalEndDate());

        Assertions.assertDoesNotThrow(() -> {
            model.validateProperties(exampleProject, title, desc, date);
        });
    }
//
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

        String expectedMessage = "Evidence date is after parent project start date";

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
//
//    @Test
//    public void validateProperties_longDesc_throwsError() {
//        String deadlineName = "A".repeat(5);
//        String deadlineDescription = "A".repeat(241);
//        String expectedMessage = "Description length must not exceed 240 characters";
//        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            event.validateProperties(deadlineName, deadlineDescription);
//        });
//        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
//    }
//
//
//
//    // Test getters and setters
//    @Test
//    public void getSetName() {
//        String itemName = "Name";
//        String itemDescription = "Desc";
//        String expectedMessage = "Description length must not exceed 60 characters";
//        Event item = new Event(
//                new Project(), itemName, itemDescription, LocalDateTime.now(), LocalDateTime.now()
//        );
//        Assertions.assertEquals(itemName, item.getName());
//        item.setName("New name");
//        Assertions.assertEquals("New name", item.getName());
//    }
//
//    @Test
//    public void getSetDescription() {
//        String itemName = "Name";
//        String itemDescription = "Desc";
//        String expectedMessage = "Description length must not exceed 60 characters";
//        Event item = new Event(
//                new Project(), itemName, itemDescription, LocalDateTime.now(), LocalDateTime.now()
//        );
//        Assertions.assertEquals(itemDescription, item.getDescription());
//        item.setDescription("New desc");
//        Assertions.assertEquals("New desc", item.getDescription());
//    }
//
//    // The start and end properties should always be equal. Assert this holds.
//    @Test
//    public void getSetStartAndEndTime() {
//        String itemName = "Name";
//        String itemDescription = "Desc";
//        String expectedMessage = "Description length must not exceed 60 characters";
//        LocalDateTime initialStartTime = LocalDateTime.now();
//        LocalDateTime initialEndTime = initialStartTime.plusHours(5);
//        Event item = new Event(
//                new Project(), itemName, itemDescription, initialStartTime, initialEndTime
//        );
//        Assertions.assertEquals(initialStartTime, item.getStartDate());
//        Assertions.assertEquals(initialEndTime, item.getEndDate());
//
//        item.setStartDate(initialEndTime); // Try set start
//        Assertions.assertEquals(initialEndTime, item.getStartDate());
//        Assertions.assertEquals(initialEndTime, item.getEndDate());
//
//        LocalDateTime newTime = initialStartTime.plusHours(2);
//        item.setEndDate(newTime); // Try set end
//        Assertions.assertEquals(initialEndTime, item.getStartDate());
//        Assertions.assertEquals(newTime, item.getEndDate());
//    }
}
