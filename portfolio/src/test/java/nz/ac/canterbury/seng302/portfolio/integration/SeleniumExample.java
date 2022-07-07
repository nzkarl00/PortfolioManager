package nz.ac.canterbury.seng302.portfolio.integration;

import nz.ac.canterbury.seng302.portfolio.integration.SeleniumConfig;
import org.openqa.selenium.By;
import org.springframework.boot.web.server.LocalServerPort;

public class SeleniumExample {
    SeleniumConfig config;
    @LocalServerPort
    private int port = 9000;
    public String url = "http://localhost:" + port;
    public String url3 = "http://localhost:" + port + "/landing";
    public String url2 = "http://localhost:" + port + "/details?id=1";
    public String urlDates;

    public SeleniumExample(String contextPath) {
        config = new SeleniumConfig();
        config.getDriver().get(url + contextPath);
    }

    public void closeWindow() {
        this.config.getDriver().close();
    }

}
