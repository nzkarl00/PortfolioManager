package gradle.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import static nz.ac.canterbury.seng302.portfolio.model.Validate.validateText;

class ValidateFeature {
    String text = "";

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

    @Then("I get an error")
    public void i_get_an_error() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            validateText(text);
        });
    }
}
