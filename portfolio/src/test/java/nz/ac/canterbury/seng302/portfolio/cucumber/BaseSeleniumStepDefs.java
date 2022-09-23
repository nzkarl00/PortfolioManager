package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumLogins;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.FileNotFoundException;
import java.util.List;

public class BaseSeleniumStepDefs {

    public static SeleniumExample seleniumExample;

    String passwordText = "";

    private boolean registered = false;

    @Before("@Admin")
    public void adminIsLoggedIn() throws FileNotFoundException, InterruptedException {
        iAmAuthenticatedAsAAdmin();
    }

    @Given("User is logged in.")
    public void userIsLoggedIn() {
        seleniumExample = new SeleniumExample("");
        if (!registered) {
            SeleniumLogins.whenPortfolioIsLoaded_thenRegisterWorks(seleniumExample);
            registered = true;
        }
        SeleniumLogins.whenPortfolioIsLoaded_thenLoginWorks(seleniumExample);
    }

    /**
     *Can be used to scroll window to element
     * @Param element webElement that the window is to scroll too
     **/
    public static void scrollWindowToElement(WebDriver driver, WebElement element)
            throws InterruptedException {
        //https://learn-automation.com/how-to-scroll-into-view-in-selenium-webdriver/
        JavascriptExecutor je = (JavascriptExecutor) driver;
        je.executeScript("arguments[0].scrollIntoView(true);",element);
        Thread.sleep(300);
    }

    /**
     * https://stackoverflow.com/questions/18510576/find-an-element-by-text-and-get-xpath-selenium-webdriver-junit
     * get the xpath of aan element
     * @param childElement the element to get the xpath from
     * @param current the current xpath to recurse onto
     * @return the xpath in the form of a string
     */
    public static String generateXPATH(WebElement childElement, String current) {
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

    @And("I am authenticated as a admin")
    public void iAmAuthenticatedAsAAdmin() throws FileNotFoundException, InterruptedException {
        seleniumExample = new SeleniumExample("");
        passwordText = SeleniumLogins.getPassword_ForAdmin_FromTextFile();
        SeleniumLogins.whenPortfolioIsLoaded_thenLoginAdmin_forTests(seleniumExample, passwordText);
    }

    @After("@Close")
    public void theWindowIsClosed() {
        seleniumExample.closeWindow();
    }

    @When("User navigates to {string}.")
    public void userNavigatesTo(String arg0) throws InterruptedException {
        seleniumExample.config.getDriver().get(seleniumExample.url + "/" + arg0);
        Thread.sleep(500);
    }
}
