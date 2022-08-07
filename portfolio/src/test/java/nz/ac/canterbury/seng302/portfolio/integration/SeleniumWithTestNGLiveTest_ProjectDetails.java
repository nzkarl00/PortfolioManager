package nz.ac.canterbury.seng302.portfolio.integration;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


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
    String sprint1Id = "";
    String sprint2Id = "";

    @BeforeSuite
    public void setUp() throws FileNotFoundException, InterruptedException {

        seleniumExample = new SeleniumExample("");

        getPassword_ForAdmin_FromTextFile();
        whenPortfolioIsLoaded_thenLoginAdmin_forTests();

        whenPortfolioIsLoaded_thenCreateNewProject();

        whenPortfolioIsLoaded_thenCreateNewSprint();
         inProject_addDeadline();
        inProject_addMilestone();
        inProject_addSecondDeadline();
        inProject_addThirdDeadline();
        inProject_addSecondMilestone();
        inProject_deleteDeadline();
        inProject_deleteMilestone();

        inProject_addSecondSprint();
        inProject_addDeadlineSprint2();
        inProject_addMilestoneSprint2();
        inProject_deleteDeadlineSprint2();
        inProject_deleteMilestoneSprint2();
        inProject_addBetweenMilestone();

    }

    @AfterSuite
    public void tearDown() {
        seleniumExample.closeWindow();
    }

    /**
     * Gets the password for the pre-generated admin account from the application files
     * @throws FileNotFoundException
     */
    public void getPassword_ForAdmin_FromTextFile() throws FileNotFoundException {
        String originpath = System.getProperty("user.dir");
        File passwordFile = new File(originpath.substring(0, originpath.length()-9) + "identityprovider/defaultAdminPassword.txt");
        Scanner passwordReader = new Scanner(passwordFile);
        passwordText = passwordReader.nextLine();
    }

    /**
     * Takes an element and validates that it returns a tooltip with valid text
     * @param element the element that a tooltip must be found for
     * @param expectedText the text that the tooltip is expected to contain
     */
    public void checkTooltip_isValid(WebElement element, String expectedText) throws InterruptedException {
        Actions actions = new Actions(seleniumExample.config.getDriver());
        actions.moveToElement(element).perform();
        WebElement tooltip = seleniumExample.config.getDriver().findElement(By.className("tooltip-inner"));
        List<WebElement> tooltipList = tooltip.findElements(By.xpath(".//*"));
        String tooltipText = tooltipList.get(0).getText() + tooltipList.get(1).getText() + tooltipList.get(2).getText();
        Assertions.assertEquals(expectedText, tooltipText);
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
     * load up the page then login to the admin user
     */
    public void whenPortfolioIsLoaded_thenCreateNewProject() throws InterruptedException {
        seleniumExample.config.getDriver().get(seleniumExample.url + "/landing");
        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1500);
        WebElement projectButton = seleniumExample.config.getDriver().findElement(By.id("newProject"));
        projectButton.click();
        WebElement projectName = seleniumExample.config.getDriver().findElement(By.id("projectName"));
        WebElement projectStart = seleniumExample.config.getDriver().findElement(By.id("projectStartDate"));
        WebElement projectEnd = seleniumExample.config.getDriver().findElement(By.id("projectEndDate"));
        WebElement projectDesc = seleniumExample.config.getDriver().findElement(By.id("projectDescription"));
        WebElement projectSave = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
        projectName.clear();
        projectName.sendKeys("ProjectOne");
        projectStart.click();
        projectStart.sendKeys("2033-01-01");
        projectEnd.click();
        projectEnd.sendKeys("2034-01-01");
        projectDesc.sendKeys("Project Test Description");
        projectSave.click();
        projectInfoUrl =  seleniumExample.config.getDriver().getCurrentUrl();

        WebElement projectDates = seleniumExample.config.getDriver().findElement(By.id("projectDate"));
        WebElement projectTitle = seleniumExample.config.getDriver().findElement(By.id("projectTitle"));
        Assertions.assertEquals("Start Date: 01/Jan/2033 - End Date: 01/Jan/2034", projectDates.getText());
        Assertions.assertEquals("ProjectOne", projectTitle.getText());

    }

    /**
     * load up the page then login to the admin user
     */
    public void whenPortfolioIsLoaded_thenCreateNewSprint() {
        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccess = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        WebElement addDateAccess = seleniumExample.config.getDriver().findElement(By.id("addDateButton"));
        detailAccess.click();
        addDateAccess.click();
        urlDates =  seleniumExample.config.getDriver().getCurrentUrl();

        seleniumExample.config.getDriver().get(urlDates);
        WebElement eventType = seleniumExample.config.getDriver().findElement(By.id("eventType"));
        eventType.click();
        WebElement sprint = seleniumExample.config.getDriver().findElement(By.id("eventSprint"));
        sprint.click();
        WebElement sprintName = seleniumExample.config.getDriver().findElement(By.id("eventName"));
        WebElement sprintStart = seleniumExample.config.getDriver().findElement(By.id("eventStartDate"));
        WebElement sprintEnd = seleniumExample.config.getDriver().findElement(By.id("eventEndDate"));
        WebElement sprintDesc = seleniumExample.config.getDriver().findElement(By.id("eventDescription"));
        WebElement dateSave = seleniumExample.config.getDriver().findElement(By.id("dateSave"));
        sprintName.sendKeys("SprintOne");
        sprintStart.click();
        sprintStart.sendKeys("2033-01-02");
        sprintEnd.click();
        sprintEnd.sendKeys("2033-03-01");
        sprintDesc.sendKeys("TestOne");
        dateSave.click();

        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();
        WebElement allSprints = seleniumExample.config.getDriver().findElement(By.id("sprints"));
        WebElement firstSprint = allSprints.findElement(By.xpath("/html/body/div[2]/div/div[3]/div[2]/div/div[3]/div[2]/div[3]"));
        WebElement sprintCheckDate = firstSprint.findElement(By.id("sprintDate"));
        WebElement sprintCheckDesc = firstSprint.findElement(By.id("sprintDesc"));
        WebElement sprintCheckName = firstSprint.findElement(By.id("sprintName"));
        Assertions.assertEquals("02/Jan/2033-01/Mar/2033", sprintCheckDate.getText());
        Assertions.assertEquals("Description: TestOne", sprintCheckDesc.getText());
        Assertions.assertEquals("SprintOne", sprintCheckName.getText());
    }


    /**
     * Creates a deadline in the initial sprint
     */
    public void inProject_addDeadline() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
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
        deadlineStart.sendKeys("2033-01-04");
        deadlineEnd.click();
        deadlineEnd.sendKeys("08:00");
        deadlineDesc.sendKeys("TestOne");
        dateSave.click();

        seleniumExample.config.getDriver().get(projectInfoUrl);


        WebElement deadlineCalendar = seleniumExample.config.getDriver().findElement(By.id("Tue Jan 04 2033 deadlineList"));
        String deadlineCalendarString = deadlineCalendar.getText();
        Assertions.assertEquals("D - 1", deadlineCalendarString);

        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();
        WebElement allSprints = seleniumExample.config.getDriver().findElement(By.id("sprints"));
        WebElement firstSprint = allSprints.findElement(By.xpath("/html/body/div[2]/div/div[3]/div[2]/div/div[3]/div[2]"));
        sprint1Id = (firstSprint.getAttribute("id")).substring(6);

        WebElement deadlineList = seleniumExample.config.getDriver().findElement(By.id("deadlinesList"+sprint1Id));
        WebElement firstDeadline = deadlineList.findElement(By.cssSelector("form:first-child"));
        Thread.sleep(1000);
        checkTooltip_isValid(firstDeadline, "TestOneDue: 2033-01-04At: 08:00:00");

        Assertions.assertEquals("TestOne", firstDeadline.getText());
    }

    /**
     * Creates a milestone in the initial sprint
     */
    public void inProject_addMilestone() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccess = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        WebElement addDateAccess = seleniumExample.config.getDriver().findElement(By.id("addDateButton"));
        detailAccess.click();
        addDateAccess.click();
        urlDates =  seleniumExample.config.getDriver().getCurrentUrl();

        seleniumExample.config.getDriver().get(urlDates);
        WebElement eventType = seleniumExample.config.getDriver().findElement(By.id("eventType"));
        eventType.click();
        WebElement deadline = seleniumExample.config.getDriver().findElement(By.id("eventMilestone"));
        deadline.click();
        WebElement milestoneName = seleniumExample.config.getDriver().findElement(By.id("eventName"));
        WebElement milestoneStart = seleniumExample.config.getDriver().findElement(By.id("eventStartDate"));
        WebElement milestoneDesc = seleniumExample.config.getDriver().findElement(By.id("eventDescription"));
        WebElement dateSave = seleniumExample.config.getDriver().findElement(By.id("dateSave"));
        milestoneName.sendKeys("MileOne");
        milestoneStart.click();
        milestoneStart.sendKeys("2033-01-04");
        milestoneDesc.sendKeys("MileOne");
        dateSave.click();

        seleniumExample.config.getDriver().get(projectInfoUrl);

        WebElement milestoneCalendar = seleniumExample.config.getDriver().findElement(By.id("Tue Jan 04 2033 milestoneList"));
        String milestoneCalendarString = milestoneCalendar.getText();
        Assertions.assertEquals("M - 1", milestoneCalendarString);

        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();
        WebElement allSprints = seleniumExample.config.getDriver().findElement(By.id("sprints"));

        WebElement deadlineList = seleniumExample.config.getDriver().findElement(By.id("milestonesList"+sprint1Id));
        WebElement firstDeadline = deadlineList.findElement(By.cssSelector("form:first-child"));

        WebElement milestoneTooltip = firstDeadline;


        Assertions.assertEquals("MileOne", firstDeadline.getText());
    }

    /**
     * Creates a second deadline in the initial sprint
     */
    public void inProject_addSecondDeadline() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
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
        deadlineName.sendKeys("TestTwo");
        deadlineStart.click();
        deadlineStart.sendKeys("2033-01-04");
        deadlineEnd.click();
        deadlineEnd.sendKeys("15:00");
        deadlineDesc.sendKeys("TestTwo");
        dateSave.click();

        seleniumExample.config.getDriver().get(projectInfoUrl);

        WebElement deadlineCalendar = seleniumExample.config.getDriver().findElement(By.id("Tue Jan 04 2033 deadlineList"));
        String deadlineCalendarString = deadlineCalendar.getText();
        Assertions.assertEquals("D - 2", deadlineCalendarString);

        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();

        WebElement allSprints = seleniumExample.config.getDriver().findElement(By.id("sprints"));

        WebElement deadlineList = seleniumExample.config.getDriver().findElement(By.id("deadlinesList"+sprint1Id));
        WebElement firstDeadline = deadlineList.findElement(By.cssSelector("form:nth-child(1)"));
        WebElement secondDeadline = deadlineList.findElement(By.cssSelector("form:nth-child(2)"));

        Assertions.assertEquals("TestOne", firstDeadline.getText());
        Assertions.assertEquals("TestTwo", secondDeadline.getText());

    }

    /**
     * Creates a third deadline in the initial sprint
     */
    public void inProject_addThirdDeadline() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
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
        deadlineName.sendKeys("TestThree");
        deadlineStart.click();
        deadlineStart.sendKeys("2033-01-03");
        deadlineEnd.click();
        deadlineEnd.sendKeys("15:00");
        deadlineDesc.sendKeys("TestThre");
        dateSave.click();

        seleniumExample.config.getDriver().get(projectInfoUrl);

        WebElement deadlineCalendar = seleniumExample.config.getDriver().findElement(By.id("Tue Jan 04 2033 deadlineList"));
        String deadlineCalendarString = deadlineCalendar.getText();
        Assertions.assertEquals("D - 2", deadlineCalendarString);

        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();
        WebElement allSprints = seleniumExample.config.getDriver().findElement(By.id("sprints"));

        WebElement deadlineList = seleniumExample.config.getDriver().findElement(By.id("deadlinesList"+sprint1Id));
        WebElement firstDeadline = deadlineList.findElement(By.cssSelector("form:nth-child(1)"));
        WebElement secondDeadline = deadlineList.findElement(By.cssSelector("form:nth-child(2)"));

        Assertions.assertEquals("TestThree", firstDeadline.getText());
        Assertions.assertEquals("TestOne", secondDeadline.getText());

    }

    /**
     * Creates a second milestone in the initial sprint
     */
    public void inProject_addSecondMilestone() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccess = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        WebElement addDateAccess = seleniumExample.config.getDriver().findElement(By.id("addDateButton"));
        detailAccess.click();
        addDateAccess.click();
        urlDates =  seleniumExample.config.getDriver().getCurrentUrl();

        seleniumExample.config.getDriver().get(urlDates);
        WebElement eventType = seleniumExample.config.getDriver().findElement(By.id("eventType"));
        eventType.click();
        WebElement milestone = seleniumExample.config.getDriver().findElement(By.id("eventMilestone"));
        milestone.click();

        WebElement milestoneName = seleniumExample.config.getDriver().findElement(By.id("eventName"));
        WebElement milestoneStart = seleniumExample.config.getDriver().findElement(By.id("eventStartDate"));
        WebElement milestoneDesc = seleniumExample.config.getDriver().findElement(By.id("eventDescription"));
        WebElement dateSave = seleniumExample.config.getDriver().findElement(By.id("dateSave"));
        milestoneName.sendKeys("MileTwo");
        milestoneStart.click();
        milestoneStart.sendKeys("2033-01-04");
        milestoneDesc.sendKeys("TestTwo");
        dateSave.click();

        seleniumExample.config.getDriver().get(projectInfoUrl);



        WebElement milestoneCalendar = seleniumExample.config.getDriver().findElement(By.id("Tue Jan 04 2033 milestoneList"));
        String milestoneCalendarString = milestoneCalendar.getText();
        Assertions.assertEquals("M - 2", milestoneCalendarString);

        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();


        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(100);

        WebElement allSprints = seleniumExample.config.getDriver().findElement(By.id("sprints"));

        WebElement milestoneList = seleniumExample.config.getDriver().findElement(By.id("milestonesList"+sprint1Id));
        WebElement firstMilestone = milestoneList.findElement(By.cssSelector("form:nth-child(1)"));
        WebElement secondMilestone = milestoneList.findElement(By.cssSelector("form:nth-child(2)"));

        Assertions.assertEquals("MileOne", firstMilestone.getText());
        Assertions.assertEquals("MileTwo", secondMilestone.getText());

    }


    /**
     * Delete a deadline in the first sprint
     */
    public void inProject_deleteDeadline() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();
        WebElement allSprints = seleniumExample.config.getDriver().findElement(By.id("sprints"));

        WebElement deadlineList = seleniumExample.config.getDriver().findElement(By.id("deadlinesList"+sprint1Id));
        WebElement firstDeadline = deadlineList.findElement(By.cssSelector("form:nth-child(2)"));
        WebElement deleteDeadline = firstDeadline.findElement(By.id("deleteButton"));
        deleteDeadline.click();

        WebElement deadlineCalendar = seleniumExample.config.getDriver().findElement(By.id("Tue Jan 04 2033 deadlineList"));
        String deadlineCalendarString = deadlineCalendar.getText();
        Assertions.assertEquals("D - 1", deadlineCalendarString);

        detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();
        WebElement deadlineList2 = seleniumExample.config.getDriver().findElement(By.id("deadlinesList"+sprint1Id));
        WebElement initialDeadline = deadlineList2.findElement(By.cssSelector("form:nth-child(2)"));

        Assertions.assertEquals("TestTwo", initialDeadline.getText());

    }

    /**
     * Delete a milestone in the first sprint
     */
    public void inProject_deleteMilestone() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();


        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);

        WebElement allSprints = seleniumExample.config.getDriver().findElement(By.id("sprints"));

        WebElement milestoneList = seleniumExample.config.getDriver().findElement(By.id("milestonesList"+sprint1Id));
        WebElement firstMilestone = milestoneList.findElement(By.cssSelector("form:first-child"));
        WebElement deleteMilestone = firstMilestone.findElement(By.id("deleteButton"));
        deleteMilestone.click();

        WebElement milestoneCalendar = seleniumExample.config.getDriver().findElement(By.id("Tue Jan 04 2033 milestoneList"));
        String milestoneCalendarString = milestoneCalendar.getText();
        Assertions.assertEquals("M - 1", milestoneCalendarString);

        detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);

        WebElement milestoneList2 = seleniumExample.config.getDriver().findElement(By.id("milestonesList"+sprint1Id));
        WebElement secondMilestone = milestoneList2.findElement(By.cssSelector("form:nth-child(1)"));

        Assertions.assertEquals("MileTwo", secondMilestone.getText());

    }


    /**
     * Create a second sprint occurring after the first one
     */
    public void inProject_addSecondSprint() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccess = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        WebElement addDateAccess = seleniumExample.config.getDriver().findElement(By.id("addDateButton"));
        detailAccess.click();
        addDateAccess.click();
        urlDates =  seleniumExample.config.getDriver().getCurrentUrl();

        seleniumExample.config.getDriver().get(urlDates);
        WebElement eventType = seleniumExample.config.getDriver().findElement(By.id("eventType"));
        eventType.click();
        WebElement sprint = seleniumExample.config.getDriver().findElement(By.id("eventSprint"));
        sprint.click();
        WebElement sprintName = seleniumExample.config.getDriver().findElement(By.id("eventName"));
        WebElement sprintStart = seleniumExample.config.getDriver().findElement(By.id("eventStartDate"));
        WebElement sprintEnd = seleniumExample.config.getDriver().findElement(By.id("eventEndDate"));
        WebElement sprintDesc = seleniumExample.config.getDriver().findElement(By.id("eventDescription"));
        WebElement dateSave = seleniumExample.config.getDriver().findElement(By.id("dateSave"));

        sprintName.sendKeys("SprintTwo");
        sprintStart.click();
        sprintStart.sendKeys("2033-04-01");
        sprintEnd.click();
        sprintEnd.sendKeys("2033-05-01");
        sprintDesc.sendKeys("TestTwo");
        dateSave.click();

        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);
        WebElement allSprints = seleniumExample.config.getDriver().findElement(By.id("sprints"));
        sprint2Id = String.valueOf(Integer.parseInt(sprint1Id)+6);
        WebElement secondSprint = allSprints.findElement(By.id("sprint"+sprint2Id));
        WebElement sprintCheckDate = secondSprint.findElement(By.id("sprintDate"));
        WebElement sprintCheckDesc = secondSprint.findElement(By.id("sprintDesc"));
        WebElement sprintCheckName = secondSprint.findElement(By.id("sprintName"));
        Assertions.assertEquals("01/Apr/2033-01/May/2033", sprintCheckDate.getText());
        Assertions.assertEquals("Description: TestTwo", sprintCheckDesc.getText());
        Assertions.assertEquals("SprintTwo", sprintCheckName.getText());

    }

    /**
     * Add a deadline to the second sprint
     */
    public void inProject_addDeadlineSprint2() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
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
        deadlineName.sendKeys("TestThree");
        deadlineStart.click();
        deadlineStart.sendKeys("2033-04-05");
        deadlineEnd.click();
        deadlineEnd.sendKeys("07:00");
        deadlineDesc.sendKeys("TestThree");
        dateSave.click();

        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);

        WebElement deadlineList = seleniumExample.config.getDriver().findElement(By.id("deadlinesList"+sprint2Id));
        WebElement firstDeadline = deadlineList.findElement(By.cssSelector("form:first-child"));

        Assertions.assertEquals("TestThree", firstDeadline.getText());

    }

    /**
     * Add a milestone to the second sprint
     */
    public void inProject_addMilestoneSprint2() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccess = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        WebElement addDateAccess = seleniumExample.config.getDriver().findElement(By.id("addDateButton"));
        detailAccess.click();
        addDateAccess.click();
        urlDates =  seleniumExample.config.getDriver().getCurrentUrl();

        seleniumExample.config.getDriver().get(urlDates);
        WebElement eventType = seleniumExample.config.getDriver().findElement(By.id("eventType"));
        eventType.click();
        WebElement milestone = seleniumExample.config.getDriver().findElement(By.id("eventMilestone"));
        milestone.click();
        WebElement milestoneName = seleniumExample.config.getDriver().findElement(By.id("eventName"));
        WebElement milestoneStart = seleniumExample.config.getDriver().findElement(By.id("eventStartDate"));
        WebElement milestoneDesc = seleniumExample.config.getDriver().findElement(By.id("eventDescription"));
        WebElement dateSave = seleniumExample.config.getDriver().findElement(By.id("dateSave"));
        milestoneName.sendKeys("MileThree");
        milestoneStart.click();
        milestoneStart.sendKeys("2033-04-05");
        milestoneDesc.sendKeys("TestThree");
        dateSave.click();

        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);

        WebElement milestoneList = seleniumExample.config.getDriver().findElement(By.id("milestonesList"+sprint2Id));
        WebElement firstMilestone = milestoneList.findElement(By.cssSelector("form:first-child"));


        Assertions.assertEquals("MileThree", firstMilestone.getText());

    }



    /**
     * Delete a deadline from the second Sprint
     */
    public void inProject_deleteDeadlineSprint2() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);
        WebElement allSprints = seleniumExample.config.getDriver().findElement(By.id("sprints"));

        WebElement deadlineList = seleniumExample.config.getDriver().findElement(By.id("deadlinesList"+sprint2Id));
        WebElement firstDeadline = deadlineList.findElement(By.cssSelector("form:first-child"));
        WebElement deleteDeadline = firstDeadline.findElement(By.id("deleteButton"));
        deleteDeadline.click();

        detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);

        WebElement deadlinesDeleted = seleniumExample.config.getDriver().findElement( By.id("deadlinesList"+sprint2Id));

        boolean deadlineList2 = deadlinesDeleted.findElements(By.cssSelector("form")).size() != 0;
        Assertions.assertFalse(deadlineList2);

    }

    /**
     * Delete a milestone from the second Sprint
     */
    public void inProject_deleteMilestoneSprint2() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);
        WebElement allSprints = seleniumExample.config.getDriver().findElement(By.id("sprints"));

        WebElement milestoneList = seleniumExample.config.getDriver().findElement(By.id("milestonesList"+sprint2Id));
        WebElement firstMilestone = milestoneList.findElement(By.cssSelector("form:first-child"));
        WebElement deleteMilestone = firstMilestone.findElement(By.id("deleteButton"));
        deleteMilestone.click();

        detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);

        WebElement milestonesDeleted = seleniumExample.config.getDriver().findElement( By.id("milestonesList"+sprint2Id));

        boolean milestoneList2 = milestonesDeleted.findElements(By.cssSelector("form")).size() != 0;
        Assertions.assertFalse(milestoneList2);

    }

    /**
     * Add a between-sprint item
     */
    public void inProject_addBetweenMilestone() throws InterruptedException {
        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccess = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        WebElement addDateAccess = seleniumExample.config.getDriver().findElement(By.id("addDateButton"));
        detailAccess.click();
        addDateAccess.click();
        urlDates =  seleniumExample.config.getDriver().getCurrentUrl();

        seleniumExample.config.getDriver().get(urlDates);
        WebElement eventType = seleniumExample.config.getDriver().findElement(By.id("eventType"));
        eventType.click();
        WebElement milestone = seleniumExample.config.getDriver().findElement(By.id("eventMilestone"));
        milestone.click();
        WebElement milestoneName = seleniumExample.config.getDriver().findElement(By.id("eventName"));
        WebElement milestoneStart = seleniumExample.config.getDriver().findElement(By.id("eventStartDate"));
        WebElement milestoneDesc = seleniumExample.config.getDriver().findElement(By.id("eventDescription"));
        WebElement dateSave = seleniumExample.config.getDriver().findElement(By.id("dateSave"));
        milestoneName.sendKeys("MileFour");
        milestoneStart.click();
        milestoneStart.sendKeys("2033-03-19");
        milestoneDesc.sendKeys("TestFour");
        dateSave.click();

        seleniumExample.config.getDriver().get(projectInfoUrl);
        WebElement detailAccessCheck = seleniumExample.config.getDriver().findElement(By.id("toDetails"));
        detailAccessCheck.click();
//*
        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);

        WebElement milestoneList = seleniumExample.config.getDriver().findElement(By.id("milestonesList1.5"));
        WebElement firstMilestone = milestoneList.findElement(By.cssSelector("form:first-child"));


        Assertions.assertEquals("MileFour", firstMilestone.getText());

    }

}
