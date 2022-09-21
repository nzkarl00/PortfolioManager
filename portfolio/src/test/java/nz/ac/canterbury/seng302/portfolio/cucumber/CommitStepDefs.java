package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class CommitStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;


    @When("I open the Add Commit Form")
    public void iOpenTheAddCommitForm() throws InterruptedException {

        Thread.sleep(500);
        WebElement commitbutton = seleniumExample.config.getDriver().findElement(By.id("commit_group"));
        Select commitSelect = new Select(seleniumExample.config.getDriver().findElement(By.id("commit_group")));


        ((JavascriptExecutor) seleniumExample.config.getDriver()).executeScript("arguments[0].scrollIntoView(true);", commitbutton);

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollBy(0, 600)");
        Thread.sleep(500);

        scrollIntoView(commitbutton);
        Thread.sleep(500);
        commitSelect.selectByVisibleText("Group Repo");
        Thread.sleep(500);
    }

    @When("I select a group without a repository")
    public void iSelectAGroupWithNoRepo() throws InterruptedException {

        Thread.sleep(500);
        WebElement commitbutton = seleniumExample.config.getDriver().findElement(By.id("commit_group"));
        Select commitSelect = new Select(seleniumExample.config.getDriver().findElement(By.id("commit_group")));


        ((JavascriptExecutor) seleniumExample.config.getDriver()).executeScript("arguments[0].scrollIntoView(true);", commitbutton);

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollBy(0, 600)");
        Thread.sleep(500);

        scrollIntoView(commitbutton);
        Thread.sleep(500);
        commitSelect.selectByVisibleText("GroupNoRep");
        Thread.sleep(500);
    }

    @When("I select a group with a repository")
    public void iSelectAGroupWithRepo() throws InterruptedException {

        Thread.sleep(500);
        WebElement commitbutton = seleniumExample.config.getDriver().findElement(By.id("commit_group"));
        Select commitSelect = new Select(seleniumExample.config.getDriver().findElement(By.id("commit_group")));


        ((JavascriptExecutor) seleniumExample.config.getDriver()).executeScript("arguments[0].scrollIntoView(true);", commitbutton);

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollBy(0, 600)");
        Thread.sleep(500);

        scrollIntoView(commitbutton);
        Thread.sleep(500);
        commitSelect.selectByVisibleText("Group Repo");
        Thread.sleep(500);
    }

    @When("I select the no group option")
    public void iSelectNoGroup() throws InterruptedException {

        Thread.sleep(500);
        WebElement commitbutton = seleniumExample.config.getDriver().findElement(By.id("commit_group"));
        Select commitSelect = new Select(seleniumExample.config.getDriver().findElement(By.id("commit_group")));


        ((JavascriptExecutor) seleniumExample.config.getDriver()).executeScript("arguments[0].scrollIntoView(true);", commitbutton);

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollBy(0, 600)");
        Thread.sleep(500);

        scrollIntoView(commitbutton);
        Thread.sleep(500);
        commitSelect.selectByVisibleText("Select A Group");
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

    @And("I search for evidence")
    public void iSearchForEvidence() {
    }

    @Then("I add commit {string}")
    public void iAddCommit(String arg0) {
    }

    @Then("I enter the appropriate mandatory attributes for {string}")
    public void iEnterTheAppropriateMandatoryAttributesFor(String arg0) {
    }

    @Then("There is the commit saved")
    public void thereIsTheCommitSaved() {
    }

    @Then("I delete commit {string}")
    public void iDeleteCommit(String arg0) {
    }

    @Then("There is not the commit saved")
    public void thereIsNotTheCommitSaved() {
    }

    @Then("There are the commits saved")
    public void thereAreTheCommitsSaved() {
    }

    @Then("I can see a message showing I need to add a valid group")
    public void searchCommitCantBeSeen() throws InterruptedException {
        Thread.sleep(500);
        WebElement alert_message = seleniumExample.config.getDriver().findElement(By.id("commit_group_alert_message"));
        scrollIntoView(alert_message);
        Thread.sleep(500);
        Assertions.assertEquals(true, alert_message.isDisplayed());
    }

    @Then("I can see the search form and search button")
    public void searchCommitIsVisible() throws InterruptedException {
        Thread.sleep(500);
        WebElement searchButton = seleniumExample.config.getDriver().findElement(By.id("searchCommit"));
        scrollIntoView(searchButton);
        Thread.sleep(500);
        Assertions.assertEquals(true, searchButton.isDisplayed());
    }
}
