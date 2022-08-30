package nz.ac.canterbury.seng302.portfolio.cucumber;

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
        Assertions.assertTrue(userButton.getAttribute("class").contains("contributor_tag"));
    }
}
