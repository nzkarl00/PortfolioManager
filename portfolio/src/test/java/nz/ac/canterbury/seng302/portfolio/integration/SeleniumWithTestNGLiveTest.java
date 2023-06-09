package nz.ac.canterbury.seng302.portfolio.integration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.FileNotFoundException;

import static nz.ac.canterbury.seng302.portfolio.integration.SeleniumLogins.getPassword_ForAdmin_FromTextFile;
import static nz.ac.canterbury.seng302.portfolio.integration.SeleniumLogins.whenPortfolioIsLoaded_thenLoginAdmin_forTests;

/**
 * Note this is a template to pull from
 * import org.testng.annotations.Test;
 * use this test annotation to intiate your actual tests
 * do not remove the existing tests
 * they are needed to setup and login to an account
 */
public class SeleniumWithTestNGLiveTest {

        private SeleniumExample seleniumExample;

        String passwordText = "";

        @BeforeSuite
        public void setUp() throws FileNotFoundException, InterruptedException {

                seleniumExample = new SeleniumExample("");
                whenPortfolioIsLoaded_thenRegisterWorks();
                whenPortfolioIsLoaded_thenLoginWorks();
                passwordText = getPassword_ForAdmin_FromTextFile();
                whenPortfolioIsLoaded_thenLoginAdmin_forTests(seleniumExample, passwordText);
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
         * access the add dates page for the project
         */
        public void whenProjectIsAccessed_thenGoToAddDates() {
                seleniumExample.config.getDriver().get(seleniumExample.url3);
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
                WebElement milestone = seleniumExample.config.getDriver().findElement(By.id("eventMilestone"));
                milestone.click();
                Assertions.assertEquals(dateLen.getText(), "Your title is 6 characters too long");
                dateName.sendKeys(Keys.BACK_SPACE);
                dateName.sendKeys(Keys.BACK_SPACE);
                dateName.sendKeys(Keys.BACK_SPACE);
                Assertions.assertEquals(dateLen.getText(), "Your title is 3 characters too long");
                dateName.sendKeys("123456789");
                Assertions.assertEquals(dateLen.getText(), "Your title is 3 characters too long");
                dateName.sendKeys(Keys.BACK_SPACE);
                dateName.sendKeys(Keys.BACK_SPACE);
                dateName.sendKeys(Keys.BACK_SPACE);
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 0");
                dateName.sendKeys("123456789");
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 0");
                dateName.sendKeys(Keys.BACK_SPACE);
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 1");
                dateName.sendKeys("123456789");
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 0");
                eventType.click();
                sprint.click();
                Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 40");
        }
}
