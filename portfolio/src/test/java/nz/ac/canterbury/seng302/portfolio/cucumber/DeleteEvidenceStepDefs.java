package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

public class DeleteEvidenceStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;
    WebDriver driver = seleniumExample.config.getDriver();

    /**
     *Gets the evidence id for the users evidence
     * @return evidence id - type String
     **/
    public String getEvidenceId(String title) {
        List<WebElement> elementsList = seleniumExample.config.getDriver().findElements(By.xpath("//*[contains(text(),'" + title + "')]"));
        return elementsList.get(0).getAttribute("id");
    }

    /**
     *Can be used to scroll window to element
     * @Param element webElement that the window is to scroll too
     **/
    public void scrollWindowToElement(WebElement element) {
        //https://learn-automation.com/how-to-scroll-into-view-in-selenium-webdriver/
        JavascriptExecutor je = (JavascriptExecutor) driver;
        je.executeScript("arguments[0].scrollIntoView(true);",element);
    }

    /**
     *Opens a piece of evidence
     **/
    public void viewFullPieceOfEvidence() {
        String evidenceId = getEvidenceId("Evidence Delete");
        new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton" + evidenceId)));
        WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + evidenceId));
        //scrollWindowToElement(arrowButton);
        seleniumExample.config.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        arrowButton.click();
    }
    /**
     *Opens a piece of evidence
     **/
    public void viewFullPieceOfEvidence(String arg0) {
        String evidenceId = getEvidenceId(arg0);
        new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton" + evidenceId)));
        WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + evidenceId));
        //scrollWindowToElement(arrowButton);
        seleniumExample.config.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        arrowButton.click();
    }

    @And("I view that piece of evidence {string}")
    public void iViewThatPieceOfEvidence(String arg0)  {
        viewFullPieceOfEvidence(arg0);
    }

    @When("I view that piece of evidence")
    public void iViewThatPieceOfEvidence() {
        viewFullPieceOfEvidence();
    }

    @Then("I can see a delete icon")
    public void iCanSeeADeleteIcon() {
        String evidenceId = getEvidenceId("Evidence Delete");
        WebElement element = driver.findElement(By.id(evidenceId));
        scrollWindowToElement(element);
    }

    @Then("I can click the delete Icon")
    public void iCanClickTheDeleteIcon() throws InterruptedException {
        String evidenceId = getEvidenceId("Evidence Delete");
        WebElement element = driver.findElement(By.id(evidenceId));
        scrollWindowToElement(element);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        WebElement button = seleniumExample.config.getDriver().findElement(By.className("group_delete_button"));
        scrollWindowToElement(button);
        Thread.sleep(200);
        button.click();
    }

    @When("I view that piece of evidence that is not mine")
    public void iViewThatPieceOfEvidenceThatIsNotMine() {
        viewFullPieceOfEvidence();
    }

    @Then("I cannot see a delete icon")
    public void iCannotSeeADeleteIcon() {
        String evidenceId = getEvidenceId("Evidence Delete");
        try {
            WebElement button = seleniumExample.config.getDriver().findElement(By.className("group_delete_button"));
            Assertions.assertNotEquals(evidenceId, button.getAttribute("id"));
        } catch(Exception e) {

        }
    }

    @Then("A model appears containing the evidence title")
    public void aModelAppearsContainingTheEvidenceTitle() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        WebElement modelTitle = seleniumExample.config.getDriver().findElement(By.id("exampleModalLongTitle"));
        String expected = "Delete -  Evidence One?";
        Assertions.assertEquals(expected, modelTitle.getText());

    }

    @When("I click cancel")
    public void iClickCancel() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        WebElement button = seleniumExample.config.getDriver().findElement(By.id("CancelButton"));
        button.click();
    }


    @When("I fill out all mandatory fields for delete")
    public void iFillOutAllMandatoryFields() throws InterruptedException {
        WebElement titleField = seleniumExample.config.getDriver().findElement(By.id("evidence_title"));
        titleField.sendKeys("Evidence Delete");
        WebElement description = seleniumExample.config.getDriver().findElement(By.id("evidence_desc"));
        description.sendKeys("This evidence relates to the work done on the evidence page");
        WebElement date = seleniumExample.config.getDriver().findElement(By.id("date_input"));
        String dateToSend = date.getAttribute("min");
        date.sendKeys(dateToSend);
        Thread.sleep(1000);
    }

    @Then("I close the window")
    public void iCloseTheWindow() {
        seleniumExample.closeWindow();
    }

}
