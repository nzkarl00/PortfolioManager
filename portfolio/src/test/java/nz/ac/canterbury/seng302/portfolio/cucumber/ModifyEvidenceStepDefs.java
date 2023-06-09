package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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


    @Given("There is an existing evidence that I own")
    public void there_is_an_existing_evidence_that_i_own() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @And("I click the edit button")
    public void iClickTheEditButton() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @When("I navigate to that evidence")
    public void i_navigate_to_that_evidence() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @And("I click the {string} tag")
    public void iClickTheTag(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @Then("I can click on the edit icon")
    public void i_can_click_on_the_edit_icon() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @And("I change the skill to {string}")
    public void iChangeTheSkillTo(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see a pencil icon for the edit icon")
    public void i_can_see_a_pencil_icon_for_the_edit_icon() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("User inputs {string} into the edit skill input textbox.")
    public void iAddTheSkillTo(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @Then("I can see the edit icon is next to the delete icon")
    public void i_can_see_the_edit_icon_is_next_to_the_delete_icon() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The edit list is updated")
    public void theEditListIsUpdated() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("There is an existing evidence that I don't own")
    public void there_is_an_existing_evidence_that_i_don_t_own() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @And("I click the delete {string} button")
    public void iClickTheDeleteButton(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @Then("I will not find an edit icon at all")
    public void i_will_not_find_an_edit_icon_at_all() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @Then("The delete list is updated")
    public void theDeleteListIsUpdated() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I click on the edit icon")
    public void i_click_on_the_edit_icon() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can obviously see I am in the edit mode")
    public void i_can_obviously_see_i_am_in_the_edit_mode() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see the save button is visible, but disabled")
    public void i_can_see_the_save_button_is_visible_but_disabled() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see the cancel button")
    public void i_can_see_the_cancel_button() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I cannot see the edit icon as it is not visible in edit mode")
    public void i_cannot_see_the_edit_icon_as_it_is_not_visible_in_edit_mode() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I am on edit mode for an evidence I own")
    public void i_am_on_edit_mode_for_an_evidence_i_own() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I can see this evidence has the {string} filled in")
    public void i_can_see_this_evidence_has_the_filled_in(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I can see the save button is visible and disabled")
    public void i_can_see_the_save_button_is_visible_and_disabled() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I have make a modification to the evidence {string}")
    public void i_have_make_a_modification_to_the_evidence(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see the save button enabled for me to click on")
    public void i_can_see_the_save_button_enabled_for_me_to_click_on() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I can see this evidence has an {string} skill tag")
    public void i_can_see_this_evidence_has_an_skill_tag(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I click the skill tag")
    public void i_click_the_skill_tag() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I save this piece of evidence")
    public void i_save_this_piece_of_evidence() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see the skill tag is updated globally")
    public void i_can_see_the_skill_tag_is_updated_globally() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see the skill tag is updated on all my piece of evidence")
    public void i_can_see_the_skill_tag_is_updated_on_all_my_piece_of_evidence() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see the skill tag is updated in the headings")
    public void i_can_see_the_skill_tag_is_updated_in_the_headings() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I click the delete button on a skill tag")
    public void i_click_the_delete_button_on_a_skill_tag() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see this skill tag deleted from this piece of evidence")
    public void i_can_see_this_skill_tag_deleted_from_this_piece_of_evidence() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("This skill tag won't be deleted globally")
    public void this_skill_tag_won_t_be_deleted_globally() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("User inputs {string} into the edit skill input textbox")
    public void user_inputs_into_the_edit_skill_input_textbox(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see this {string} skill tag added to this piece of evidence")
    public void i_can_see_this_skill_tag_added_to_this_piece_of_evidence(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I can see this evidence has a {string}")
    public void i_can_see_this_evidence_has_a(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I delete the existing {string}")
    public void i_delete_the_existing(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I can see this evidence does not have a {string} added already")
    public void i_can_see_this_evidence_does_not_have_a_added_already(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("User add a {string} to this evidence")
    public void user_add_a_to_this_evidence(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see this {string} added to this piece of evidence")
    public void i_can_see_this_added_to_this_piece_of_evidence(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I remove data from the {string}")
    public void i_remove_data_from_the(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I will see the save button is disabled")
    public void i_will_see_the_save_button_is_disabled() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I will see an error message to explain why I cannot save")
    public void i_will_see_an_error_message_to_explain_why_i_cannot_save() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I modify the {string} with {string}")
    public void i_modify_the_with(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @When("Usual validation fails on the modified {string}")
    public void usual_validation_fails_on_the_modified(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("Usual validation pass on the modified {string}")
    public void usual_validation_pass_on_the_modified(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I will see the save button is enabled")
    public void i_will_see_the_save_button_is_enabled() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The new list is updated")
    public void theNewListIsUpdated() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I edit the piece of evidence")
    public void i_edit_the_piece_of_evidence() throws InterruptedException {
        WebElement evidenceId = seleniumExample.config.getDriver().findElement(By.cssSelector(".bio.evidence_head"));
        seleniumExample.config.getDriver().get(seleniumExample.url + "/edit-evidence?id=" + evidenceId.getAttribute("id"));
        Thread.sleep(100);
    }


    @When("I remove the title field")
    public void i_remove_the_title_field() {
        WebElement title = seleniumExample.config.getDriver().findElement(By.id("evidence_title"));
        title.clear();
    }

    @When("I remove the description field")
    public void i_remove_the_description_field() {
        WebElement description = seleniumExample.config.getDriver().findElement(By.id("evidence_desc"));
        description.clear();
    }

    @When("User enters {string} into the {string}")
    public void user_enters_into_the(String stringInput, String attributeId) throws InterruptedException {
        WebElement attribute = seleniumExample.config.getDriver().findElement(By.id(attributeId));
        attribute.sendKeys(stringInput);
    }

    @Then("The save button is enabled")
    public void the_save_button_is_enabled() {
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
        Assertions.assertTrue(saveButton.isEnabled());
    }

}

