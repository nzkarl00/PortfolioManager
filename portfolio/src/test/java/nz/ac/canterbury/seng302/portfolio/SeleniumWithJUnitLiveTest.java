package nz.ac.canterbury.seng302.portfolio;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumWithJUnitLiveTest {

    private static SeleniumExample seleniumExample;
    private String expectedTitle = "About Baeldung | Baeldung";

    @BeforeClass
    public static void setUp() {
        seleniumExample = new SeleniumExample();
    }

    @Test
    public void whenAboutBaeldungIsLoaded_thenAboutEugenIsMentionedOnPage() {
        seleniumExample.getAboutBaeldungPage();
        String actualTitle = seleniumExample.getTitle();

        assertNotNull(actualTitle);
        assertEquals(expectedTitle, actualTitle);
        assertTrue(seleniumExample.isAuthorInformationAvailable());
    }

    @AfterClass
    public static void tearDown() {
        seleniumExample.closeWindow();
    }
}
