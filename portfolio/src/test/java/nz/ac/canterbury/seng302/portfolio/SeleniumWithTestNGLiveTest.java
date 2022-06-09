package nz.ac.canterbury.seng302.portfolio;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
                whenPortfolioIsLoaded_thenRegisterWorks();
                whenPortfolioIsLoaded_thenLoginWorks();
                whenPortfolioIsLoaded_thenLoginWorks_two();
                whenProjectIsAccessed_thenGoToAddDates();
                whenAddingDate_CheckTitleLen();
        }

        @AfterSuite
        public void tearDown() {
                seleniumExample.closeWindow();
        }


        /**
         * load up the page to register a new uesr lra63
         */
        public void whenPortfolioIsLoaded_thenRegisterWorks() {
                seleniumExample.config.getDriver().get(seleniumExample.url);
                WebElement signupButton = seleniumExample.config.getDriver().findElement(By.id("signup-button"));
                signupButton.click();
                WebElement username = seleniumExample.config.getDriver().findElement(By.id("username"));
                username.sendKeys("lra63");
                WebElement password = seleniumExample.config.getDriver().findElement(By.id("password"));
                password.sendKeys("1234567890");
                WebElement passwordConfirm = seleniumExample.config.getDriver().findElement(By.id("passwordConfirm"));
                passwordConfirm.sendKeys("1234567890");
                WebElement firstName = seleniumExample.config.getDriver().findElement(By.id("firstname"));
                firstName.sendKeys("Lachlan");
                WebElement lastName = seleniumExample.config.getDriver().findElement(By.id("lastname"));
                lastName.sendKeys("Alsop");
                WebElement email = seleniumExample.config.getDriver().findElement(By.id("email"));
                email.sendKeys("lra63@uclive.ac.nz");
                WebElement submitButton = seleniumExample.config.getDriver().findElement(By.id("signup-button"));
                submitButton.click();
        }

        /**
         * load up the page then login to the user lra63, with the set password, note if this is not on your machine you will get errors
         */
        @Test
        public void whenPortfolioIsLoaded_thenLoginWorks() {
                seleniumExample.config.getDriver().get(seleniumExample.url);
                WebElement username = seleniumExample.config.getDriver().findElement(By.id("username"));
                username.sendKeys("lra63");
                WebElement password = seleniumExample.config.getDriver().findElement(By.id("password"));
                password.sendKeys("1234567890");
                WebElement loginButton = seleniumExample.config.getDriver().findElement(By.id("login-button"));
                loginButton.click();
                WebElement fullName = seleniumExample.config.getDriver().findElement(By.id("full-name"));
                Assertions.assertEquals("Lachlan Alsop", fullName.getText());
        }

        /**
         * load up the page then login to the user FAKE, with the set password, note if this is not on your machine you will get errors
         */
        @Test
        public void whenPortfolioIsLoaded_thenLoginWorks_two() {
                seleniumExample.config.getDriver().get(seleniumExample.url);
                WebElement username = seleniumExample.config.getDriver().findElement(By.id("username"));
                username.sendKeys("FAKE");
                WebElement password = seleniumExample.config.getDriver().findElement(By.id("password"));
                password.sendKeys("password");
                WebElement loginButton = seleniumExample.config.getDriver().findElement(By.id("login-button"));
                loginButton.click();
                WebElement fullName = seleniumExample.config.getDriver().findElement(By.id("full-name"));
                Assertions.assertEquals("Lane Edwards-Brown", fullName.getText());
        }

        /**
         * access the add dates page for the project
         */
        @Test
        public void whenProjectIsAccessed_thenGoToAddDates() {
                seleniumExample.config.getDriver().get(seleniumExample.url2);
                WebElement detailAccess = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
                detailAccess.click();
                WebElement addDateAccess = seleniumExample.config.getDriver().findElement(By.id("addDateButton"));
                addDateAccess.click();
                seleniumExample.urlDates =  seleniumExample.config.getDriver().getCurrentUrl();
        }

        /**
         * enter title and check length, see how the alert message changes when adding/removing characters and switching
         * between different event types of different max title lengths
         */
        @Test
        public void whenAddingDate_CheckTitleLen() {
                seleniumExample.config.getDriver().get(seleniumExample.urlDates);
                WebElement dateLen = seleniumExample.config.getDriver().findElement(By.id("maxLen"));
                WebElement dateName = seleniumExample.config.getDriver().findElement(By.id("eventName"));
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 100");
                dateName.sendKeys("12345");
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 95");
                dateName.clear();
                dateName.sendKeys("123456789012345");
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 85");
                dateName.sendKeys(Keys.BACK_SPACE);
                dateName.sendKeys(Keys.BACK_SPACE);
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 87");
                WebElement eventType = seleniumExample.config.getDriver().findElement(By.id("eventType"));
                eventType.click();
                WebElement deadline = seleniumExample.config.getDriver().findElement(By.id("eventDeadline"));
                deadline.click();
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 47");
                eventType.click();
                WebElement sprint = seleniumExample.config.getDriver().findElement(By.id("eventSprint"));
                sprint.click();
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 87");
                dateName.sendKeys("This is a long title to remove characters for testing");
                eventType.click();
                WebElement milestone = seleniumExample.config.getDriver().findElement(By.id("evenMilestone"));
                milestone.click();
                Assertions.assertEquals(dateLen.getText(), "Your title is 6 characters too long");
                eventType.click();
                sprint.click();
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 34");
        }
}
