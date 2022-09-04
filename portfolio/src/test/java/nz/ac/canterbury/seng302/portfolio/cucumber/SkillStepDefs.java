package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.*;

public class SkillStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;

    String skillName;

    @When("User inputs {string} into the skill input textbox.")
    public void userInputsIntoTheSkillInputTextbox(String inputSkill) {
        skillName = inputSkill;
        WebElement
            skillInput = seleniumExample.config.getDriver().findElement(By.id("add_skill_input"));
        skillInput.sendKeys(inputSkill);
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

    @When("User selects the {string} option in the skills side menu")
    public void userSelectsTheOptionInTheSkillsSideMenu(String inputSkill) {
        WebElement button = seleniumExample.config.getDriver().findElement(By.id("skill_button_"+inputSkill));
        button.click();
    }

    @And("An appropriate error message will be shown.")
    public void anAppropriateErrorMessageWillBeShown() {
        boolean check = false;
        // there are many error message locations, make sure one of them is open and showing the right text
        List<WebElement> errors = seleniumExample.config.getDriver().findElements(By.id("skill_error"));
        for (WebElement element : errors) {
            if (element.getText().equals("Only letters, underscores, hyphens, and numbers are allowed. No more than 50 characters.")) {
                check = true;
            }
        }
        Assertions.assertTrue(check);
    }

    @Then("The skill {string} will be displayed.")
    public void theSkillWillBeDisplayed(String inputSkill) {
        WebElement skill = seleniumExample.config.getDriver().findElement(By.id("skill_" + inputSkill));
        Assertions.assertEquals(inputSkill + " ✖", skill.getText());
    }

    @Then("There are no duplicates in the skill menu")
    public void thereAreNoDuplicatesInTheSkillMenu() {
        List<WebElement> skillMenu = seleniumExample.config.getDriver().findElements(By.id("skills_menu"));
        List<String> skillList = new ArrayList<String>();
        Set<String> skillSet = new HashSet<>();
        for (WebElement eachSkill: skillMenu) {
            skillList.add(eachSkill.getText());
            skillSet.add(eachSkill.getText());
        }
        for (String eachSkillString: skillList) {
            Assertions.assertTrue(skillSet.contains(eachSkillString));
        }
        Assertions.assertEquals(skillList.size(), skillSet.size());
    }
}
