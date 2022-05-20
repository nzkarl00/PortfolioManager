package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.UserPreference;
import nz.ac.canterbury.seng302.portfolio.model.UserPreferenceRepository;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TableControllerFailTest {


    public AuthState invalidAuthState = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("ADMIN").build()) // Set the mock user's role
            .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
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

    private PaginatedUsersResponse paginatedUsersResponse = PaginatedUsersResponse.newBuilder()
            .addUsers(testUser)
            .build();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    NavController navController;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    AuthenticateClientService authenticateClientService;

    @MockBean
    UserPreferenceRepository userPreferenceRepo;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(TableController.class)
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

    String role = "STUDENT"; //testUser.getRoles()
    private UserPreference userPreference = new UserPreference(1, "", 0);
    int step = 50;
    int currentPage = 0;
    int start = currentPage * step;
    String sortCol = userPreference.getSortCol();
    int sortOrder = userPreference.getSortOrder();


    @Test
    public void getUserListWithInValidCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(invalidAuthState, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getRole(invalidAuthState)).thenReturn(role);
        utilities.when(() -> AuthStateInformer.getId(invalidAuthState)).thenReturn(1);
        when(accountClientService.getUserById(1)).thenReturn(testUser);
        when(userPreferenceRepo.findById(1)).thenReturn(userPreference);
        when(accountClientService.getPaginatedUsers(step, start, sortCol, sortOrder)).thenReturn(paginatedUsersResponse);

        mockMvc.perform(get("/user-list"))
                .andExpect(status().isForbidden()); // Whether to return the status "200 OK"
    }

}
