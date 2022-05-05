package nz.ac.canterbury.seng302.identityprovider.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class containing the step definitions for the jwt-token-claims Cucumber feature
 */
public class StepDefinitions {

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
        this.profile.roles = new List<Role>();
    }

    @Given("account name is {string}")
    public void givenAccountName(String initialName) {
        this.account = new Account(initialName, 0.0);
    }

    @Given("account withdrawl count limit is {int}")
    public void givenAccountWithdrawlCountLimit(Integer limit) {
//        account = new Account("Default", 0.0);
        account.setWithdrawlCountLimit(limit);
    }

    @When("the account is credited with {double}")
    public void whenAccountIsCredited(Double amount) {
        account.credit(amount);
    }

    @When("the account name is changed to {string}")
    public void whenAccountNameIsChanged(String name) {
        account.setName(name);
    }

    @When("the account is debited with {double}")
    public void whenAccountIsDebited(Double amount) {
        try {
            account.debit(amount);
        } catch (Exception e) {}
    }

    @When("account withdrawl count limit is set to {int}")
    public void whenAccountWithdrawalCountLimitIsSet(Integer limit) {
        account.setWithdrawlCountLimit(limit);
    }

    @When("withdraw {double} {int} times")
    public void whenWithdrawAmountNTimes(Double amount, Integer times) {
        try {
            int i = 0;
            for (i = 0; i < times; i = i + 1) {
                account.debit(amount);
            }
        } catch (Exception e) {

        }
    }

    @Then("account should have a balance of {double}")
    public void thenAccountShouldHaveBalance(Double expectedBalance) {
        assertEquals(expectedBalance, account.getBalance());
    }

    @Then("account name should be {string}")
    public void thenAccountShouldHaveName(String expectedName) {
        assertEquals(expectedName, account.getName());
    }

    @Then("Then account withdrawl count limit is 5")
    public void thenAccountWithdrawlCountLimitIs(Integer expectedLimit) {
        assertEquals(expectedLimit, account.getWithdrawlCountLimit());
    }
}