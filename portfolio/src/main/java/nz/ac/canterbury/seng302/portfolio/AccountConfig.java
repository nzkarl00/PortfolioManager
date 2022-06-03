package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.controller.NavController;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AccountPhotoService;
import nz.ac.canterbury.seng302.portfolio.service.GroupClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {
    @Bean
    public AccountClientService accountClientService() {
        return new AccountClientService();
    }

    @Bean
    public GroupClientService groupClientService() {
        return new GroupClientService();
    }

    @Bean
    public NavController navController() {
        return new NavController();
    }

    @Bean
    public AccountPhotoService uploadService() {
        return new AccountPhotoService();
    }
}
