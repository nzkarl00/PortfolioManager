package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;

public class ModifyEvidenceStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;

    @And("I click the edit button")
    public void iClickTheEditButton() {
    }

    @And("I click the {string} tag")
    public void iClickTheTag(String arg0) {
    }

    @And("I change the skill to {string}")
    public void iChangeTheSkillTo(String arg0) {
    }

    @Then("The edit list is updated")
    public void theEditListIsUpdated() {
    }

    @And("I click the delete {string} button")
    public void iClickTheDeleteButton(String arg0) {
    }

    @Then("The delete list is updated")
    public void theDeleteListIsUpdated() {
    }

    @Then("The new list is updated")
    public void theNewListIsUpdated() {
    }
}
