package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import org.junit.Assert;
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
import java.util.NoSuchElementException;

public class DeleteEvidenceStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;
    WebDriver driver = seleniumExample.config.getDriver();

    /**
     *Gets the evidence id for the users evidence
     * @return evidence id - type String
     **/
    public String getEvidenceId(String title) {
        List<WebElement> elementsList = seleniumExample.config.getDriver().findElements(By.xpath("//*[contains(text(),'" + title + "')]"));
        return elementsList.get(0).getAttribute("id");
    }


    @Then("I can see a delete icon for {string}")
    public void i_can_see_a_delete_icon_for(String title) throws InterruptedException {
        String evidenceId = getEvidenceId(title);
        WebElement deleteIcon = driver.findElement(By.xpath("//button[@data-target='#deleteModal" + evidenceId + "']"));
        Assertions.assertTrue(deleteIcon.isEnabled());



    }

    @Then("I can click the delete Icon for {string}")
    public void i_can_click_the_delete_icon_for(String title) throws InterruptedException {
        String evidenceId = getEvidenceId(title);
        WebElement deleteIcon = driver.findElement(By.xpath("//button[@data-target='#deleteModal" + evidenceId + "']"));
        deleteIcon.sendKeys(Keys.ENTER);
    }

    @Then("I cannot see a delete icon")
    public void iCannotSeeADeleteIcon() {
        String evidenceId = getEvidenceId("Evidence Delete");
        try {
            WebElement button = seleniumExample.config.getDriver().findElement(By.className("group_delete_button"));
            Assertions.assertNotEquals(evidenceId, button.getAttribute("id"));
        } catch(Exception e) {

        }
    }


    @Then("A model appears containing the evidence title {string}")
    public void a_model_appears_containing_the_evidence_title(String title) throws InterruptedException {
        List<WebElement> list = driver.findElements(By.xpath("//*[contains(text(),'" + title + "')]"));
        Assertions.assertFalse(list.isEmpty());
    }


    @When("I click cancel")
    public void iClickCancel() throws InterruptedException {
        Thread.sleep(700);
        WebElement cancelButton = driver.findElement(By.id("cancelButton"));
        BaseSeleniumStepDefs.scrollWindowToElement(driver, cancelButton);
        Thread.sleep(700);
        cancelButton.sendKeys(Keys.ENTER);
    }



    @When("I click Delete")
    public void i_click_delete() throws InterruptedException {
        Thread.sleep(100);
        WebElement cancelButton = driver.findElement(By.id("deleteButton"));
        Thread.sleep(300);
        cancelButton.submit();
    }


    @Then("I cannot view that piece of evidence {string}")
    public void i_cannot_view_that_piece_of_evidence(String string) {
        //https://stackoverflow.com/questions/11454798/how-can-i-check-if-some-text-exist-or-not-in-the-page-using-selenium
        List<WebElement> list = driver.findElements(By.xpath("//*[contains(text(),'" + string + "')]"));
        Assert.assertTrue("Text not found!", list.size() > 0);

    }


    @When("I have filled out all mandatory title {string}, description {string}, and date fields to an evidence")
    public void i_have_filled_out_all_mandatory_title_description_and_date_fields_to_an_evidence(String title, String desc) {
        WebElement titleField = seleniumExample.config.getDriver().findElement(By.id("evidence_title"));
        titleField.sendKeys(title);
        WebElement description = seleniumExample.config.getDriver().findElement(By.id("evidence_desc"));
        description.sendKeys(desc);
    }


//    @Then("A model appears containing the evidence title")
//    public void aModelAppearsContainingTheEvidenceTitle() {
//        String evidenceId = getEvidenceId("Evidence One");
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
//        WebElement modalContainer = driver.findElement(By.id("deleteModal"+evidenceId));
//        WebElement modelTitle = modalContainer.findElement(By.id("exampleModalLongTitle"));
//        String expected = "Delete - Evidence One?";
//        Assertions.assertEquals(expected, modelTitle.getText());
//
//    }
}
