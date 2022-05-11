package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.authentication.AuthenticationClientInterceptor;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
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
import org.springframework.web.servlet.HandlerInterceptor;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

    public AuthState validAuthState = AuthState.newBuilder()
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

    private ChangePasswordResponse changePasswordResponse = ChangePasswordResponse.newBuilder()
        .setIsSuccess(true)
        .setMessage("Password changed successfully")
        .build();

    String newPassword = "newPassword";
    String currentPassword = "currentPassword";
    String passwordConfirm = "newPassword";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(EditPasswordController.class)
            .build();
    }

    @Test
    public void getEditPasswordWithValidCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        MockedStatic<AuthStateInformer> utilities = Mockito.mockStatic(AuthStateInformer.class);
        utilities.when(() -> AuthStateInformer.getId(validAuthState)).thenReturn(1);
        when(accountClientService.getUserById(1)).thenReturn(testUser);

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
            .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        MockedStatic<AuthStateInformer> utilities = Mockito.mockStatic(AuthStateInformer.class);
        utilities.when(() -> AuthStateInformer.getId(validAuthState)).thenReturn(1);
        when(accountClientService.editPassword(1, currentPassword, newPassword)).thenReturn(changePasswordResponse);

        mockMvc.perform(post("/edit-password")
                .param("newPassword", newPassword)
                .param("currentPassword", currentPassword)
                .param("passwordConfirm",passwordConfirm)
            )
                .andExpect(status().is3xxRedirection()) // Whether to return the status "302 OK"
                .andExpect(MockMvcResultMatchers.view().name("redirect:/edit-password"));
    }
}
