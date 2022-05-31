package nz.ac.canterbury.seng302.portfolio;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.fail;
import static org.testng.AssertJUnit.*;

public class SeleniumWithTestNGLiveTest {

        private SeleniumExample seleniumExample;
        private String expectedTitle = "About Baeldung | Baeldung";

        @BeforeSuite
        public void setUp() {
                seleniumExample = new SeleniumExample("");
        }

        @AfterSuite
        public void tearDown() {
                seleniumExample.closeWindow();
        }

        @Test
        public void whenPortfolioIsLoaded_thenLoginWorks() {
                WebElement username = seleniumExample.config.getDriver().findElement(By.id("username"));
                username.sendKeys("lra63");
                WebElement password = seleniumExample.config.getDriver().findElement(By.id("password"));
                password.sendKeys("1234567890");
                WebElement loginButton = seleniumExample.config.getDriver().findElement(By.id("login-button"));
                loginButton.click();
                WebElement fullName = seleniumExample.config.getDriver().findElement(By.id("full-name"));
                Assertions.assertEquals("Lachlan Alsop", fullName.getText());
        }
}
