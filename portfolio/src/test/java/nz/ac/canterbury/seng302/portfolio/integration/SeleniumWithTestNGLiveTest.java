package nz.ac.canterbury.seng302.portfolio.integration;

import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.fail;

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
        public void setUp() throws FileNotFoundException {
                seleniumExample = new SeleniumExample("");
                whenPortfolioIsLoaded_thenRegisterWorks();
                whenPortfolioIsLoaded_thenLoginWorks();

                getPassword_ForAdmin_FromTextFile();
                whenPortfolioIsLoaded_thenLoginAdmin_forTests();
                whenProjectIsAccessed_thenGoToAddDates();
                whenAddingDate_CheckTitleLen();
        }

        @AfterSuite
        public void tearDown() {
                seleniumExample.closeWindow();
        }


        public void getPassword_ForAdmin_FromTextFile() throws FileNotFoundException {
                String originpath = System.getProperty("user.dir");
                File passwordFile = new File(originpath.substring(0, originpath.length()-9) + "identityprovider/defaultAdminPassword.txt");
                Scanner passwordReader = new Scanner(passwordFile);
                passwordText = passwordReader.nextLine();
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
         * load up the page then login to the admin user
         */
        public void whenPortfolioIsLoaded_thenLoginAdmin_forTests() {
                seleniumExample.config.getDriver().get(seleniumExample.url);
                WebElement username = seleniumExample.config.getDriver().findElement(By.id("username"));
                username.sendKeys("admin");
                WebElement password = seleniumExample.config.getDriver().findElement(By.id("password"));
                password.sendKeys(passwordText);
                WebElement loginButton = seleniumExample.config.getDriver().findElement(By.id("login-button"));
                loginButton.click();
                WebElement fullName = seleniumExample.config.getDriver().findElement(By.id("full-name"));
                Assertions.assertEquals("admin admin", fullName.getText());
                seleniumExample.config.getDriver().get(seleniumExample.url2);
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
                WebElement milestone = seleniumExample.config.getDriver().findElement(By.id("evenMilestone"));
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

        /**
         * enter sprint details and make the sprint from add dates
         */
        @Test
        public void whenAddingDate_addSprint() {
                seleniumExample.config.getDriver().get(seleniumExample.urlDates);
                WebElement dateName = seleniumExample.config.getDriver().findElement(By.id("eventName"));
                WebElement dateStart = seleniumExample.config.getDriver().findElement(By.id("eventStartDate"));
                WebElement dateEnd = seleniumExample.config.getDriver().findElement(By.id("eventEndDate"));
                WebElement submitButton = seleniumExample.config.getDriver().findElement(By.id("submitButton"));
                dateName.sendKeys("Sprint Test");
                dateStart.sendKeys("2022-06-24");
                dateEnd.sendKeys("2022-06-25");
                submitButton.click();
        }


        /**
         * enter dealine details and make the dealine from add dates
         */
        @Test
        public void whenAddingDate_addDeadline() {
                seleniumExample.config.getDriver().get(seleniumExample.urlDates);
                WebElement dateName = seleniumExample.config.getDriver().findElement(By.id("eventName"));
                WebElement dateStart = seleniumExample.config.getDriver().findElement(By.id("eventStartDate"));
                WebElement dateEnd = seleniumExample.config.getDriver().findElement(By.id("eventEndDate"));
                WebElement type = seleniumExample.config.getDriver().findElement(By.id("eventType"));
                WebElement deadline = seleniumExample.config.getDriver().findElement(By.id("eventDeadline"));
                WebElement submitButton = seleniumExample.config.getDriver().findElement(By.id("submitButton"));
                type.click();
                deadline.click();
                dateName.sendKeys("Sprint Test");
                dateStart.sendKeys("2022-06-24");
                dateEnd.sendKeys("18:20");
                submitButton.click();
        }
}
