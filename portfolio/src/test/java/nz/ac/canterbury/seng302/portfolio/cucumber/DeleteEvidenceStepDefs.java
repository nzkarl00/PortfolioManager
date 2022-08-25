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
        String getId = getEvidenceId("Evidence One");
        new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton"+getId)));
        WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + getId));
        //scrollWindowToElement(arrowButton);
        seleniumExample.config.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        arrowButton.click();
    }

    @And("I view that piece of evidence {string}")
    public void iViewThatPieceOfEvidence(String arg0) {
        viewFullPieceOfEvidence();
    }

    @When("I view that piece of evidence")
    public void i_view_that_piece_of_evidence() {
        viewFullPieceOfEvidence();
    }

    @Then("I can see a delete icon")
    public void i_can_see_a_delete_icon() {
        String getId = getEvidenceId("Evidence One");
        WebElement element = driver.findElement(By.id(getId));
        scrollWindowToElement(element);
    }

    @Then("I can click the delete Icon")
    public void i_can_click_the_delete_icon() {
        String getId = getEvidenceId("Evidence One");
        WebElement element = driver.findElement(By.id(getId));
        scrollWindowToElement(element);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        WebElement button = seleniumExample.config.getDriver().findElement(By.className("group_delete_button"));
        button.click();
    }

    @When("I view that piece of evidence that is not mine")
    public void i_view_that_piece_of_evidence_that_is_not_mine() {
        viewFullPieceOfEvidence();
    }

    @Then("I cannot see a delete icon")
    public void i_cannot_see_a_delete_icon() {
        String getId = getEvidenceId("Evidence One");
        try {
            WebElement button = seleniumExample.config.getDriver().findElement(By.className("group_delete_button"));
            Assertions.assertNotEquals(getId, button.getAttribute("id"));
        } catch(Exception e) {

        }
    }

    @Then("A model appears containing the evidence title")
    public void a_model_appears_containing_the_evidence_title() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        WebElement modelTitle = seleniumExample.config.getDriver().findElement(By.id("exampleModalLongTitle"));
        String expected = "Delete - Evidence One?";
        Assertions.assertEquals(expected, modelTitle.getText());

    }

    @When("I click cancel")
    public void i_click_cancel() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        WebElement button = seleniumExample.config.getDriver().findElement(By.id("CancelButton"));
        button.click();
    }


    @Then("I fill out all mandatory fields")
    public void i_fill_out_all_mandatory_fields() {
        WebElement titleField = seleniumExample.config.getDriver().findElement(By.id("evidence_title"));
        titleField.sendKeys("Added AC Testing For Delete Icon");
        WebElement description = seleniumExample.config.getDriver().findElement(By.id("evidence_desc"));
        description.sendKeys("This evidence relates to the work done on the evidence page");
        WebElement date = seleniumExample.config.getDriver().findElement(By.id("date_input"));
        String dateToSend = date.getAttribute("min");
        date.sendKeys(dateToSend);
    }

    @Then("I close the window")
    public void i_close_the_window() {
        seleniumExample.closeWindow();
    }

}
