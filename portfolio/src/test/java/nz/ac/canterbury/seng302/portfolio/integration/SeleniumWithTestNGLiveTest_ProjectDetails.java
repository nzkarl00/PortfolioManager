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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Note this is a template to pull from
 * import org.testng.annotations.Test;
 * use this test annotation to intiate your actual tests
 * do not remove the existing tests
 * they are needed to setup and login to an account
 */
public class SeleniumWithTestNGLiveTest_ProjectDetails {

    private SeleniumExample seleniumExample;

    String passwordText = "";
    String projectInfoUrl = "/details?id=1";
    String urlDates = "";

    @BeforeSuite
    public void setUp() throws FileNotFoundException, InterruptedException {
        seleniumExample = new SeleniumExample("");

        getPassword_ForAdmin_FromTextFile();
        whenPortfolioIsLoaded_thenLoginAdmin_forTests();

        inProject_addDeadline();
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
        seleniumExample.config.getDriver().get(seleniumExample.url + "/landing");
    }

    /**
     * Creates a deadline in the initial sprint
     */
    public void inProject_addDeadline() throws InterruptedException {
        seleniumExample.config.getDriver().get("http://localhost:9000/details?id=1");
        WebElement detailAccess = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        WebElement addDateAccess = seleniumExample.config.getDriver().findElement(By.id("addDateButton"));
        detailAccess.click();
        addDateAccess.click();
        urlDates =  seleniumExample.config.getDriver().getCurrentUrl();

        seleniumExample.config.getDriver().get(urlDates);
        WebElement eventType = seleniumExample.config.getDriver().findElement(By.id("eventType"));
        eventType.click();
        WebElement deadline = seleniumExample.config.getDriver().findElement(By.id("eventDeadline"));
        deadline.click();
        WebElement deadlineName = seleniumExample.config.getDriver().findElement(By.id("eventName"));
        WebElement deadlineStart = seleniumExample.config.getDriver().findElement(By.id("eventStartDate"));
        WebElement deadlineEnd = seleniumExample.config.getDriver().findElement(By.id("eventEndDate"));
        WebElement deadlineDesc = seleniumExample.config.getDriver().findElement(By.id("eventDescription"));
        WebElement dateSave = seleniumExample.config.getDriver().findElement(By.id("dateSave"));
        deadlineName.sendKeys("TestOne");
        deadlineStart.click();
        deadlineStart.sendKeys("2022-06-23");
        deadlineEnd.click();
        deadlineEnd.sendKeys("08:00");
        deadlineDesc.sendKeys("TestOne");
        dateSave.click();
    }

    /**
     *
     */
    @Test
    public void inProject_addSecondDeadline() {

    }

    /**
     *
     */
    @Test
    public void inProject_deleteDeadline() {

    }

    /**
     *
     */
    @Test
    public void inProject_addSprint() {

    }

    /**
     *
     */
    @Test
    public void inProject_addDeadlineSprint2() {

    }

    /**
     *
     */
    @Test
    public void inProject_deleteDeadlineSprint2() {

    }

}
