package nz.ac.canterbury.seng302.portfolio.controller;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;

import com.google.protobuf.Timestamp;
import com.google.rpc.context.AttributeContext;
import nz.ac.canterbury.seng302.portfolio.authentication.AuthenticationClientInterceptor;
import nz.ac.canterbury.seng302.portfolio.authentication.JwtAuthenticationFilter;
import nz.ac.canterbury.seng302.portfolio.authentication.JwtAuthenticationToken;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EditAccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class EditAccountControllerTest {
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

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(EditAccountController.class)
                .build();
    }

    @Test
    public void getAccountInfoWithValidCredentials() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        MockedStatic<AuthStateInformer> utilities = Mockito.mockStatic(AuthStateInformer.class);
        utilities.when(() -> AuthStateInformer.getId(validAuthState)).thenReturn(1);
        when(accountClientService.getUserById(1)).thenReturn(testUser);

        // Expected values in the model

        String firstName = testUser.getFirstName();
        String lastName = testUser.getLastName();
        String nickname = testUser.getNickname();
        String email = testUser.getEmail();
        String bio = testUser.getBio();
        String pronouns = testUser.getPersonalPronouns();

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
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState, ""));

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        String firstName = "testUser.getFirstName()";
        String lastName = "testUser.getLastName()";
        String nickname = "testUser.getNickname()";
        String email = "testUser.getEmail()";
        String bio = "testUser.getBio()";
        String pronouns = "testUser.getPersonalPronouns()";

        MockedStatic<AuthStateInformer> utilities = Mockito.mockStatic(AuthStateInformer.class);
        utilities.when(() -> AuthStateInformer.getId(validAuthState)).thenReturn(1);
        when(accountClientService.getUserById(1)).thenReturn(testUser);
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