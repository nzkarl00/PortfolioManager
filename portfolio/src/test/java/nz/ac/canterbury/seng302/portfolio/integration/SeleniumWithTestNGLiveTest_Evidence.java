package nz.ac.canterbury.seng302.portfolio.integration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumLogins;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import static nz.ac.canterbury.seng302.portfolio.integration.SeleniumLogins.getPassword_ForAdmin_FromTextFile;
import static nz.ac.canterbury.seng302.portfolio.integration.SeleniumLogins.whenPortfolioIsLoaded_thenLoginAdmin_forTests;

public class SeleniumWithTestNGLiveTest_Evidence {

    private SeleniumExample seleniumExample;

    String passwordText = "";
    String evidenceUrl = "/evidence";
    WebElement skillsInput;
    WebElement addSkillButton;

    @BeforeSuite
    public void setUp() throws FileNotFoundException, InterruptedException {
        seleniumExample = new SeleniumExample("");
        passwordText = getPassword_ForAdmin_FromTextFile();
        whenPortfolioIsLoaded_thenLoginAdmin_forTests(seleniumExample, passwordText);
        whenLoggedInAsAdmin_AddSkill_ForTest();
        whenLoggedInAsAdmin_AddTwoSkills_ForTest();
        whenLoggedInAsAdmin_AddTwoSkills_ThenRemoveFirstSkill_ForTest();
    }

    @AfterSuite
    public void tearDown() {
        seleniumExample.closeWindow();
    }

    /**
     * Inputs a new skill, checks a tag has been added
     */
    public void whenLoggedInAsAdmin_AddSkill_ForTest() {
        seleniumExample.config.getDriver().get(seleniumExample.url + evidenceUrl);
        skillsInput = seleniumExample.config.getDriver().findElement(By.id("add_skill_input"));
        addSkillButton = seleniumExample.config.getDriver().findElement(By.id("add_skill_button"));
        skillsInput.sendKeys("new skill");
        addSkillButton.click();
        List<WebElement> skillTags = seleniumExample.config.getDriver().findElements(By.className("skill_tag"));
        Assertions.assertEquals(1, skillTags.size());
        Assertions.assertEquals("new skill ✖", skillTags.get(0).getText());
    }

    /**
     * Inputs a second skill, checks there are now two skill tags
     */
    public void whenLoggedInAsAdmin_AddTwoSkills_ForTest() {
        seleniumExample.config.getDriver().get(seleniumExample.url + evidenceUrl);
        skillsInput = seleniumExample.config.getDriver().findElement(By.id("add_skill_input"));
        addSkillButton = seleniumExample.config.getDriver().findElement(By.id("add_skill_button"));
        skillsInput.sendKeys("new skill");
        addSkillButton.click();
        skillsInput.sendKeys("new skill 2");
        addSkillButton.click();
        List<WebElement> skillTags = seleniumExample.config.getDriver().findElements(By.className("skill_tag"));
        Assertions.assertEquals(2, skillTags.size());
        Assertions.assertEquals("new skill 2 ✖", skillTags.get(1).getText());
    }

    /**
     * Removes the first skill, checks it has been removed and the second tag is unchanged
     */
    public void whenLoggedInAsAdmin_AddTwoSkills_ThenRemoveFirstSkill_ForTest() {
        seleniumExample.config.getDriver().get(seleniumExample.url + evidenceUrl);
        skillsInput = seleniumExample.config.getDriver().findElement(By.id("add_skill_input"));
        addSkillButton = seleniumExample.config.getDriver().findElement(By.id("add_skill_button"));
        skillsInput.sendKeys("new skill");
        addSkillButton.click();
        skillsInput.sendKeys("new skill 2");
        addSkillButton.click();
        List<WebElement> skillTags = seleniumExample.config.getDriver().findElements(By.className("skill_tag"));
        skillTags.get(0).click();
        skillTags = seleniumExample.config.getDriver().findElements(By.className("skill_tag"));
        Assertions.assertEquals(1, skillTags.size());
        Assertions.assertEquals("new skill 2 ✖", skillTags.get(0).getText());
    }
}
