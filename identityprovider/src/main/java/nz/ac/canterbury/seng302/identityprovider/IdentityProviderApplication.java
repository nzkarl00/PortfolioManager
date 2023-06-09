package nz.ac.canterbury.seng302.identityprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * main app to run the server
 */
@SpringBootApplication
public class IdentityProviderApplication {

    /**
     * the log to report errors
     */
    private static final Logger LOG = LoggerFactory.getLogger(
            IdentityProviderApplication.class
    );

    /**
     * run the main application to init the server
     * @param args default main param
     */
    public static void main(String[] args) {
        SpringApplication.run(IdentityProviderApplication.class, args);
    }
}

