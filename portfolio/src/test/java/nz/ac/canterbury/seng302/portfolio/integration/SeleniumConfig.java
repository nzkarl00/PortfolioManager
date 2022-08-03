package nz.ac.canterbury.seng302.portfolio.integration;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SeleniumConfig {

    private WebDriver driver;

    /**
     * Makes the browser to test implementation on
     */
    public SeleniumConfig() {
        Capabilities capabilities = DesiredCapabilities.firefox();
        driver = new FirefoxDriver(capabilities);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    public WebDriver getDriver() {
        return driver;
    }

    // I set up a file directory of web-drivers
    // so that hopefully most of that can be done automatically
    static {
        String suffix;

        String bit = System.getProperty("os.arch");
        if (bit.contains("64")) {
            bit = "64";
        } else {
            bit = "32";
        }

        String os = System.getProperty("os.name");
        if (os.toLowerCase(Locale.ROOT).contains("window")) {
            os = "Windows";
            suffix = ".exe";
        } else if (os.toLowerCase(Locale.ROOT).contains("linux")) {
            os = "Linux";
            suffix = "";
        } else {
            os = "Mac";
            suffix = ".mac";
        }
        System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "/WebDrivers/FireFox/" + os + "/" + bit + "/geckodriver" + suffix);
    }
}
