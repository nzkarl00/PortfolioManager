package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Set;

public class WeblinksStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;

    @Given("I open the piece of evidence")
    public void openEvidence() throws InterruptedException {
        // open the first piece of evidence
        WebElement button = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div/div[1]/div[1]/div[5]/div[2]/div[1]/div[4]/a/i"));
        button.click();
        // wait for dropdown
        Thread.sleep(500);
    }

    @And("I click the weblink")
    public void iClickTheWeblink() {
        WebElement link = seleniumExample.config.getDriver().findElement(By.id("evidence_links_text_https://en.wikipedia.org/wiki/Main_Page"));
        link.click();
    }

    @Then("I am taken to wikipedia in a new tab")
    public void iAmTakenToWikipediaInANewTab() {
        Set<String> tabs = seleniumExample.config.getDriver().getWindowHandles();
        Assertions.assertTrue(tabs.size() > 1);
    }
}
