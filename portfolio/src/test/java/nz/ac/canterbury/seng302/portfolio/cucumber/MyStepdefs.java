package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Scanner;

public class MyStepdefs {
    @Given("User is logged in.")
    public void userIsLoggedIn() {
    }

    @When("User navigates to user table.")
    public void userNavigatesToUserTable() {
    }

    @Then("User is shown a table containing all users.")
    public void userIsShownATableContainingAllUsers() {
    }

    @Given("User is not logged in.")
    public void userIsNotLoggedIn() {
    }

    @Then("User is shown an error page.")
    public void userIsShownAnErrorPage() {
    }

    @Then("User is shown a table containing all users details in scenario.")
    public void userIsShownATableContainingAllUsersDetailsInScenario() {
    }

    @Then("The table is sorted by name alphabetically from A - Z.")
    public void theTableIsSortedByNameAlphabeticallyFromAZ() {
    }


    private SeleniumExample seleniumExample;

    String passwordText = "";


    @Given("There is evidence in the table")
    public void thereIsEvidenceInTheTable() throws InterruptedException {
        // When evidence adding exists, add here, until then it must be added manually
    }

    @And("I am authenticated as a admin")
    public void iAmAuthenticatedAsAAdmin() throws FileNotFoundException {
        seleniumExample = new SeleniumExample("");

        getPassword_ForAdmin_FromTextFile();
        whenPortfolioIsLoaded_thenLoginAdmin_forTests();
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


    @When("I go to the evidence page")
    public void iGoToTheEvidencePage() {
        seleniumExample.config.getDriver().get(seleniumExample.url + "/evidence");
    }

    @Then("There will be the data for the evidence I created")
    public void thereWillBeTheDataForTheEvidenceICreated() throws InterruptedException {
        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1500);
        // get the title of the evidence and the button to open the dropdown
        WebElement title = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div/div[5]/div[2]/div[1]/div[1]/p"));
        WebElement button = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div/div[5]/div[2]/div[1]/div[3]/a"));
        button.click();
        // wait for dropdown
        Thread.sleep(500);
        // get the description, title, and date, then validate said data
        WebElement description = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div/div[5]/div[2]/div[2]/div/div/p"));
        WebElement date = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div/div[5]/div[2]/div[1]/div[2]/p"));
        Assertions.assertEquals("Test Evidence", title.getText());
        Assertions.assertEquals("This is a Description. It is going to be reasonably long but not too long in order to show how text will be potentially cut off.", description.getText());
        Assertions.assertEquals("2022-07-29", date.getText());
    }


    @Given("I go to the evidence page with a project id")
    public void i_go_to_the_evidence_page_with_a_project_id() {
        seleniumExample.config.getDriver().get(seleniumExample.url + "/evidence?pi=1");
    }

    @Given("I click the Add Evidence button")
    public void i_click_the_add_evidence_button() {
        WebElement button = seleniumExample.config.getDriver().findElement(By.id("add_button"));
        button.click();
    }

    @When("I have filled out all mandatory title, description, and date fields to an evidence")
    public void i_have_filled_out_all_mandatory_title_description_and_date_fields_to_an_evidence(){
        WebElement titleField = seleniumExample.config.getDriver().findElement(By.id("evidence_title"));
        titleField.sendKeys("Evidence One");
        WebElement description = seleniumExample.config.getDriver().findElement(By.id("evidence_desc"));
        description.sendKeys("This evidence relates to the work done on the evidence page");

    }


    @When("I click the save button")
    public void i_click_the_save_button() {
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
        Assertions.assertTrue(saveButton.isEnabled());
        saveButton.submit();


    }

    @Then("I will see a message that this evidence has saved successfully")
    public void i_will_see_a_message_that_this_evidence_has_saved_successfully() throws InterruptedException {
        Thread.sleep(1000);
        WebElement message = seleniumExample.config.getDriver().findElement(By.id("display_box"));
        Assertions.assertEquals("Evidence has been added", message.getText());
    }

    @When("The all mandatory fields to an evidence are empty, I cannot click the save button")
    public void the_all_mandatory_fields_to_an_evidence_are_empty_i_cannot_click_the_save_button() {
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
        Assertions.assertFalse(saveButton.isEnabled());
    }

    @When("I click the cancel button")
    public void i_click_the_cancel_button() {
        WebElement cancelButton = seleniumExample.config.getDriver().findElement(By.id("cancelButton"));
        cancelButton.sendKeys(Keys.ENTER);

    }

    @Then("I can see the evidence creation page extract and replace by a plus button")
    public void i_can_see_the_evidence_creation_page_extract_and_replace_by_a_plus_button() {
        WebElement addButton = seleniumExample.config.getDriver().findElement(By.id("add_button"));
        Assertions.assertTrue(addButton.isEnabled());
    }


}
