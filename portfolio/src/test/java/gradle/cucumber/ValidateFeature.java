package gradle.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import static nz.ac.canterbury.seng302.portfolio.model.Validate.validateText;

class ValidateFeature {
    String text = "";

    @Given("I am logged in")
    public void i_am_logged_in() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I am on the evidence page")
    public void i_am_on_the_evidence_page() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I am entering into the title text field")
    public void i_am_entering_into_the_title_text_field() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I enter the characters {string}")
    public void i_enter_the_characters(String textToValidate) {
        text = textToValidate;
    }

    @Then("I can post my evidence")
    public void i_can_post_my_evidence() {
        Assertions.assertDoesNotThrow(() -> {
            validateText(text);
        });
    }

    @Then("I get a success message")
    public void i_get_a_success_message() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I am entering into the description text field")
    public void i_am_entering_into_the_description_text_field() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I cannot post my evidence")
    public void i_cannot_post_my_evidence() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            validateText(text);
        });
    }

    @Then("I get an error")
    public void i_get_an_error() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
