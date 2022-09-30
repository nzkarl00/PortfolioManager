package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.ChangePasswordResponse;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.testUserStudent;
import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.validAuthStateStudent;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EditPasswordController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EditPasswordTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AuthenticateClientService authenticateClientService;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    NavController navController;

    private ChangePasswordResponse changePasswordResponse = ChangePasswordResponse.newBuilder()
        .setIsSuccess(true)
        .setMessage("Password changed")
        .build();

    String newPassword = "newPassword";
    String currentPassword = "currentPassword";
    String passwordConfirm = "newPassword";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(EditPasswordController.class)
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
    public void getEditPasswordWithValidCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getId(validAuthStateStudent)).thenReturn(1);
        when(accountClientService.getUserById(1)).thenReturn(testUserStudent);

        mockMvc.perform(get("/edit-password"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(MockMvcResultMatchers.view().name("editPassword")) // Whether to return the template "account"
                .andExpect(MockMvcResultMatchers.model().attribute("password", ""))
                .andExpect(MockMvcResultMatchers.model().attribute("passwordConfirm", ""))
                .andExpect(MockMvcResultMatchers.model().attribute("passwordSuccessCode", "successCode"));
    }

    @Test
    public void postEditPasswordWithValidCredentials() throws Exception {

        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities.when(() -> AuthStateInformer.getId(validAuthStateStudent)).thenReturn(1);
        when(accountClientService.editPassword(1, currentPassword, newPassword)).thenReturn(changePasswordResponse);

        mockMvc.perform(post("/edit-password")
                .param("newPassword", newPassword)
                .param("currentPassword", currentPassword)
                .param("passwordConfirm",passwordConfirm)
            )
                .andExpect(status().is3xxRedirection()) // Whether to return the status "302 OK"
                .andExpect(MockMvcResultMatchers.view().name("redirect:edit-password"));
    }
}
