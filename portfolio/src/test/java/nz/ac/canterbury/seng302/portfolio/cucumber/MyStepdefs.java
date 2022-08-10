package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MyStepdefs {

    private SeleniumExample seleniumExample;

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

    String passwordText = "";


    @Given("There is evidence in the table")
    public void thereIsEvidenceInTheTable() throws InterruptedException {
        seleniumExample.config.getDriver().get(seleniumExample.url + "/evidence?pi=1");
        WebElement button = seleniumExample.config.getDriver().findElement(By.id("add_button"));
        button.click();
        WebElement titleField = seleniumExample.config.getDriver().findElement(By.id("evidence_title"));
        titleField.sendKeys("Test Evidence");
        WebElement description = seleniumExample.config.getDriver().findElement(By.id("evidence_desc"));
        description.sendKeys("This is a Description. It is going to be reasonably long but not too long in order to show how text will be potentially cut off.");
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
        Assertions.assertTrue(saveButton.isEnabled());
        saveButton.submit();
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
     * load up the page to register a new uesr lra63
     */
    public void whenPortfolioIsLoaded_thenRegisterWorks() {
        seleniumExample.config.getDriver().get(seleniumExample.url);
        WebElement signupButton = seleniumExample.config.getDriver().findElement(By.id("signup-button"));
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
        Assertions.assertEquals("Test Evidence", title.getText());
        Assertions.assertEquals("This is a Description. It is going to be reasonably long but not too long in order to show how text will be potentially cut off.", description.getText());

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

    String skillName;

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

    @When("User navigates to {string}.")
    public void userNavigatesTo(String arg0) {
        seleniumExample.config.getDriver().get(seleniumExample.url + "/" + arg0);
    }

    @When("User inputs {string} into the skill input textbox.")
    public void userInputsIntoTheSkillInputTextbox(String arg0) {
        skillName = arg0;
        WebElement skillInput = seleniumExample.config.getDriver().findElement(By.id("add_skill_input"));
        skillInput.sendKeys(arg0);
        WebElement skillButton = seleniumExample.config.getDriver().findElement(By.id("add_skill_button"));
        skillButton.click();
    }

    @Then("There will be a skill displayed.")
    public void thereWillBeASkillDisplayed() {
        WebElement skill = seleniumExample.config.getDriver().findElement(By.id("skill_" + skillName));
        Assertions.assertEquals(skillName + " ✖", skill.getText());
    }

    @Then("There will not be a skill displayed.")
    public void thereWillNotBeASkillDisplayed() {
        List<WebElement> skills = seleniumExample.config.getDriver().findElements(By.id("skill_" + skillName));
        System.out.println(skills);
        Assertions.assertTrue(skills.isEmpty());
    }

    @And("An appropriate error message will be shown.")
    public void anAppropriateErrorMessageWillBeShown() {
        WebElement error = seleniumExample.config.getDriver().findElement(By.id("skill_error"));
        Assertions.assertEquals("Only letters, underscores, hyphens, and numbers are allowed", error.getText());
    }

    @And("The window is closed.")
    public void theWindowIsClosed() {
        seleniumExample.closeWindow();
    }

    @When("User selects the Quantitative skills option in the category dropdown")
    public void userSelectsTheOptionInTheCategoryDropdown() {
        Select category = new Select(seleniumExample.config.getDriver().findElement(By.id("ci")));
        category.selectByValue("0");
    }

    @And("User clicks search button")
    public void userClicksSearchButton() {
        WebElement searchButton = seleniumExample.config.getDriver().findElement(By.id("searchEvidence"));
        searchButton.click();
    }

    @Then("user is directed to a page where the heading is {string}")
    public void userIsDirectedToAPageWhereTheHeadingIs(String arg0) {
        WebElement title = seleniumExample.config.getDriver().findElement(By.id("title"));
        Assertions.assertEquals(arg0, title.getText());
    }

    @When("User selects the {string} option in the skills dropdown")
    public void userSelectsTheOptionInTheSkillsDropdown(String arg0) {
        Select category = new Select(seleniumExample.config.getDriver().findElement(By.id("si")));
        category.selectByVisibleText(arg0);
    }

    @Then("I can see the prefilled date is today's date")
    public void i_can_see_the_prefilled_date_is_today_s_date() {
        WebElement date = seleniumExample.config.getDriver().findElement(By.id("date_input"));
        String currentDateExpected = DateParser.dateToStringHtml(new Date());
        String currentDateActual = date.getAttribute("value");
        Assertions.assertEquals(currentDateExpected, currentDateActual);
    }

    @When("I try to update the widget date to a new date that is within the project")
    public void i_try_to_update_the_widget_date_to_a_new_date_that_is_within_the_project() {
        WebElement date = seleniumExample.config.getDriver().findElement(By.id("date_input"));
        String minDateStringFormat = date.getAttribute("min");
        Date minDate = DateParser.stringToDate(minDateStringFormat);
        Calendar cal = Calendar.getInstance();
        cal.setTime(minDate);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        String month = String.valueOf(cal.get(Calendar.MONTH));;
        if (cal.get(Calendar.MONTH) < 10) {
            month = "0" + String.valueOf(cal.get(Calendar.MONTH));
        }
        String dateToSend = String.format("%d-%s-%d", cal.get(Calendar.YEAR), month, cal.get(Calendar.DATE));
        date.sendKeys(dateToSend);
    }

    @Then("I can see the widget date field is set to the updated new date")
    public void i_can_see_the_widget_date_field_is_set_to_the_updated_new_date() {
        WebElement date = seleniumExample.config.getDriver().findElement(By.id("date_input"));
        String minDateStringFormat = date.getAttribute("min");
        Date minDate = DateParser.stringToDate(minDateStringFormat);
        Calendar cal = Calendar.getInstance();
        cal.setTime(minDate);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        String month = String.valueOf(cal.get(Calendar.MONTH));;
        if (cal.get(Calendar.MONTH) < 10) {
            month = "0" + String.valueOf(cal.get(Calendar.MONTH));
        }
        String expected = String.format("%d-%s-%d", cal.get(Calendar.YEAR), month, cal.get(Calendar.DATE));
        String actual = date.getAttribute("value");
        Assertions.assertEquals(expected, actual);
    }

    @Then("Hovering my mouse over the question mark icon beside the date picker will give me information about it")
    public void hovering_my_mouse_over_the_question_mark_icon_beside_the_date_picker_will_give_me_information_about_it() {
        WebElement icon = seleniumExample.config.getDriver().findElement(By.id("evidence_date_tool"));
        WebDriver driver = seleniumExample.config.getDriver();
        Actions actions = new Actions(driver);
        actions.moveToElement(icon).perform();
        String toolTipActual = seleniumExample.config.getDriver().findElement(By.id("info")).getText();
        String toolTipExpected = "This is the date the evidence occurred, date selection is restricted to the boundaries of the project it is assigned to.";
        Assertions.assertEquals(toolTipExpected, toolTipActual);

    }

    @Then("I can see that the range of the date widget is filled in with the project date range")
    public void i_can_see_that_the_range_of_the_date_widget_is_filled_in_with_the_project_date_range() {
        WebElement date = seleniumExample.config.getDriver().findElement(By.id("date_input"));
        String min = date.getAttribute("min");
        String max = date.getAttribute("max");
        Assertions.assertFalse(min.isBlank());
        Assertions.assertFalse(max.isBlank());
    }
}
