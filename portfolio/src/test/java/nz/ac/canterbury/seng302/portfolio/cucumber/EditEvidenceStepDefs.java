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
import java.util.List;

public class EditEvidenceStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;
    WebDriver driver = seleniumExample.config.getDriver();

    CommonEvidenceServices commonEvidenceServices = new CommonEvidenceServices();

    /**
     *Gets the evidence id for the users evidence
     * @return evidence id - type String
     **/
    public String getEvidenceId(String title) {
        List<WebElement> elementsList = seleniumExample.config.getDriver().findElements(By.xpath("//*[contains(text(),'" + title + "')]"));
        return elementsList.get(0).getAttribute("id");
    }

    @When("I click the edit icon")
    public void i_click_the_edit_icon() throws InterruptedException {
        String evidenceId = getEvidenceId("Evidence One");
        WebElement element = driver.findElement(By.id(evidenceId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, element);
        new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton"+evidenceId)));
        WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + evidenceId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, arrowButton);
        arrowButton.click();

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight/8)");
        Thread.sleep(100);
        WebElement button = seleniumExample.config.getDriver().findElement(By.id("editButton" + evidenceId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, button);
        Thread.sleep(200);
        button.click();
        Thread.sleep(1000);
    }

    @Then("I cannot click the edit icon")
    public void i_cannot_click_the_edit_icon() {
        String evidenceId = getEvidenceId("Evidence One");
        WebElement element = driver.findElement(By.id(evidenceId));
        try {
            BaseSeleniumStepDefs.scrollWindowToElement(driver, element);
            new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton"+evidenceId)));
            WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + evidenceId));
            BaseSeleniumStepDefs.scrollWindowToElement(driver, arrowButton);
            arrowButton.click();

            ((JavascriptExecutor) seleniumExample.config.getDriver())
                    .executeScript("window.scrollTo(0, document.body.scrollHeight/8)");
            Thread.sleep(100);
            WebElement button = seleniumExample.config.getDriver().findElement(By.id("editButton" + evidenceId));
        } catch(Exception e) {

        }
    }
}