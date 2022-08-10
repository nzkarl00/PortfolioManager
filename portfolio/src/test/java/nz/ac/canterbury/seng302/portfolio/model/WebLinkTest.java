package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.WebLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WebLinkTest {
    WebLink link;
    Evidence evidence;
    String testUrl = "https://example.com";

//    @Rule
//    public ExpectedException exception = ExpectedException.none();

    @BeforeEach
    void beforeEach() throws MalformedURLException {
        // Refresh to a new link
        evidence = new Evidence(1, null, "Title", "Desc", LocalDate.of(2022, 1, 25));
        link = new WebLink(testUrl, evidence);
    }

    @Test
    public void isFetched() {
        assertEquals(false, link.isFetched());
        link.setFetchResult(true);
        assertEquals(true, link.isFetched());
    }

    @Test
    public void getUrlWithoutProtocol() {
        assertEquals(testUrl, link.getUrl());
        String checkPoint = "://";
        Integer checkPointIndex = testUrl.indexOf(checkPoint) + checkPoint.length();
        String expectedUrlWithoutProto = testUrl.substring(checkPointIndex);
        assertEquals(expectedUrlWithoutProto, link.getUrlWithoutProtocol());
    }

    @Test
    public void constructor_throwsOnNoProtocol() {
        String expectedMessage = "URL must contain an HTTP(s) protocol definition";

        Exception argumentException = Assertions.assertThrows(MalformedURLException.class, () -> {
            new WebLink("url-no-protocol", evidence);
        });
        Assertions.assertEquals(expectedMessage, argumentException.getMessage());
    }

   @Test
   public void isSecure_trueOnHttpsURL() {
      Assertions.assertTrue(link.isSecure());
   }

    @Test()
    public void isSecure_falseOnHttpURL() {
        Assertions.assertDoesNotThrow(() -> {
            link = new WebLink("http://google.com", evidence);
            Assertions.assertFalse(link.isSecure());
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
        link.setFetchResult(true);
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
        link.setFetchResult(false);
        assertEquals(true, link.isFetched());
        assertEquals(false, link.isNotFound());
    }

    @Test
    public void setFetchResult_setsTrue() {
        link.setFetchResult(true);
        assertEquals(true, link.isFetched());
        assertEquals(true, link.isNotFound());
    }

    @Test
    public void setNotFound() {
        link.setFetchResult(true);

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
