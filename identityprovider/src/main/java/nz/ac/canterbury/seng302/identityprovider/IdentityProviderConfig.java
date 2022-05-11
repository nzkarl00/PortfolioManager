package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.util.FileSystemUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityProviderConfig {
    @Bean
    public FileSystemUtils fileSystemUtils() {
        return new FileSystemUtils();
    }
}