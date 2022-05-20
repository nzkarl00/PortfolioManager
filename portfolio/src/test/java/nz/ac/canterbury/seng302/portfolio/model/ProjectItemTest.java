package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProjectItemTest {
    ProjectItem item;

    @Test
    public void validateProperties_blueSky() {
        String itemName = "A".repeat(5);
        String itemDescription = "A".repeat(5);
        Assertions.assertDoesNotThrow(() -> {
            item.validateProperties(itemName, itemDescription);
        });
    }

    @Test
    public void validateProperties_longName_throwsError() {
        String itemName = "A".repeat(61);
        String itemDescription = "A".repeat(5);
        String expectedMessage = "Name length must not exceed 60 characters";
        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            item.validateProperties(itemName, itemDescription);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void validateProperties_longDesc_throwsError() {
        String itemName = "A".repeat(5);
        String itemDescription = "A".repeat(61);
        String expectedMessage = "Description length must not exceed 60 characters";
        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            item.validateProperties(itemName, itemDescription);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }
}