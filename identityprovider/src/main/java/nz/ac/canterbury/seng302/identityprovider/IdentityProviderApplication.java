package nz.ac.canterbury.seng302.identityprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;

@SpringBootApplication
public class IdentityProviderApplication {

    private static final Logger log = LoggerFactory.getLogger(IdentityProviderApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(IdentityProviderApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(AccountProfileRepository repo) {
        return (args) -> {
           System.out.println("\nTest\nTest\nTest\nTest\nTest\nTest");
        repo.save((new AccountProfile("toby", "fakePassword", "2022-1-1", "bio", "email", "photopath")));
        //AccountProfile toby = repo.findByUsername("toby");
        //log.info(toby.toString());
        //AccountProfile test = new AccountProfile();
        //test.setID(Long.valueOf(435));
        //test.setEmail("test@email");
        //test.setUsername("newUsername");
        //System.out.println("ddddddddddddddddddddd\nTest\nTest\nddddddddddddTest\nTest\nTest\nTest");
        //repo.save(test);
        //System.out.println("\nTest\nTest\nTest\nTest\nTest\nTest");
        };
    }

}
