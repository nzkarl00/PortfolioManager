package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.evidence.WebLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.UnknownHostException;

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
     * Utilises Mono to aid in parallelisation / asynchronous calling, ie. avoid blocking.
     * @param link The web link to update
     * @return A mono resolving to the Web Link passed as a parameter
     */
    public Mono<WebLink> tryLink(final WebLink link) {
        final String NOT_FOUND_ERROR_MESSAGE = "Status is 404";
        return client.method(HttpMethod.GET)
            .uri(link.getUrl())
            .retrieve()
            .onStatus(
                    status -> status == HttpStatus.NOT_FOUND,
                    clientResponse -> Mono.error(new Error("Status is 404"))
            )
            .toEntity(String.class)
            .doOnError(throwable -> {
                if (throwable.getClass() == WebClientRequestException.class) {
                    if (throwable.getCause().getClass() == UnknownHostException.class) {
                        logger.warn(String.format(
                                "Requesting link resulted in unknown host error, link: %s",
                                link.url
                        ));
                    } else {
                        logger.warn("Requesting link resulted in exception", throwable);
                    }
                    link.setFetchResult(true);
                } else if (throwable.getMessage().equals(NOT_FOUND_ERROR_MESSAGE)) {
                    logger.warn("Requesting link resulted in a 404");
                    link.setFetchResult(true);
                }
            })
            .doOnNext(responseEntity -> {
                logger.debug("Fetching URL resulted in status code: " + responseEntity.getStatusCode().toString());
                link.setFetchResult(false);
            })
            .map(e -> link);
    }

}
