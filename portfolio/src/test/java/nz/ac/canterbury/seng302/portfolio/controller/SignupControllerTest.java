package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = SignupController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SignupControllerTest {

    private int userID = 1;
    private String username = "username";
    private String password = "password";
    private String firstname = "firstname";
    private String lastname = "lastname";
    private String pronouns = "";
    private String email = "email@email.email";

    String successCode = "userRegisterResponse Successful";

    UserRegisterResponse userRegisterResponse = UserRegisterResponse.newBuilder()
            .setIsSuccess(true)
            .setNewUserId(userID)
            .setMessage(successCode)
            .build();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    NavController navController;

    @MockBean
    LoginController loginController;

    @MockBean
    AuthenticateClientService authenticateClientService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(SignupController.class).build();
    }

    @Test
    public void signupGetMapping() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("signup"))
                .andExpect(MockMvcResultMatchers.model().attribute("successCode", successCode));
    }

    @Test
    public void signupBlueSky() throws Exception {
        when(accountClientService.register(username, password, firstname, lastname, pronouns, email))
                .thenReturn(userRegisterResponse);

        mockMvc.perform(post("/signup")
            .param("username", username)
            .param("password", password)
            .param("passwordConfirm", password)
            .param("firstname", firstname)
            .param("lastname", lastname)
            .param("pronouns", pronouns)
            .param("email", email)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:signup"));
    }
}
