package nz.ac.canterbury.seng302.identityprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import nz.ac.canterbury.seng302.identityprovider.model.*;

@SpringBootApplication
public class IdentityProviderApplication {

    private static final Logger log = LoggerFactory.getLogger(IdentityProviderApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(IdentityProviderApplication.class, args);
    }

/*    @Bean
    public CommandLineRunner demo(AccountProfileRepository repo) {
        return (args) -> {
            //Creates a new account for the user
            AccountProfile user = new AccountProfile("abc123", "abc123", "2022-01-01", "Hello my name is Allen :)", "abc123@uclive.ac.nz", "Allan", "Blue-cod", "Ally", "Mary");
            
            //Saves the user to the repository
            repo.save(user);

            //Finds the user from the repository, using the person's ID
            AccountProfile savedUser = repo.findById(user.getId());

            //Finds the same user again, this time using the person's username
            AccountProfile sameUser = repo.findByUsername(user.getUsername());
            
            //Prints the user's information to the log
            log.info(savedUser.toString());

            //Demonstrating that this is the same user
            log.info(sameUser.toString());
        };
    }*/

}
