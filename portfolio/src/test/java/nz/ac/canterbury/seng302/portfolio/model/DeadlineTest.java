package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        String deadlineDescription = "A".repeat(61);
        String expectedMessage = "Description length must not exceed 60 characters";
        Exception argumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            deadline.validateProperties(deadlineName, deadlineDescription);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }
}