package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AccountPhotoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {
    @Bean
    public AccountClientService accountClientService() {
        return new AccountClientService();
    }


    @Bean
    public AccountPhotoService uploadService() {
        return new AccountPhotoService();
    }
}
