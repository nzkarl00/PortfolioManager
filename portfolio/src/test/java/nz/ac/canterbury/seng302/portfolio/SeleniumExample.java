package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.integration.SeleniumConfig;
import org.springframework.boot.web.server.LocalServerPort;

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
