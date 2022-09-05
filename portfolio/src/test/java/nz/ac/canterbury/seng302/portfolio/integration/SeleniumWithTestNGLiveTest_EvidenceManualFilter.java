package nz.ac.canterbury.seng302.portfolio.integration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * Note this is a template to pull from
 * import org.testng.annotations.Test;
 * use this test annotation to intiate your actual tests
 * do not remove the existing tests
 * they are needed to setup and login to an account
 */
public class SeleniumWithTestNGLiveTest_EvidenceManualFilter {

    private SeleniumExample seleniumExample;

    String passwordText = "";

    @BeforeSuite
    public void setUp() throws FileNotFoundException, InterruptedException {
        seleniumExample = new SeleniumExample("");

        getPassword_ForAdmin_FromTextFile();
        whenPortfolioIsLoaded_thenLoginAdmin_forTests();

        goTo_evidencePage();
        clickTo_Category();
        swapTo_Skill();
        swapTo_Category();
        swapTo_Skill();
        swapTo_Category();
        search_Arbitrary();

    }

    @AfterSuite
    public void tearDown() {
        seleniumExample.closeWindow();
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

    /**
     * Enter the evidence page and validate the title
     */
    public void goTo_evidencePage() {
        seleniumExample.config.getDriver().get(seleniumExample.url+"/evidence");
        WebElement pageTitle = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div/div[1]/div[1]/div[2]/div/p"));
        Assertions.assertEquals("List Of Evidence", pageTitle.getText());
    }

    /**
     * Click to the category section with a fresh page
     */
    public void clickTo_Category() {
        Select category = new Select(seleniumExample.config.getDriver().findElement(By.id("ci")));
        category.selectByValue("Qualitative Skills");
        WebElement option = category.getFirstSelectedOption();
        String defaultItem = option.getText();
        Assertions.assertEquals("  Qualitative skills", defaultItem);

    }

    public void swapTo_Skill() {
        Select categoryCI = new Select(seleniumExample.config.getDriver().findElement(By.id("ci")));
        Select categorySI = new Select(seleniumExample.config.getDriver().findElement(By.id("si")));
        categorySI.selectByValue("No_skills");
        WebElement option = categoryCI.getFirstSelectedOption();
        String defaultItem = option.getText();
        Assertions.assertEquals("Choose here", defaultItem);
    }

    public void swapTo_Category() {
        Select categoryCI = new Select(seleniumExample.config.getDriver().findElement(By.id("ci")));
        Select categorySI = new Select(seleniumExample.config.getDriver().findElement(By.id("si")));
        categoryCI.selectByValue("Qualitative Skills");
        WebElement option = categorySI.getFirstSelectedOption();
        String defaultItem = option.getText();
        Assertions.assertEquals("Choose here", defaultItem);
    }

    public void search_Arbitrary(){
        WebElement searchPage = seleniumExample.config.getDriver().findElement(By.id("searchEvidence"));
        searchPage.click();
        Select categoryCI = new Select(seleniumExample.config.getDriver().findElement(By.id("ci")));
        Select categorySI = new Select(seleniumExample.config.getDriver().findElement(By.id("si")));
        WebElement optionC = categoryCI.getFirstSelectedOption();
        String defaultItemC = optionC.getText();
        WebElement optionS = categorySI.getFirstSelectedOption();
        String defaultItemS = optionS.getText();
        Assertions.assertEquals("Choose here", defaultItemC);
        Assertions.assertEquals("Choose here", defaultItemS);
    }


}
