package nz.ac.canterbury.seng302.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PortfolioApplication {

    private static final Logger log = LoggerFactory.getLogger(PortfolioApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PortfolioApplication.class, args);
    }
}
