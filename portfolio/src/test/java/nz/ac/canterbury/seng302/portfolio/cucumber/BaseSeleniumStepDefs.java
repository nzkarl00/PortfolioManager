package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
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
            whenPortfolioIsLoaded_thenRegisterWorks();
            registered = true;
        }
        whenPortfolioIsLoaded_thenLoginWorks();
    }

    /**
     * Gets the password for the pre-generated admin account from the application files
     * @throws FileNotFoundException
     */
    public void getPassword_ForAdmin_FromTextFile() throws FileNotFoundException {
        String originpath = System.getProperty("user.dir");
        File passwordFile = new File(originpath.substring(0, originpath.length()-9) + "identityprovider/defaultAdminPassword.txt");
        Scanner passwordReader = new Scanner(passwordFile);
        passwordText = passwordReader.nextLine();
    }

    /**
     * load up the page then login to the admin user
     */
    public void whenPortfolioIsLoaded_thenLoginAdmin_forTests() {
        seleniumExample.config.getDriver().get(seleniumExample.url);
        WebElement username = seleniumExample.config.getDriver().findElement(By.id("username"));
        username.sendKeys("admin");
        WebElement password = seleniumExample.config.getDriver().findElement(By.id("password"));
        password.sendKeys(passwordText);
        WebElement loginButton = seleniumExample.config.getDriver().findElement(By.id("login-button"));
        loginButton.click();

        WebElement fullName = seleniumExample.config.getDriver().findElement(By.id("full-name"));
        Assertions.assertEquals("admin admin", fullName.getText());
        seleniumExample.config.getDriver().get(seleniumExample.url + "/landing");
    }

    @And("I am authenticated as a admin")
    public void iAmAuthenticatedAsAAdmin() throws FileNotFoundException {
        seleniumExample = new SeleniumExample("");
        getPassword_ForAdmin_FromTextFile();
        whenPortfolioIsLoaded_thenLoginAdmin_forTests();
    }

    /**
     * load up the page to register a new uesr lra63
     */
    public void whenPortfolioIsLoaded_thenRegisterWorks() {
        seleniumExample.config.getDriver().get(seleniumExample.url);
        WebElement
            signupButton = seleniumExample.config.getDriver().findElement(By.id("signup-button"));
        signupButton.click();
        WebElement username = seleniumExample.config.getDriver().findElement(By.id("username"));
        username.sendKeys("lra63");
        WebElement password = seleniumExample.config.getDriver().findElement(By.id("password"));
        password.sendKeys("1234567890");
        WebElement passwordConfirm = seleniumExample.config.getDriver().findElement(By.id("passwordConfirm"));
        passwordConfirm.sendKeys("1234567890");
        WebElement firstName = seleniumExample.config.getDriver().findElement(By.id("firstname"));
        firstName.sendKeys("Lachlan");
        WebElement lastName = seleniumExample.config.getDriver().findElement(By.id("lastname"));
        lastName.sendKeys("Alsop");
        WebElement email = seleniumExample.config.getDriver().findElement(By.id("email"));
        email.sendKeys("lra63@uclive.ac.nz");
        WebElement submitButton = seleniumExample.config.getDriver().findElement(By.id("signup-button"));
        submitButton.click();
    }

    /**
     * load up the page then login to the user lra63, with the set password, note if this is not on your machine you will get errors
     */
    public void whenPortfolioIsLoaded_thenLoginWorks() {
        seleniumExample.config.getDriver().get(seleniumExample.url);
        WebElement username = seleniumExample.config.getDriver().findElement(By.id("username"));
        username.sendKeys("lra63");
        WebElement password = seleniumExample.config.getDriver().findElement(By.id("password"));
        password.sendKeys("1234567890");
        WebElement loginButton = seleniumExample.config.getDriver().findElement(By.id("login-button"));
        loginButton.click();
        WebElement fullName = seleniumExample.config.getDriver().findElement(By.id("full-name"));
        Assertions.assertEquals("Lachlan Alsop", fullName.getText());
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
