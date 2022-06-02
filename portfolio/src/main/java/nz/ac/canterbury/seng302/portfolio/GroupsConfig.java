package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupsConfig {
    @Bean
    public GroupsClientService groupsClientService() {
        return new GroupsClientService();
    }
}
