package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.testUserStudent;
import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.validAuthStateStudent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AuthenticateClientService authenticateClientService;

    @MockBean
    NavController navController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(LoginController.class).build();
    }

//    @AfterAll
//    public static void close() {
//        utilities.close();
//    }

    /**
     * Testing if the login page shows
     * @throws Exception
     */
    @Test
    public void loginMessageIsEmptyOnNavigation() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("loginMessage"))
                .andExpect(MockMvcResultMatchers.model().attribute("loginMessage", ""));
    }

    /**
     * Testing if the default page takes you to the login page
     * @throws Exception
     */
    @Test
    public void loginWhenDefaultPageLoads() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("login"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("loginMessage"))
            .andExpect(MockMvcResultMatchers.model().attribute("loginMessage", ""));
    }

    /**
     * Testing when a failed login attempt will redirect users back to the login page to attempt again
     * @throws Exception
     */
    @Test
    public void loginRedirectionWhenFailAuthentication() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "invalid_username")
                .param("password", "invalid_password")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.view().name("redirect:login"))
            .andExpect(redirectedUrl("login"));

    }

//    /**
//     * Testing when a successful login attempt will redirect users to their account page
//     * @throws Exception
//     */
//    @Test
//    public void accountRedirectionWhenSuccessfulAuthenticationLogin() throws Exception {
//        mockMvc.perform(post("/login")
//                .param("username", valid_username)
//                .param("password", valid_password)
//            )
//            .andExpect(status().is3xxRedirection())
//            .andExpect(MockMvcResultMatchers.view().name("redirect:account"))
//            .andExpect(redirectedUrl("account"));
//
//    }

//    @Test
//    public void loginHasSuccessMessageAfterValidLogin() throws Exception {
//        mockMvc.perform(post("/login"))
//            .andExpect(status().isOk())
//            .andExpect(MockMvcResultMatchers.model().attributeExists("loginMessage"));
//    }
//
//    @Test
//    public void setCookieValid() throws Exception {
//
//        // Put the cookie in the request
//
//        final String cookieValue = “some value”;
//
//        // authenticate login meessage,
//        // response from above^^
//
//        MvcResult result = mockMvc
//            .perform(post("/login")
//                .param("username", username)
//                .param("password", password)
//            )
//            .andExpect(status().isOk())
//            .andExpect(MockMvcResultMatchers.model().attributeExists("loginMessage"))
//            .cookie(invalidSessionCookie)
//            .param(“configEntity”, “configEntity”));
//
//        assertThat(<getCookieFromRequest>,
//            is(
//                CookieUtil.create(
//                response,
//                "lens-session-token",
//                authenticateResponse.getToken(),
//                true,
//                5*60*60,
//                domain.startsWith("localhost") ? null : domain
//                )
//            )
//        );
//
//    }

}
