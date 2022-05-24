package nz.ac.canterbury.seng302.portfolio;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

public class SeleniumWithTestNGLiveTest {

        private SeleniumExample seleniumExample;
        private String expectedTitle = "About Baeldung | Baeldung";

        @BeforeSuite
        public void setUp() {
                seleniumExample = new SeleniumExample();
        }

        @AfterSuite
        public void tearDown() {
                seleniumExample.closeWindow();
        }

        @Test
        public void whenAboutBaeldungIsLoaded_thenAboutEugenIsMentionedOnPage() {
                seleniumExample.getAboutBaeldungPage();
                String actualTitle = seleniumExample.getTitle();

                assertNotNull(actualTitle);
                assertEquals(expectedTitle, actualTitle);
                assertTrue(seleniumExample.isAuthorInformationAvailable());
        }
}
