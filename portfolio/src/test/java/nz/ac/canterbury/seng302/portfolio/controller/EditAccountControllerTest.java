package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
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

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.testUserAdmin;
import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.validAuthStateAdmin;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EditAccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class EditAccountControllerTest {

    private EditUserResponse editedUser = EditUserResponse.newBuilder()
            .setIsSuccess(true)
            .setMessage("Finished updating")
            .build();

    private EditUserResponse editedUserFalse = EditUserResponse.newBuilder()
            .setIsSuccess(false)
            .setMessage("update denied")
            .build();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    NavController navController;

    MockedStatic<AuthStateInformer> utilities;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(EditAccountController.class)
                .build();
    }

    @AfterEach
    public void closeStatics() {
        utilities.close();
    }

    @Test
    public void getAccountInfoWithValidCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        utilities = Mockito.mockStatic(AuthStateInformer.class);
        utilities.when(() -> AuthStateInformer.getId(validAuthStateAdmin)).thenReturn(1);
        when(accountClientService.getUserById(1)).thenReturn(testUserAdmin);

        // Expected values in the model

        String firstName = testUserAdmin.getFirstName();
        String lastName = testUserAdmin.getLastName();
        String nickname = testUserAdmin.getNickname();
        String email = testUserAdmin.getEmail();
        String bio = testUserAdmin.getBio();
        String pronouns = testUserAdmin.getPersonalPronouns();

        // Spring now thinks we are logged in as the user specified in validAuthState, so any request made from now on will be authenticated.
        // Ready to make a request to any endpoint which requires authentication
        mockMvc.perform(get("/edit-account"))
                .andExpect(status().isOk()) // Whether to return the status "200 OK"
                .andExpect(view().name("editAccount")) // Whether to return the template "account"
                //Model test.
                .andExpect(model().attribute("firstname", firstName))
                .andExpect(model().attribute("lastname", lastName))
                .andExpect(model().attribute("nickname", nickname))
                .andExpect(model().attribute("email", email))
                .andExpect(model().attribute("pronouns", pronouns))
                .andExpect(model().attribute("bio", bio));
    }

    @Test
    public void projectSaveTest() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        String firstName = "testUser.getFirstName()";
        String lastName = "testUser.getLastName()";
        String nickname = "testUser.getNickname()";
        String email = "testUser.getEmail()";
        String bio = "testUser.getBio()";
        String pronouns = "testUser.getPersonalPronouns()";

        utilities = Mockito.mockStatic(AuthStateInformer.class);
        utilities.when(() -> AuthStateInformer.getId(validAuthStateAdmin)).thenReturn(1);
        when(accountClientService.getUserById(1)).thenReturn(testUserAdmin);
        when(accountClientService.editUser(1, firstName, "", lastName, nickname, bio, pronouns, email)).thenReturn(editedUser);


       // accountClientService.editUser(AuthStateInformer.getId(validAuthState), firstName, "", lastName, nickname, bio,pronouns, email);
        mockMvc.perform(post("/edit-account")
                        .param("nickname", nickname)
                        .param("bio", bio)
                        .param("firstname",firstName)
                        .param("lastname", lastName)
                        .param("pronouns", pronouns)
                        .param("email", email)
                )

                .andExpect(status().is3xxRedirection()) // Whether to return the status "302 OK"
                .andExpect(view().name("redirect:edit-account"));

    }

}