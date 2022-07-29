package nz.ac.canterbury.seng302.portfolio.gradle.cucumber;

import com.google.protobuf.Timestamp;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber-report.html"},
        features = {"src/test/resources"},
        glue = "nz.ac.canterbury.seng302.portfolio.gradle.cucumber"

)
public class RunCucumberTest {

}
