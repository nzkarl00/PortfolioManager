package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.Set;

public class WeblinksStepDefs {

    SeleniumExample seleniumExample = BaseSeleniumStepDefs.seleniumExample;

    @Given("I open the piece of evidence")
    public void openEvidence() throws InterruptedException {
        // get the xpath of the desired pieve of evidence
        String xpath = BaseSeleniumStepDefs.generateXPATH(seleniumExample.config.getDriver().findElement(By.xpath("//*[contains(text(), 'Test Evidence')]")), "");
        // get the button's xpath based on the title's xpath
        WebElement button = seleniumExample.config.getDriver().findElement(By.xpath(xpath.substring(0,66) + "div[4]/a"));
        button.click();
        // wait for dropdown
        Thread.sleep(500);
    }

    @And("I click the weblink")
    public void iClickTheWeblink() {
        List<WebElement> links = seleniumExample.config.getDriver().findElements(By.id("evidence_links_text_https://en.wikipedia.org/wiki/Main_Page"));
        for (WebElement element: links) {
            try {
                // https://stackoverflow.com/questions/3401343/scroll-element-into-view-with-selenium
                ((JavascriptExecutor) seleniumExample.config.getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
                Thread.sleep(500);
                element.click();
            } catch (ElementNotInteractableException | InterruptedException e) {
                continue;
            }
        }
    }

    @Then("I am taken to wikipedia in a new tab")
    public void iAmTakenToWikipediaInANewTab() {
        Set<String> tabs = seleniumExample.config.getDriver().getWindowHandles();
        Assertions.assertTrue(tabs.size() > 1);
    }

    @Then("Wikipedia link has a closed padlock")
    public void wikipediaLinkHasAClosedPadlock() {
        List<WebElement> links = seleniumExample.config.getDriver().findElements(By.id("lockedhttps://en.wikipedia.org/wiki/Main_Page"));
        Assertions.assertTrue(links.size() > 0);
    }

    @And("Fake Cern link has a open padlock")
    public void fakeCernLinkHasAOpenPadlock() {
        List<WebElement> links = seleniumExample.config.getDriver().findElements(By.id("unlockedhttp://info.cern.ch/"));
        Assertions.assertTrue(links.size() > 0);
    }
}
