package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Category;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.WebLink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {
    Category testCategory;
    Evidence testEvidence;

    @BeforeEach
    void beforeEach() {
        // Refresh to a new category
        testEvidence = new Evidence(1, null, "Title", "Desc", LocalDate.of(2022, 1, 25));
        testCategory = new Category(testEvidence, "Quantitative Skills");
    }

    /**
     * Checks parent evidence is returned correctly
     */
    @Test
    public void getParentEvidence() {
        assertEquals(testEvidence, testCategory.getParentEvidence());
    }

    /**
     * Checks that "Service" is a valid name
     */
    @Test
    public void validServiceNameCheck() { assertDoesNotThrow(() -> Category.validateCategoryName("Service"));}

    /**
     * Checks that "Quantitative Skills" is a valid name
     */
    @Test
    public void validQuantNameCheck() { assertDoesNotThrow(() -> Category.validateCategoryName("Quantitative Skills"));}

    /**
     * Checks that "Qualitative Skills" is a valid name
     */
    @Test
    public void validQualNameCheck() { assertDoesNotThrow(() -> Category.validateCategoryName("Qualitative Skills"));}

    /**
     * Checks that any other name is invalid
     */
    @Test
    public void invalidNameCheck() { assertThrows(IllegalArgumentException.class, () -> Category.validateCategoryName("Something else"));}
}
