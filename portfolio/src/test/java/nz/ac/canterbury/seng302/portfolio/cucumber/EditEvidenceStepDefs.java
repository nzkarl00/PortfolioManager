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

public class EditEvidenceStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;
    WebDriver driver = seleniumExample.config.getDriver();

    CommonEvidenceServices commonEvidenceServices = new CommonEvidenceServices();

    /**
     *Can be used to scroll window to element
     * @Param element webElement that the window is to scroll too
     **/
    public void scrollWindowToElement(WebElement element) {
        //https://learn-automation.com/how-to-scroll-into-view-in-selenium-webdriver/
        JavascriptExecutor je = (JavascriptExecutor) driver;
        je.executeScript("arguments[0].scrollIntoView(true);",element);
    }


    @When("I click the edit icon")
    public void i_click_the_edit_icon() {
        String getId = commonEvidenceServices.getEvidenceId("Evidence One");
        WebElement element = driver.findElement(By.id(getId));
        scrollWindowToElement(element);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        WebElement button = driver.findElement(By.className("edit_evidence"));
        button.click();
    }

    @Then("I cannot click the edit icon")
    public void i_cannot_click_the_edit_icon() {
        String getId = commonEvidenceServices.getEvidenceId("Evidence One");
        try {
            WebElement button = driver.findElement(By.className("edit_evidence"));
            Assertions.assertNotEquals(getId, button.getAttribute("id"));
        } catch(Exception e) {

        }
    }
}