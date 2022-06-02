package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTest {

    public AuthState validAuthState = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("ADMIN").build()) // Set the mock user's role
            .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    private AuthState invalidAuthState = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("invalidtesttoken")
            .build();

    private UserResponse testUser = UserResponse.newBuilder()
        .setBio("testbio")
        .setCreated(Timestamp.newBuilder().setSeconds(10))
        .setEmail("test@email")
        .setFirstName("testfirstname")
        .setLastName("testlastname")
        .setMiddleName("testmiddlename")
        .setNickname("testnickname")
        .setPersonalPronouns("test/test")
        .addRoles(UserRole.STUDENT)
        .build();

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
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        when(accountClientService.getUserById(1)).thenReturn(testUser);

        utilities.when(() -> AuthStateInformer.getId(validAuthState)).thenReturn(1);

        // Expected values in the model
        String name = testUser.getFirstName() + " " + testUser.getLastName();
        String nickname = testUser.getNickname();
        String email = testUser.getEmail();
        String bio = testUser.getBio();
        String roles = "STUDENT";
        String pronouns = testUser.getPersonalPronouns();

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
