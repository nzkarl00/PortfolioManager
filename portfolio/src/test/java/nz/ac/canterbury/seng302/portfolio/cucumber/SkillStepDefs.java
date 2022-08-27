package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SkillStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;

    String skillName;

    @When("User inputs {string} into the skill input textbox.")
    public void userInputsIntoTheSkillInputTextbox(String arg0) {
        skillName = arg0;
        WebElement
            skillInput = seleniumExample.config.getDriver().findElement(By.id("add_skill_input"));
        skillInput.sendKeys(arg0);
        WebElement skillButton = seleniumExample.config.getDriver().findElement(By.id("add_skill_button"));
        skillButton.click();
    }

    @Then("There will be a skill displayed.")
    public void thereWillBeASkillDisplayed() {
        WebElement skill = seleniumExample.config.getDriver().findElement(By.id("skill_" + skillName));
        Assertions.assertEquals(skillName + " ✖", skill.getText());
    }

    @Then("There will not be a skill displayed.")
    public void thereWillNotBeASkillDisplayed() {
        List<WebElement> skills = seleniumExample.config.getDriver().findElements(By.id("skill_" + skillName));
        Assertions.assertTrue(skills.isEmpty());
    }

    @And("An appropriate error message will be shown.")
    public void anAppropriateErrorMessageWillBeShown() {
        WebElement error = seleniumExample.config.getDriver().findElement(By.id("skill_error"));
        Assertions.assertEquals("Only letters, underscores, hyphens, and numbers are allowed", error.getText());
    }
}
