package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.userGroups.UserPreference;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.UserPreferenceRepository;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.invalidAuthState;
import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.testUserStudent;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TableController.class)
public class TableControllerFailTest {

    private PaginatedUsersResponse paginatedUsersResponse = PaginatedUsersResponse.newBuilder()
            .addUsers(testUserStudent)
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
        when(accountClientService.getUserById(1)).thenReturn(testUserStudent);
        when(userPreferenceRepo.findById(1)).thenReturn(userPreference);
        when(accountClientService.getPaginatedUsers(step, start, sortCol, sortOrder)).thenReturn(paginatedUsersResponse);

        mockMvc.perform(get("/user-list"))
                .andExpect(status().isForbidden()); // Whether to return the status "200 OK"
    }

}
