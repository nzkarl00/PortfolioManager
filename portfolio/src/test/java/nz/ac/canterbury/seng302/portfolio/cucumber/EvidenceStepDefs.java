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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EvidenceStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;

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

    // ensures a piece of evidence with the desired traits
    boolean evidenceAdded = false;

    @Given("There is evidence in the table")
    public void thereIsEvidenceInTheTable() throws InterruptedException {
        seleniumExample.config.getDriver()
                .get(seleniumExample.url + "/evidence?pi=1");
        evidenceAdded = seleniumExample.config.getDriver().findElements(By.xpath("//*[contains(text(), 'Evidence One')]")).size() > 0;
        if (!evidenceAdded) {
            // open create evidence form
            WebElement button = seleniumExample.config.getDriver()
                .findElement(By.id("add_button"));
            button.click();

            // add title
            WebElement titleField = seleniumExample.config.getDriver()
                .findElement(By.id("evidence_title"));
            titleField.sendKeys("Evidence One");

            // add description
            WebElement description = seleniumExample.config.getDriver()
                .findElement(By.id("evidence_desc"));
            description.sendKeys(
                "This evidence relates to the work done on the evidence page");

            // add skill
            WebElement skillInput = seleniumExample.config.getDriver()
                .findElement(By.id("add_skill_input"));
            skillInput.sendKeys("skill");
            WebElement skillButton = seleniumExample.config.getDriver()
                .findElement(By.id("add_skill_button"));
            skillButton.click();

            // add secure weblink
            WebElement linkInput = seleniumExample.config.getDriver()
                .findElement(By.id("add_link_input"));
            linkInput.sendKeys("https://en.wikipedia.org/wiki/Main_Page");
            WebElement linkButton = seleniumExample.config.getDriver()
                .findElement(By.id("add_link_button"));
            linkButton.click();
            Thread.sleep(100);

            // add insecure weblink
            linkInput.sendKeys("http://info.cern.ch/");
            linkButton.click();
            System.out.println("links added");

            WebElement saveButton = seleniumExample.config.getDriver()
                .findElement(By.id("projectSave"));
            Assertions.assertTrue(saveButton.isEnabled());
            saveButton.submit();

            evidenceAdded = true;
        }
        seleniumExample.config.getDriver()
                .get(seleniumExample.url + "/evidence?pi=1");
    }

    @When("I go to the evidence page")
    public void iGoToTheEvidencePage() {
        seleniumExample.config.getDriver().get(seleniumExample.url + "/evidence");
    }

    @Then("There will be the data for the evidence I created")
    public void thereWillBeTheDataForTheEvidenceICreated() {
        // get the xpath of the desired pieve of evidence
        WebElement title = seleniumExample.config.getDriver().findElement(By.xpath("//*[contains(text(), 'Evidence One')]"));
        Assertions.assertEquals("Evidence One", title.getText());
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
    public void i_click_the_save_button() throws InterruptedException {
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
        Assertions.assertTrue(saveButton.isEnabled());
        saveButton.submit();
        Thread.sleep(200);
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

    @When("User selects the Quantitative skills option in the category dropdown")
    public void userSelectsTheOptionInTheCategoryDropdown()
        throws InterruptedException {
        Select category = new Select(seleniumExample.config.getDriver().findElement(By.id("ci")));
        category.selectByValue("Quantitative Skills");
        Thread.sleep(100);
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
        category.selectByValue(arg0);
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
    public void hovering_my_mouse_over_the_question_mark_icon_beside_the_date_picker_will_give_me_information_about_it()
        throws InterruptedException {
        WebElement icon = seleniumExample.config.getDriver().findElement(By.id("evidence_date_tool"));
        WebDriver driver = seleniumExample.config.getDriver();
        Actions actions = new Actions(driver);
        actions.moveToElement(icon).perform();
        Thread.sleep(100);
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

    @When("User selects the {string} option in the skills side menu")
    public void userSelectsTheOptionInTheSkillsSideMenu(String arg0) {
        WebElement button = seleniumExample.config.getDriver().findElement(By.id("skill_button_"+arg0));
        button.click();
    }

    @When("User enters {string} into the title")
    public void userEntersIntoTheTitle(String arg0) {
        WebElement titleInput = seleniumExample.config.getDriver().findElement(By.id("evidence_title"));
        titleInput.clear();
        titleInput.sendKeys(arg0);
    }

    @And("User enters {string} into the description")
    public void userEntersIntoTheDescription(String arg0) {
        WebElement descInput = seleniumExample.config.getDriver().findElement(By.id("evidence_desc"));
        descInput.clear();
        descInput.sendKeys(arg0);
    }

    @Then("Save button can be clicked")
    public void saveButtonCanBeClicked() {
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
        Assertions.assertNull(saveButton.getAttribute("disabled"));
    }

    @Then("Save button cannot be clicked")
    public void saveButtonCanNotBeClicked() {
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
        Assertions.assertEquals("true", saveButton.getAttribute("disabled"));
    }
}
