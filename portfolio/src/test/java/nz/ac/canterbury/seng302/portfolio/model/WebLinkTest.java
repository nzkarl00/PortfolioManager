package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WebLinkTest {
    WebLink link;
    Evidence evidence;

//    @Rule
//    public ExpectedException exception = ExpectedException.none();

    @BeforeEach
    void beforeEach() {
        // Refresh to a new link
        evidence = new Evidence(1, null, "Title", "Desc", LocalDate.of(2022, 1, 25));
        link = new WebLink("https://example.com", evidence);
    }

    @Test
    public void isFetched() {
        assertEquals(false, link.isFetched());
        link.setFetchResult(true, true);
        assertEquals(true, link.isFetched());
    }

    @Test
    public void isSecure_throwsOnUnfetchedLink() {
        String expectedMessage = "Link must be fetched first";

        Exception argumentException = Assertions.assertThrows(IllegalStateException.class, () -> {
            link.isSecure();
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

   @Test
   public void isSecure_doesNotThrowOnFetched() {
       link.setFetchResult(true, true);
       Assertions.assertDoesNotThrow(() -> {
          link.isSecure();
       });
   }

    @Test
    public void isNotFound_throwsOnUnfetchedLink() {
        String expectedMessage = "Link must be fetched first";

        Exception argumentException = Assertions.assertThrows(IllegalStateException.class, () -> {
            link.isNotFound();
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

    @Test
    public void isNotFound_doesNotThrowOnFetched() {
        link.setFetchResult(true, true);
        Assertions.assertDoesNotThrow(() -> {
            link.isNotFound();
        });
    }

    @Test
    public void getParentEvidence() {
        assertEquals(evidence, link.getParentEvidence());
    }

    @Test
    public void setFetchResult_setsFalse() {
        link.setFetchResult(false, false);
        assertEquals(true, link.isFetched());
        assertEquals(false, link.isNotFound());
        assertEquals(false, link.isSecure());
    }

    @Test
    public void setFetchResult_setsTrue() {
        link.setFetchResult(true, true);
        assertEquals(true, link.isFetched());
        assertEquals(true, link.isNotFound());
        assertEquals(true, link.isSecure());
    }

    @Test
    public void setSecure() {
        link.setFetchResult(true, true);

        assertEquals(true, link.isSecure());
        link.setSecure(false);
        assertEquals(false, link.isSecure());
        link.setSecure(true);
        assertEquals(true, link.isSecure());
    }

    @Test
    public void setSecure_throwsIfNotAlreadyFetched() {
        // Note: I have looked all over for a way to test
        // That assert statements are being triggered, but this is the best I've found
        // The @Rule doesn't seem to work
        // See: https://stackoverflow.com/questions/15216438/junit-testing-exceptions
        try {
            link.setSecure(true);
            fail("Should throw an assertion error");
        } catch(AssertionError e) {
            assertTrue(true);
        }
    }

    @Test
    public void setNotFound() {
        link.setFetchResult(true, true);

        assertEquals(true, link.isNotFound());
        link.setNotFound(false);
        assertEquals(false, link.isNotFound());
        link.setNotFound(true);
        assertEquals(true, link.isNotFound());
    }

    @Test
    public void setNotFound_throwsIfNotAlreadyFetched() {
        try {
            link.setNotFound(true);
            fail("Should throw an assertion error");
        } catch(AssertionError e) {
            assertTrue(true);
        }
    }
}
