package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumLogins;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BaseSeleniumStepDefs {

    public static SeleniumExample seleniumExample;

    String passwordText = "";

    private boolean registered = false;

    @Given("User is logged in.")
    public void userIsLoggedIn() {
        seleniumExample = new SeleniumExample("");
        if (!registered) {
            SeleniumLogins.whenPortfolioIsLoaded_thenRegisterWorks(seleniumExample);
            registered = true;
        }
        SeleniumLogins.whenPortfolioIsLoaded_thenLoginWorks(seleniumExample);
    }

    @And("I am authenticated as a admin")
    public void iAmAuthenticatedAsAAdmin() throws FileNotFoundException {
        seleniumExample = new SeleniumExample("");
        SeleniumLogins.getPassword_ForAdmin_FromTextFile(passwordText);
        SeleniumLogins.whenPortfolioIsLoaded_thenLoginAdmin_forTests(seleniumExample, passwordText);
    }

    @And("The window is closed.")
    public void theWindowIsClosed() {
        seleniumExample.closeWindow();
    }

    @When("User navigates to {string}.")
    public void userNavigatesTo(String arg0) {
        seleniumExample.config.getDriver().get(seleniumExample.url + "/" + arg0);
    }
}
