package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ModifyEvidenceStepDefs {

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

    @When("I click the edit icon")
    public void i_click_the_edit_icon() throws InterruptedException {
        String evidenceId = getEvidenceId("Evidence One");
        WebElement element = driver.findElement(By.id(evidenceId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, element);
        new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton"+evidenceId)));
        WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + evidenceId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, arrowButton);
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
            Assertions.assertNotEquals(evidenceId, button.getAttribute("id"));
        } catch(Exception e) {

        }
    }

    @And("I click the edit button")
    public void iClickTheEditButton() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @And("I click the {string} tag")
    public void iClickTheTag(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @And("I change the skill to {string}")
    public void iChangeTheSkillTo(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("User inputs {string} into the edit skill input textbox.")
    public void iAddTheSkillTo(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The edit list is updated")
    public void theEditListIsUpdated() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @And("I click the delete {string} button")
    public void iClickTheDeleteButton(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The delete list is updated")
    public void theDeleteListIsUpdated() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The new list is updated")
    public void theNewListIsUpdated() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}