package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.evidence.WebLink;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
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
        Mono<ResponseEntity<String>> uriSpec = client.method(HttpMethod.GET)
            .uri(link.url)
            .retrieve()
            .onStatus(
                    status -> status == HttpStatus.NOT_FOUND,
                    clientResponse -> Mono.error(new Error("Status is 404"))
            )
            .toEntity(String.class);

        try {
            ResponseEntity res = uriSpec.block();
            logger.info(res.getStatusCode().toString());
            logger.info(res.getHeaders().toString());
            logger.info(res.getHeaders().getLocation().toString());
        } catch (Error e) {
            logger.warn("Requesting link resulted in a 404");
        } catch (WebClientRequestException e) {
            if (e.getCause().getClass() == UnknownHostException.class) {
                logger.warn(String.format(
                    "Requesting link resulted in unknown host error, link: %s",
                    link.url
                ));
            } else {
                logger.warn("Requesting link resulted in exception", e);
            }
            link.setFetchResult(false, true);
        }
    }

}
