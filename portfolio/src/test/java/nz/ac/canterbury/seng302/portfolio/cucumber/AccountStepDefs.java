package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class AccountStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;

    @Then("There are edit buttons for me.")
    public void thereAreEditButtonsForMe() {
        List<WebElement> editButtons = seleniumExample.config.getDriver().findElements(By.id("edit_account_button"));
        Assertions.assertTrue(editButtons.get(0).isDisplayed());
    }

    @Then("There are not edit buttons for me.")
    public void thereAreNotEditButtonsForMe() {
        List<WebElement> editButtons = seleniumExample.config.getDriver().findElements(By.id("edit_account_button"));
        Assertions.assertTrue(editButtons.isEmpty());
    }
}
