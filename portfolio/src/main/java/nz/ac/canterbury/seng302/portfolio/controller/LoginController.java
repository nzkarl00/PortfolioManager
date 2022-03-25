package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.regex.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private AuthenticateClientService authenticateClientService;

    /**
     * Attempts to authenticate with the Identity Provider via gRPC.
     *
     * This process works in a few stages:
     *  1.  We send the username and password to the IdP
     *  2.  We check the response, and if it is successful we add a cookie to the HTTP response so that
     *      the client's browser will store it to be used for future authentication with this service.
     *  3.  We return the thymeleaf login template with the 'message' given by the identity provider,
     *      this message will be something along the lines of "Logged in successfully!",
     *      "Bad username or password", etc.
     *
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param username Username of account to log in to IdP with
     * @param password Password associated with username
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Message generated by IdP about authenticate attempt
     */

    @GetMapping("/")
    public String empty(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        model.addAttribute("loginMessage", "");
        return "login";
    }

    @GetMapping("/login")
    public String login(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        model.addAttribute("loginMessage", "");
        return "login";
    }

    /**
     * Attempts to create a valid authorisation attempt
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param username Username of account to log in to IdP with
     * @param password Password associated with username
     * @param model The model to be used by the application for web integration
     * @return redirects to the account or login page based on if the correct details were submitted
     */
    @PostMapping("/login")
    public String signin(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value="username") String username,
            @RequestParam(value="password") String password,
            Model model
    ) {
        AuthenticateResponse loginReply;
        try {
            loginReply = authenticateClientService.authenticate(username, password);
        } catch (StatusRuntimeException e){
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            return "login";
        }
        if (loginReply.getSuccess()) {
            var domain = request.getHeader("host");
            CookieUtil.create(
                response,
                "lens-session-token",
                    loginReply.getToken(),
                true,
                5 * 60 * 60, // Expires in 5 hours
                domain.startsWith("localhost") ? null : domain
            );
            return "account";
        }

        model.addAttribute("loginMessage", loginReply.getMessage());
        return "login";
    }

    /**
     * Redirects to the signup page
     * @param model The model to be used by the application for web integration
     * @return signup page reference
     */
    @GetMapping("/signup")
    public String signup(
        Model model
    ) {
        model.addAttribute("testData", "null");
        return "signup";
    }

    /**
     * Attempts to register the user if all information is valid
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param username Username of account to log in to IdP with
     * @param password Password associated with username
     * @param passwordConfirm Password inputted again to ensure user validity
     * @param firstname User first name
     * @param lastname User last name
     * @param pronouns User pronouns
     * @param email User email
     * @param model The model to be used by the application for web integration
     * @return redirects to the signup page
     */
    @PostMapping("/signup")
    public String createAccount(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value="username") String username,
            @RequestParam(value="password") String password,
            @RequestParam(value="passwordConfirm") String passwordConfirm,
            @RequestParam(value="firstname") String firstname,
            @RequestParam(value="lastname") String lastname,
            @RequestParam(value="pronouns") String pronouns,
            @RequestParam(value="email") String email,
            Model model
            )
    {
        System.out.println("TESTMESSAGE");
        UserRegisterResponse registerReply;
        registerReply = authenticateClientService.register(username, password, firstname, lastname, email, pronouns);
        model.addAttribute("registerTest", registerReply.getMessage());
        return "signup";
    }
}
