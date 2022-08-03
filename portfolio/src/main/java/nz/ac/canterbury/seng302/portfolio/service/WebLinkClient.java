package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.evidence.WebLink;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * TODO
 */
@Service
public class WebLinkClient {
    WebClient client = WebClient.create();
//        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
//        .responseTimeout(Duration.ofMillis(5000));

    private final Logger logger = LoggerFactory.getLogger(WebLinkClient.class);

    public WebLinkClient() {
    }


    /**
     * Given a supplied web link, attempt to fetch the link.
     * Modifies the properties of the link that is received, without returning a new link.
     * @param link
     */
    public void tryLink(final WebLink link) {
        final int notFoundCode = 404;
        Mono<String> uriSpec = client.method(HttpMethod.GET)
            .uri(link.url)
            .retrieve()
            .onStatus(
                    status -> status.value() == notFoundCode,
                    clientResponse -> Mono.error(new Error("Status is 404"))
            )
            .bodyToMono(String.class);
        try {
            String res = uriSpec.block();
            logger.info(res);
            link.setFetchResult(false);
            return
        } catch (Error e) {
            logger.warn("Requesting link resulted in a 404");
        } catch (WebClientException e) {
            logger.warn("Requesting link resulted in exception", e);
        }
    }

}
