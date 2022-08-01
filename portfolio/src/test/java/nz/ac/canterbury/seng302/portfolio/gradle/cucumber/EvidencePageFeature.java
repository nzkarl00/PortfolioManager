package nz.ac.canterbury.seng302.portfolio.gradle.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.integration.SeleniumExample;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class EvidencePageFeature {

    private SeleniumExample seleniumExample;

    String passwordText = "";


    @Given("There is evidence in the table")
    public void thereIsEvidenceInTheTable() throws InterruptedException {
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
        WebElement title = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div/div[3]/div[2]/div[1]/div[1]/p"));
        WebElement button = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div/div[3]/div[2]/div[1]/div[3]/a"));
        button.click();
        Thread.sleep(500);
        WebElement description = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div/div[3]/div[2]/div[2]/div/div/p"));
        WebElement date = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div/div[3]/div[2]/div[1]/div[2]/p"));
        Assertions.assertEquals("Title", title.getText());
        Assertions.assertEquals("Description", description.getText());
        Assertions.assertEquals("3020-01-10", date.getText());
    }
}
