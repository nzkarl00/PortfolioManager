package nz.ac.canterbury.seng302.identityprovider.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;

import java.util.List;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class containing the step definitions for the jwt-token-claims Cucumber feature
 */
public class JWTStepDefinitions {

    private AccountProfile profile;

    @Given("default user exists")
    public void givenDefaultUser() {
        this.profile = new AccountProfile(
            "user",
            "irrelevant-hash",
            new Date(),
            "irrelevant bio",
            "email@email.com",
            "photopath",
            "Foo",
            "Bar",
            "He/Him"
        );
//        this.profile.roles = new List<Role>();
    }
}
