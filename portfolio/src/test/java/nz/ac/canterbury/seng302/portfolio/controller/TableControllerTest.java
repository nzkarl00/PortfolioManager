package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.Before;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TableController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TableControllerTest {

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
    AccountClientService accountClientService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(TableController.class)
            .build();
    }

    //String role = testUser.getRoles();

    @Test
    public void getUserListWithValidCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        MockedStatic<AuthStateInformer> utilities = Mockito.mockStatic(AuthStateInformer.class);
        utilities.when(() -> AuthStateInformer.getRole(validAuthState)).thenReturn(role);
        utilities.when(() -> AuthStateInformer.getId(validAuthState)).thenReturn(1);
        when(accountClientService.getUserById(1)).thenReturn(testUser);

        mockMvc.perform(get("/user-list"))
            .andExpect(status().isOk()) // Whether to return the status "200 OK"
            .andExpect(MockMvcResultMatchers.view().name("userList")) // Whether to return the template "account"
            .andExpect(MockMvcResultMatchers.model().attribute("userRole", role));
    }

}
