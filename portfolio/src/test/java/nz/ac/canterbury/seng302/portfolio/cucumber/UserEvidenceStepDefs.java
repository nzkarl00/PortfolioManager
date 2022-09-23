package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

public class UserEvidenceStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;

    @Then("There will be me displayed as a author")
    public void thereWillBeMeDisplayedAsAAuthor() {
        List<WebElement> userButtons = seleniumExample.config.getDriver().findElements(
            By.id("contributor_lra63"));
        WebElement userButton = null;
        for (WebElement element : userButtons) {
            if (element.getText().equals("lra63")) {
                userButton = element;
            }
        }
        Assertions.assertEquals("lra63", userButton.getText());
        // check that this tag is formatted to be the author
        Assertions.assertTrue(userButton.getAttribute("class").contains("author_tag"));
    }

    @And("I type {string} into the user entry")
    public void iTypeIntoTheUserEntry(String arg0) {
        WebElement userInput = seleniumExample.config.getDriver().findElement(By.id("add_user_input"));
        userInput.sendKeys(arg0);
    }

    @Then("There will be {string} suggested")
    public void thereWillBeSuggested(String arg0) {
        List<WebElement> elementsList = seleniumExample.config.getDriver().findElements(By.xpath("//*[contains(text(),'" + arg0 + "')]"));
        boolean check = false;
        for (WebElement element : elementsList) {
            if (element.getText().equals(arg0)) {
                check = true;
            }
        }
        Assertions.assertTrue(check);
    }

    boolean evidenceAdded;

    @And("There is evidence in the table that contains the admin too")
    public void thereIsEvidenceInTheTableThatContainsTheAdminToo()
        throws InterruptedException {
        seleniumExample.config.getDriver()
            .get(seleniumExample.url + "/evidence?pi=1");
        evidenceAdded = seleniumExample.config.getDriver().findElements(By.xpath("//*[contains(text(), 'users')]")).size() > 0;
        if (!evidenceAdded) {
            // open create evidence form
            WebElement button = seleniumExample.config.getDriver()
                .findElement(By.id("add_button"));
            button.click();

            // add title
            WebElement titleField = seleniumExample.config.getDriver()
                .findElement(By.id("evidence_title"));
            titleField.sendKeys("users");

            // add description
            WebElement description = seleniumExample.config.getDriver()
                .findElement(By.id("evidence_desc"));
            description.sendKeys(
                "This evidence relates to the work done on the evidence page");

            // add admin to contributors
            WebElement userInput = seleniumExample.config.getDriver().findElement(By.id("add_user_input"));
            userInput.sendKeys("admin");
            WebElement userButton = seleniumExample.config.getDriver().findElement(By.id("add_user_button"));
            userButton.click();
            Thread.sleep(100);

            WebElement saveButton = seleniumExample.config.getDriver()
                .findElement(By.id("projectSave"));
            Assertions.assertTrue(saveButton.isEnabled());
            JavascriptExecutor je = (JavascriptExecutor) seleniumExample.config.getDriver();
            je.executeScript("arguments[0].scrollIntoView(true);", saveButton);
            Thread.sleep(300);
            saveButton.click();
        }
        Thread.sleep(200);
    }

    @And("There will be admin displayed as a contributor")
    public void thereWillBeAdminDisplayedAsAContributor() {
        List<WebElement> userButtons = seleniumExample.config.getDriver().findElements(
            By.id("contributor_admin"));
        WebElement userButton = null;
        for (WebElement element : userButtons) {
            if (element.getText().equals("admin")) {
                userButton = element;
            }
        }
        Assertions.assertEquals("admin", userButton.getText());
        // check that this tag is formatted to be the author
        Assertions.assertTrue(userButton.getAttribute("class").contains("contributor_tag"));
    }

    @And("I click on the author")
    public void iClickOnTheAuthor() {
        WebElement userButton = seleniumExample.config.getDriver().findElement(By.id("contributor_lra63"));
        userButton.click();
    }

    @Then("I am taken to author's page")
    public void iAmTakenToAuthorSPage() {
        Assertions.assertEquals("Account", seleniumExample.config.getDriver().getTitle());
        WebElement username = seleniumExample.config.getDriver().findElement(By.id("username"));
        Assertions.assertEquals("lra63", username.getText());
    }
}
