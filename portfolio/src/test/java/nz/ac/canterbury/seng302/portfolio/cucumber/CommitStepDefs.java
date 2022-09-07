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
import org.openqa.selenium.support.ui.Select;

import java.util.Calendar;
import java.util.Date;

public class CommitStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;


    @When("I open the Add Commit Form")
    public void iOpenTheAddCommitForm() throws InterruptedException {

        Thread.sleep(500);
        WebElement commitbutton = seleniumExample.config.getDriver().findElement(By.id("ArrowButtonCommit"));

        ((JavascriptExecutor) seleniumExample.config.getDriver()).executeScript("arguments[0].scrollIntoView(true);", commitbutton);

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollBy(0, 600)");
        Thread.sleep(500);

        scrollIntoView(commitbutton);
        Thread.sleep(500);
        commitbutton.click();
        Thread.sleep(500);
    }
    @When("I then open the Add Commit Form")
    public void iThenOpenTheAddCommitForm() throws InterruptedException {

        Thread.sleep(500);
        WebElement newcommitbutton = seleniumExample.config.getDriver().findElement(By.id("ArrowButtonCommit"));

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollBy(0, 1500)");
        Thread.sleep(500);

        Thread.sleep(500);
        newcommitbutton.click();
        Thread.sleep(500);
    }

    public void scrollIntoView(WebElement element) throws InterruptedException {
        ((JavascriptExecutor) seleniumExample.config.getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);
    }

    @And("I type {string} in the username box")
    public void userEntersIntoTheUserSearch(String arg0) throws InterruptedException {
        WebElement userInput = seleniumExample.config.getDriver().findElement(By.id("userCommitInput"));
        scrollIntoView(userInput);
        Thread.sleep(500);
        userInput.clear();
        userInput.sendKeys(arg0);
    }

    @And("I delete text in the username box")
    public void userDeletesTheUserSearch() throws InterruptedException {
        WebElement userInput = seleniumExample.config.getDriver().findElement(By.id("userCommitInput"));
        scrollIntoView(userInput);
        Thread.sleep(500);
        userInput.clear();
    }

    @And("I type {string} in the hash box")
    public void userEntersIntoTheHashSearch(String arg0) throws InterruptedException {
        WebElement hashInput = seleniumExample.config.getDriver().findElement(By.id("hashCommitInput"));
        scrollIntoView(hashInput);
        Thread.sleep(500);
        hashInput.clear();
        hashInput.sendKeys(arg0);
    }

    @Then("I can click the add commit button")
    public void commitSearchCanBeClicked() throws InterruptedException {
        Thread.sleep(500);
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("searchCommit"));
        scrollIntoView(saveButton);
        Thread.sleep(500);
        Assertions.assertNull(saveButton.getAttribute("disabled"));
    }

    @Then("I can't click the add commit button")
    public void commitSearchCannotBeClicked() throws InterruptedException {
        Thread.sleep(500);
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("searchCommit"));
        scrollIntoView(saveButton);
        Thread.sleep(500);
        Assertions.assertEquals("true", saveButton.getAttribute("disabled"));
    }
}
