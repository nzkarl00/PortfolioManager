package nz.ac.canterbury.seng302.portfolio.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ValidationTest {
    @Test
    public void isValidEmail_acceptsOrdinaryEmail() {
        Assertions.assertTrue(Validation.isValidEmail("user@example.com"));
    }

    @Test
    public void isValidEmail_rejectsWithNoPrecedingName() {
        Assertions.assertFalse(Validation.isValidEmail("@example.com"));
    }

    @Test
    public void isValidEmail_rejectsWithNoDomain() {
        Assertions.assertFalse(Validation.isValidEmail("user@"));
    }

    @Test
    public void isValidEmail_rejectsWithShortenedTLD() {
        Assertions.assertFalse(Validation.isValidEmail("user@example.x"));
    }
}
