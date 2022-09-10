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

import java.time.Duration;
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
        Thread.sleep(500);
        evidenceAdded = seleniumExample.config.getDriver().findElements(By.xpath("//*[contains(text(), 'Evidence One')]")).size() > 0;
        if (!evidenceAdded) {
            // open create evidence form
            Thread.sleep(500);
            WebElement thereIsEvidenceInTheTablebutton = seleniumExample.config.getDriver().findElement(By.id("add_button"));
            thereIsEvidenceInTheTablebutton.click();
            Thread.sleep(500);

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





            WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
            scrollIntoView(saveButton);
            Assertions.assertTrue(saveButton.isEnabled());
            saveButton.click();

            evidenceAdded = true;
        }
        Thread.sleep(100);
    }

    @When("I go to the evidence page")
    public void iGoToTheEvidencePage() throws InterruptedException {
        seleniumExample.config.getDriver().get(seleniumExample.url + "/evidence");

        Thread.sleep(500);
    }

    @Then("There will be the data for the evidence I created")
    public void thereWillBeTheDataForTheEvidenceICreated() {
        // get the xpath of the desired pieve of evidence
        WebElement title = seleniumExample.config.getDriver().findElement(By.xpath("//*[contains(text(), 'Evidence One')]"));
        Assertions.assertEquals("Evidence One", title.getText());
    }

    @Given("I go to the evidence page with a project id")
    public void iGoToTheEevidencePageWithAProjectId() {
        seleniumExample.config.getDriver().get(seleniumExample.url + "/evidence?pi=1");
    }

    @Given("I click the Add Evidence button")
    public void iClickTheAddEvidenceButton() throws InterruptedException {

        Thread.sleep(500);
        WebElement button = seleniumExample.config.getDriver().findElement(By.id("add_button"));
        button.click();
        Thread.sleep(500);
    }

    @When("I have filled out all mandatory title, description, and date fields to an evidence")
    public void iHaveFilledOutAllMandatoryTitleDescriptionAndDateFieldsToAnEvidence(){
        WebElement titleField = seleniumExample.config.getDriver().findElement(By.id("evidence_title"));
        titleField.sendKeys("Evidence One");
        WebElement description = seleniumExample.config.getDriver().findElement(By.id("evidence_desc"));
        description.sendKeys("This evidence relates to the work done on the evidence page");
    }

    @When("I click the save button")
    public void iClickTheSaveButton() throws InterruptedException {

        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight/8)");
        Thread.sleep(100);
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
        scrollIntoView(saveButton);
        Assertions.assertTrue(saveButton.isEnabled());
        saveButton.click();
        Thread.sleep(200);
    }

    public void scrollIntoView(WebElement element) throws InterruptedException {
        ((JavascriptExecutor) seleniumExample.config.getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);
    }

    @Then("I will see a message that this evidence has saved successfully")
    public void iWillSeeAMessageThatThisEvidenceHasSavedSuccessfully() throws InterruptedException {
        Thread.sleep(100);
        WebElement message = seleniumExample.config.getDriver().findElement(By.id("display_box"));
        Assertions.assertEquals("Evidence has been added", message.getText());
    }

    @When("The all mandatory fields to an evidence are empty, I cannot click the save button")
    public void theAllMandatoryFieldsToAnEvidenceAreEmptyICannotClickTheSaveButton() {
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
        Assertions.assertFalse(saveButton.isEnabled());
    }


    @Then("I cannot click the save button")
    public void i_cannot_click_the_save_button() {
        WebElement saveButton = seleniumExample.config.getDriver().findElement(By.id("projectSave"));
        Assertions.assertEquals("true", saveButton.getAttribute("disabled"));
    }


    @When("I click the cancel button")
    public void iClickTheCancelButton() {


        WebElement cancelButton = seleniumExample.config.getDriver().findElement(By.id("cancelButton"));
        cancelButton.sendKeys(Keys.ENTER);

    }

    @Then("I can see the evidence creation page extract and replace by a plus button")
    public void iCanSeeTheEvidenceCreationPageExtractAndReplaceByAPlusButton() throws InterruptedException {

        Thread.sleep(500);
        WebElement iCanSeeTheEvidenceCreationPageExtractAndReplaceByAPlusButtonaddButtonaddButton = seleniumExample.config.getDriver().findElement(By.id("add_button"));

        Thread.sleep(500);
        Assertions.assertTrue(iCanSeeTheEvidenceCreationPageExtractAndReplaceByAPlusButtonaddButtonaddButton.isEnabled());
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
    public void iCanSeeThePrefilledDateIsTodaysDate() {
        WebElement date = seleniumExample.config.getDriver().findElement(By.id("date_input"));
        String currentDateExpected = DateParser.dateToStringHtml(new Date());
        String currentDateActual = date.getAttribute("value");
        Assertions.assertEquals(currentDateExpected, currentDateActual);
    }

    @When("I try to update the widget date to a new date that is within the project")
    public void iTryToUpdateTheWidgetDateToANewDateThatIsWithinTheProject() {
        WebElement date = seleniumExample.config.getDriver().findElement(By.id("date_input"));
        String minDateStringFormat = date.getAttribute("min");
        Date minDate = DateParser.stringToDate(minDateStringFormat);
        Calendar cal = Calendar.getInstance();
        assert minDate != null;
        cal.setTime(minDate);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        String month = String.valueOf(cal.get(Calendar.MONTH));
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        if (cal.get(Calendar.MONTH) < 10) {
            month = "0" + cal.get(Calendar.MONTH);
        }
        if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + cal.get(Calendar.DAY_OF_MONTH);
        }
        String dateToSend = String.format("%d-%s-%s", cal.get(Calendar.YEAR), month, day);
        date.sendKeys(dateToSend);
    }

    @Then("I can see the widget date field is set to the updated new date")
    public void iCanSeeTheWidgetDateFieldIsSetToTheUpdatedNewDate() {
        WebElement date = seleniumExample.config.getDriver().findElement(By.id("date_input"));
        String minDateStringFormat = date.getAttribute("min");
        Date minDate = DateParser.stringToDate(minDateStringFormat);
        Calendar cal = Calendar.getInstance();
        assert minDate != null;
        cal.setTime(minDate);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        String month = String.valueOf(cal.get(Calendar.MONTH));
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        if (cal.get(Calendar.MONTH) < 10) {
            month = "0" + cal.get(Calendar.MONTH);
        }
        if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + cal.get(Calendar.DAY_OF_MONTH);
        }
        String expected = String.format("%d-%s-%s", cal.get(Calendar.YEAR), month, day);
        String actual = date.getAttribute("value");
        Assertions.assertEquals(expected, actual);
    }

    @Then("Hovering my mouse over the question mark icon beside the date picker will give me information about it")
    public void hoveringMyMouseOverTheQuestionMarkIconBesideTheDatePickerWillGiveMeInformationAbout_It()
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
    public void iCanSeeThatTheRangeOfTheDateWidgetIsFilledInWithTheProjectDateRange() {
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

    /**
     *Gets the evidence id for the users evidence
     * @return evidence id - type String
     **/
    public String getEvidenceId(String title) {
        List<WebElement> elementsList = seleniumExample.config.getDriver().findElements(By.xpath("//*[contains(text(),'" + title + "')]"));
        return elementsList.get(0).getAttribute("id");
    }

    /**
     *Opens a piece of evidence
     **/
    public void viewFullPieceOfEvidence() throws InterruptedException {
        String getId = getEvidenceId("Evidence Delete");
        WebDriver driver = seleniumExample.config.getDriver();
        new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton"+getId)));
        WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + getId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, arrowButton);
        arrowButton.click();
    }

    /**
     *Opens a piece of evidence
     **/
    public void viewFullPieceOfEvidence(String arg0) throws InterruptedException {
        String getId = getEvidenceId(arg0);
        WebDriver driver = seleniumExample.config.getDriver();
        new WebDriverWait(seleniumExample.config.getDriver(), Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(By.id("ArrowButton"+getId)));
        WebElement arrowButton = seleniumExample.config.getDriver().findElement(By.id("ArrowButton" + getId));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, arrowButton);
        arrowButton.click();
    }

    @And("I view that piece of evidence {string}")
    public void iViewThatPieceOfEvidence(String arg0) throws InterruptedException {
        viewFullPieceOfEvidence(arg0);
    }

    @When("I view that piece of evidence")
    public void iViewThatPieceOfEvidence() throws InterruptedException {
        viewFullPieceOfEvidence();
    }

    @When("I view that piece of evidence that is not mine")
    public void iViewThatPieceOfEvidenceThatIsNotMine() throws InterruptedException {
        viewFullPieceOfEvidence();
    }
}
