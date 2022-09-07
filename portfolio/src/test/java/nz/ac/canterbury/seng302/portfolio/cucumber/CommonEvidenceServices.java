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

public class CommonEvidenceServices {
    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;
    WebDriver driver = seleniumExample.config.getDriver();

    public CommonEvidenceServices() {
    }

    /**
     *Gets the evidence id for the users evidence
     * @return evidence id - type String
     **/
    public String getEvidenceId(String title) {
        List<WebElement> elementsList = seleniumExample.config.getDriver().findElements(By.xpath("//*[contains(text(),'" + title + "')]"));
        return elementsList.get(0).getAttribute("id");
    }

    /**
     *Opens a piece of evidence
     **/
    public void viewFullPieceOfEvidence() throws InterruptedException {
        String getId = getEvidenceId("Evidence Delete");
        new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton"+getId)));
        WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + getId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, arrowButton);
        arrowButton.click();
    }
    /**
     *Opens a piece of evidence
     **/
    public void viewFullPieceOfEvidence(String arg0) throws InterruptedException {
        String getId = getEvidenceId(arg0);
        new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton"+getId)));
        WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + getId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, arrowButton);
        arrowButton.click();
    }

    @And("I view that piece of evidence {string}")
    public void iViewThatPieceOfEvidence(String arg0) throws InterruptedException {
        viewFullPieceOfEvidence(arg0);
    }

    @When("I view that piece of evidence")
    public void iViewThatPieceOfEvidence() throws InterruptedException {
        viewFullPieceOfEvidence();
    }

    @When("I view that piece of evidence that is not mine")
    public void iViewThatPieceOfEvidenceThatIsNotMine() throws InterruptedException {
        viewFullPieceOfEvidence();
    }
}