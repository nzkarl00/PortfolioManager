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
        private SeleniumAddDatesTitleLengthTest seleniumTestTitleLengthAddDates;
        private String expectedTitle = "About Baeldung | Baeldung";

        @BeforeSuite
        public void setUp() {
                seleniumExample = new SeleniumExample("");
                whenPortfolioIsLoaded_thenRegisterWorks();
                whenPortfolioIsLoaded_thenLoginWorks();
                seleniumTestTitleLengthAddDates = new SeleniumAddDatesTitleLengthTest("");
        }

        @AfterSuite
        public void tearDown() {
                seleniumExample.closeWindow();
                seleniumTestTitleLengthAddDates.closeWindow();
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
}
