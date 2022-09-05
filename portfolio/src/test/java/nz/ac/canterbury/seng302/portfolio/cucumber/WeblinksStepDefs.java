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

    /**
     * https://stackoverflow.com/questions/18510576/find-an-element-by-text-and-get-xpath-selenium-webdriver-junit
     * get the xpath of aan element
     * @param childElement the element to get the xpath from
     * @param current the current xpath to recurse onto
     * @return the xpath in the form of a string
     */
    private String generateXPATH(WebElement childElement, String current) {
        String childTag = childElement.getTagName();
        if(childTag.equals("html")) {
            return "/html[1]"+current;
        }
        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
        int count = 0;
        for(int i=0;i<childrenElements.size(); i++) {
            WebElement childrenElement = childrenElements.get(i);
            String childrenElementTag = childrenElement.getTagName();
            if(childTag.equals(childrenElementTag)) {
                count++;
            }
            if(childElement.equals(childrenElement)) {
                return generateXPATH(parentElement, "/" + childTag + "[" + count + "]"+current);
            }
        }
        return null;
    }

    @Given("I open the piece of evidence")
    public void openEvidence() throws InterruptedException {
        // get the xpath of the desired pieve of evidence
        String xpath = BaseSeleniumStepDefs.generateXPATH(seleniumExample.config.getDriver().findElement(By.xpath("//*[contains(text(), 'Evidence One')]")), "");
        // get the button's xpath based on the title's xpath

        WebElement button = seleniumExample.config.getDriver().findElement(By.xpath(xpath.substring(0,66) + "/div[4]/a"));
        ((JavascriptExecutor) seleniumExample.config.getDriver())
                .executeScript("arguments[0].scrollIntoView(true);", button);
        Thread.sleep(100);
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
                Thread.sleep(100);
                element.click();
            } catch (ElementNotInteractableException | InterruptedException e) {
                continue;
            }
        }
    }

    @Then("I am taken to wikipedia in a new tab")
    public void iAmTakenToWikipediaInANewTab() {
        Set<String> tabs = seleniumExample.config.getDriver().getWindowHandles();
        Assertions.assertTrue(tabs.size() > 0);
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
