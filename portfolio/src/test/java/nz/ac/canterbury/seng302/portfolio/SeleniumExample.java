package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.integration.SeleniumConfig;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

public class SeleniumExample {
    SeleniumConfig config;
    @LocalServerPort
    private int port = 9000;
    public String url = "http://localhost:" + port;

    public SeleniumExample(String contextPath) {
        config = new SeleniumConfig();
        config.getDriver().get(url + contextPath);
    }

    public void closeWindow() {
        this.config.getDriver().close();
    }
}
