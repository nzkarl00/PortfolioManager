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
    CommonEvidenceServices commonEvidenceServices = new CommonEvidenceServices();

    @Then("I can see a delete icon")
    public void iCanSeeADeleteIcon() throws InterruptedException {
        String evidenceId = commonEvidenceServices.getEvidenceId("Evidence Delete");
        WebElement element = driver.findElement(By.id(evidenceId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, element);
        new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton"+evidenceId)));
        WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + evidenceId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, arrowButton);
        arrowButton.click();
    }

    @Then("I can click the delete Icon")
    public void iCanClickTheDeleteIcon() throws InterruptedException {
        String evidenceId = commonEvidenceServices.getEvidenceId("Evidence One");
        WebElement element = driver.findElement(By.id(evidenceId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, element);
        new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton"+evidenceId)));
        WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + evidenceId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, arrowButton);
        arrowButton.click();

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight/8)");
        Thread.sleep(100);
        WebElement button = seleniumExample.config.getDriver().findElement(By.className("delete_button"));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, button);
        Thread.sleep(200);
        button.click();
    }


    @Then("I cannot see a delete icon")
    public void iCannotSeeADeleteIcon() {
        String evidenceId = commonEvidenceServices.getEvidenceId("Evidence Delete");
        try {
            WebElement button = seleniumExample.config.getDriver().findElement(By.className("delete_button"));
            Assertions.assertNotEquals(evidenceId, button.getAttribute("id"));
        } catch(Exception e) {

        }
    }

    @Then("A model appears containing the evidence title")
    public void aModelAppearsContainingTheEvidenceTitle() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        WebElement modelTitle = seleniumExample.config.getDriver().findElement(By.id("exampleModalLongTitle"));
        String expected = "Delete - Evidence One?";
        Assertions.assertEquals(expected, modelTitle.getText());

    }

    @When("I click cancel")
    public void iClickCancel() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        WebElement button = seleniumExample.config.getDriver().findElement(By.id("CancelButton"));
        button.click();
    }

    @When("I fill out all mandatory fields for delete")
    public void iFillOutAllMandatoryFieldsDelete() throws InterruptedException {
        WebElement titleField = seleniumExample.config.getDriver().findElement(By.id("evidence_title"));
        titleField.sendKeys("Evidence Delete");
        WebElement description = seleniumExample.config.getDriver().findElement(By.id("evidence_desc"));
        description.sendKeys("This evidence relates to the work done on the evidence page");
        WebElement date = seleniumExample.config.getDriver().findElement(By.id("date_input"));
        String dateToSend = date.getAttribute("min");
        date.sendKeys(dateToSend);
        Thread.sleep(1000);
    }
}
