package nz.ac.canterbury.seng302.portfolio.service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.ChannelOption;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * TODO
 */
@Service
public class WebLinkClient {
    WebClient client = WebClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
        .responseTimeout(Duration.ofMillis(5000));

    Logger logger = LoggerFactory.getLogger(WebLinkClient.class);

    public WebLinkClient(final String url, final String apiKey) {
    }



}
