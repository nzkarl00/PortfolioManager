package nz.ac.canterbury.seng302.portfolio.integration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
public class SeleniumWithTestNGLiveTest_GroupDetails {

    private SeleniumExample seleniumExample;

    String passwordText = "";
    String projectUserUrls = "/user-list";
    String projectGroups = "/group?id=1";

    @BeforeSuite
    public void setUp() throws FileNotFoundException, InterruptedException {
        seleniumExample = new SeleniumExample("");

        getPassword_ForAdmin_FromTextFile();
        whenPortfolioIsLoaded_thenLoginAdmin_forTests();

        whenLoggedInAsAdmin_AddTeacherRoleToSelf_ForTest();
        whenAddedTeacherRole_AccessGroupsPage();
        whenOnGroupsPage_AccessTeacherGroup_ForDetails();


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
     * Add the teacher role to self to test if changes in roles are accounted for
     */
    public void whenLoggedInAsAdmin_AddTeacherRoleToSelf_ForTest() {
        seleniumExample.config.getDriver().get(seleniumExample.url + projectUserUrls);
        WebElement firstChild = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div[2]/table/tbody/tr[1]/td[5]/div[1]/span"));
        Assertions.assertEquals("ADMIN", firstChild.getText());
        try {
            WebElement secondChild = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div[2]/table/tbody/tr[1]/td[5]/div[2]/span"));
            Assertions.assertEquals("TEACHER", secondChild.getText());
        }
        catch(Exception e) {
            WebElement rolesButton = seleniumExample.config.getDriver().findElement(By.xpath("//*[@id=\"dropdownMenuButton\"]"));
            rolesButton.click();
            WebElement teacherButton = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div[2]/table/tbody/tr[1]/td[5]/div[2]/div/a[2]"));
            teacherButton.click();
        }
        try {
            WebElement secondChild = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div[2]/table/tbody/tr[1]/td[5]/div[2]/span"));
            Assertions.assertEquals("TEACHER", secondChild.getText());

        }
        catch(Exception e) {

        }

    }

    /**
     * Use the navbar to go to the groups page and check for new info
     */
    public void whenAddedTeacherRole_AccessGroupsPage() {
        WebElement navbutton = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[1]/nav/div/div/button"));
        navbutton.click();
        WebElement groupsButton = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[1]/nav/div/div/ul/li[4]/a"));
        groupsButton.click();

        WebElement teacherGroupAdminFirstName = seleniumExample.config.getDriver().findElement(By.xpath("//*[@id=\"groupUserTableTG\"]/tbody/tr/td[2]/span"));
        Assertions.assertEquals("admin", teacherGroupAdminFirstName.getText());
        WebElement teacherGroupAdminLastName = seleniumExample.config.getDriver().findElement(By.xpath("//*[@id=\"groupUserTableTG\"]/tbody/tr/td[3]/span"));
        Assertions.assertEquals("admin", teacherGroupAdminLastName.getText());
        WebElement teacherGroupAdminUsername = seleniumExample.config.getDriver().findElement(By.xpath("//*[@id=\"groupUserTableTG\"]/tbody/tr/td[4]/span"));
        Assertions.assertEquals("admin", teacherGroupAdminUsername.getText());
        WebElement teacherGroupAdminAlias = seleniumExample.config.getDriver().findElement(By.xpath("//*[@id=\"groupUserTableTG\"]/tbody/tr/td[5]/span"));
        Assertions.assertEquals("", teacherGroupAdminAlias.getText());
    }

    /**
     * Access the teacher's group to check if the specific details are present
     */
    public void whenOnGroupsPage_AccessTeacherGroup_ForDetails() {
        seleniumExample.config.getDriver().get(seleniumExample.url + projectGroups);
        seleniumExample.config.getDriver().get(seleniumExample.url + projectGroups);
        WebElement teacherGroupTitle = seleniumExample.config.getDriver().findElement(By.xpath("/html/body/div[2]/div/div[2]/div[2]/p"));
        Assertions.assertEquals("Teachers Group (TG)", teacherGroupTitle.getText());

        WebElement teacherGroupAdminFirstName = seleniumExample.config.getDriver().findElement(By.xpath("//*[@id=\"groupUserTableTG\"]/tbody/tr/td[1]/span"));
        Assertions.assertEquals("admin", teacherGroupAdminFirstName.getText());
        WebElement teacherGroupAdminLastName = seleniumExample.config.getDriver().findElement(By.xpath("//*[@id=\"groupUserTableTG\"]/tbody/tr/td[2]/span"));
        Assertions.assertEquals("admin", teacherGroupAdminLastName.getText());
        WebElement teacherGroupAdminUsername = seleniumExample.config.getDriver().findElement(By.xpath("//*[@id=\"groupUserTableTG\"]/tbody/tr/td[3]/span"));
        Assertions.assertEquals("admin", teacherGroupAdminUsername.getText());
        WebElement teacherGroupAdminAlias = seleniumExample.config.getDriver().findElement(By.xpath("//*[@id=\"groupUserTableTG\"]/tbody/tr/td[4]/span"));
        Assertions.assertEquals("", teacherGroupAdminAlias.getText());

    }


}
