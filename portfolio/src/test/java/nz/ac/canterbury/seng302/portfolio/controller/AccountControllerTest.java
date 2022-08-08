package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.testUserStudent;
import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.validAuthStateStudent;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    NavController navController;

    @MockBean
    AccountClientService accountClientService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(AccountController.class)
                .build();
    }

    static MockedStatic<AuthStateInformer> utilities;

    @BeforeAll
    public static void open() {
        utilities = Mockito.mockStatic(AuthStateInformer.class);
    }

    @AfterAll
    public static void close() {
        utilities.close();
    }

    @Test
    public void getAccountWithValidCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        when(accountClientService.getUserById(1)).thenReturn(testUserStudent);

        utilities.when(() -> AuthStateInformer.getId(validAuthStateStudent)).thenReturn(1);

        // Expected values in the model
        String name = testUserStudent.getFirstName() + " " + testUserStudent.getLastName();
        String nickname = testUserStudent.getNickname();
        String email = testUserStudent.getEmail();
        String bio = testUserStudent.getBio();
        String roles = "STUDENT";
        String pronouns = testUserStudent.getPersonalPronouns();

        // Spring now thinks we are logged in as the user specified in validAuthState, so any request made from now on will be authenticated.
        // Ready to make a request to any endpoint which requires authentication
        mockMvc.perform(get("/account"))
            .andExpect(status().isOk()) // Whether to return the status "200 OK"
            .andExpect(view().name("account")) // Whether to return the template "account"
             //Model test.
            .andExpect(model().attribute("name", name))
            .andExpect(model().attribute("nickname", nickname))
            .andExpect(model().attribute("email", email))
            .andExpect(model().attribute("roles", roles))
            .andExpect(model().attribute("pronouns", pronouns))
            .andExpect(model().attribute("bio", bio));
    }

}
