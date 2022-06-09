package nz.ac.canterbury.seng302.portfolio;

import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.testng.annotations.Test;
import org.openqa.selenium.Keys;

import java.util.List;

public class SeleniumAddDatesTitleLengthTest {
    SeleniumConfig config;
    @LocalServerPort
    private int port = 9000;
    public String url = "http://localhost:" + port;
    public String url2 = "http://localhost:" + port + "/landing";
    public String ur3 = "http://localhost:" + port + "/details?id=1";
    public String urlDates;

    public SeleniumAddDatesTitleLengthTest(String contextPath) {
        config = new SeleniumConfig();
        config.getDriver().get(url + contextPath);
        whenPortfolioIsLoaded_thenLoginWorks();
        whenPortfolioIsLoaded_thenAccessProjectInfo();
        whenProjectIsAccessed_thenGoToAddDates();
        whenAddingDate_CheckTitleLen();
    }

    public void closeWindow() {
        this.config.getDriver().close();
    }

    /**
     * load up the page then login to the user FAKE, with the set password, note if this is not on your machine you will get errors
     */
    @Test
    public void whenPortfolioIsLoaded_thenLoginWorks() {
        config.getDriver().get(url);
        WebElement username = config.getDriver().findElement(By.id("username"));
        username.sendKeys("FAKE");
        WebElement password = config.getDriver().findElement(By.id("password"));
        password.sendKeys("password");
        WebElement loginButton = config.getDriver().findElement(By.id("login-button"));
        loginButton.click();
        WebElement fullName = config.getDriver().findElement(By.id("full-name"));
        Assertions.assertEquals("Lane Edwards-Brown", fullName.getText());
    }

    /**
     * access the landing page and click to the project details for the first project
     */
    @Test
    public void whenPortfolioIsLoaded_thenAccessProjectInfo() {
        config.getDriver().get(url2);
        WebElement projectButton = config.getDriver().findElement(By.id("projectRedirect"));
        WebElement projectName = config.getDriver().findElement(By.id("projectName"));
        projectButton.click();
        WebElement projectFullName = config.getDriver().findElement(By.id("projectFullName"));
        Assertions.assertEquals(projectFullName.getText(), projectName.getText());
    }

    /**
     * access the add dates page for the project
     */
    @Test
    public void whenProjectIsAccessed_thenGoToAddDates() {
        WebElement detailAccess = config.getDriver().findElement(By.id("toDetails"));
        detailAccess.click();
        WebElement addDateAccess = config.getDriver().findElement(By.id("addDateButton"));
        addDateAccess.click();
        urlDates =  config.getDriver().getCurrentUrl();
    }

    /**
     * enter title and check length, see how the alert message changes when adding/removing characters and switching
     * between different event types of different max title lengths
     */
    @Test
    public void whenAddingDate_CheckTitleLen() {
        config.getDriver().get(urlDates);
        WebElement dateLen = config.getDriver().findElement(By.id("maxLen"));
        WebElement dateName = config.getDriver().findElement(By.id("eventName"));
        Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 100");
        dateName.sendKeys("12345");
        Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 95");
        dateName.clear();
        dateName.sendKeys("123456789012345");
        Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 85");
        dateName.sendKeys(Keys.BACK_SPACE);
        dateName.sendKeys(Keys.BACK_SPACE);
        Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 87");
        WebElement eventType = config.getDriver().findElement(By.id("eventType"));
        eventType.click();
        WebElement deadline = config.getDriver().findElement(By.id("eventDeadline"));
        deadline.click();
        Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 47");
        eventType.click();
        WebElement sprint = config.getDriver().findElement(By.id("eventSprint"));
        sprint.click();
        Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 87");
        dateName.sendKeys("This is a long title to remove characters for testing");
        eventType.click();
        WebElement milestone = config.getDriver().findElement(By.id("evenMilestone"));
        milestone.click();
        Assertions.assertEquals(dateLen.getText(), "Your title is 6 characters too long");
        eventType.click();
        sprint.click();
        Assertions.assertEquals(dateLen.getText(), "Characters Remaining: 34");
    }

}
