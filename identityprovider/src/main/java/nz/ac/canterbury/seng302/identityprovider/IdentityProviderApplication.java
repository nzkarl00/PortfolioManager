package nz.ac.canterbury.seng302.identityprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;

import java.util.Date;


@SpringBootApplication
public class IdentityProviderApplication {

    private static final Logger log = LoggerFactory.getLogger(IdentityProviderApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(IdentityProviderApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(AccountProfileRepository repo) {
        return (args) -> {
            AccountProfile user = new AccountProfile("abc123", "abc123", new Date(), "Hello my name is Allen :)", "abc123@uclive.ac.nz", "photopath");
            repo.save(user);
        };
    }
}

