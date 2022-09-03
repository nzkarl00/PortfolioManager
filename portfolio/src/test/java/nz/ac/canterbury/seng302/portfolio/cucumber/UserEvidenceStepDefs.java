package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class UserEvidenceStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;

    @Then("There will be me displayed as a author")
    public void thereWillBeMeDisplayedAsAAuthor() {
        WebElement userButton = seleniumExample.config.getDriver().findElement(
            By.id("contributor_lra63"));
        Assertions.assertEquals("lra63", userButton.getText());
        // check that this tag is formatted to be the author
        Assertions.assertTrue(userButton.getAttribute("class").contains("evidence_ci_tag"));
    }

    @And("I click on the author")
    public void iClickOnTheAuthor() {
        WebElement userButton = seleniumExample.config.getDriver().findElement(By.id("contributor_lra63"));
        userButton.click();
    }

    @Then("I am taken to author's page")
    public void iAmTakenToAuthorSPage() {
        WebElement title = seleniumExample.config.getDriver().findElement(By.id("title"));
        Assertions.assertEquals("Evidence from user: lra63", title.getText());
    }
}
