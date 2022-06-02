package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.util.FileSystemUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityProviderConfig {
    /**
     * Returns a new instance of the file system utils bean.
     * @return a new instance of FileSystemUtils
     */
    @Bean
    public FileSystemUtils fileSystemUtils() {
        return new FileSystemUtils();
    }
}
