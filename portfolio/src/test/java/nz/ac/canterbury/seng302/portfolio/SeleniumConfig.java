package nz.ac.canterbury.seng302.portfolio;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

public class SeleniumConfig {

    private WebDriver driver;

    public SeleniumConfig() {
        System.setProperty("webdriver.gecko.driver", "E:\\Downloads\\geckodriver-v0.31.0-win64\\geckodriver.exe");
        driver = new FirefoxDriver();
        driver.get("http://www.google.com");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    public WebDriver getDriver() {
        return driver;
    }
}
