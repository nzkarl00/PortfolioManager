package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Responsible for the login page and the calls to idp to login
 */
@Controller
public class LoginController {

    @Autowired
    private AuthenticateClientService authenticateClientService;

    Logger logger = LoggerFactory.getLogger(LoginController.class);

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

    /**
     * Attempts to authenticate with the Identity Provider via gRPC.
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Message generated by IdP about authenticate attempt
     */
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

        AuthenticateResponse authenticateResponse = authenticateLogin(username, password, model);
        logger.trace("[LOGIN] Authenticate Response received" + authenticateResponse);

        if (authenticateResponse == null) {
            return "redirect:login";
        }

        if (authenticateResponse.getSuccess()) {
            setCookie(request, response, authenticateResponse);
            return "redirect:account";
        }

        model.addAttribute("loginMessage", authenticateResponse.getMessage());
        return "login";
    }

    /**
     * This function authenticate login with a username and password, and return the authentication response.
     * Null will be returned if authenticating the login was unsuccessful.
     * @param username to login
     * @param password to login
     * @param model to display feedback to user
     */
    public AuthenticateResponse authenticateLogin(String username, String password, Model model) {
        logger.trace("[LOGIN] Attempting to authenticate user: " + username.replaceAll("[\n\r\t]", "_"));
        try {
            return authenticateClientService.authenticate(username, password);
        } catch (StatusRuntimeException e){
            model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
            logger.warn("[LOGIN] Failed authenticating user: " + username);
            return null;
        }
    }

    /**
     * A len-session-token will be set in the domain according to a successful authentication login/response.
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param authenticateResponse will contain the response details after a user has been authenticated to login
     */
    public void setCookie(HttpServletRequest request, HttpServletResponse response, AuthenticateResponse authenticateResponse) {

        var domain = request.getHeader("host");
        CookieUtil.create(
                response,
                "lens-session-token", // cookie in loginReply.getToken() stored here
                authenticateResponse.getToken(),
                true,
                5 * 60 * 60, // Expires in 5 hours
                domain.startsWith("localhost") ? null : domain
        );
        logger.info("[LOGIN] Cookie has been set for user: " + authenticateResponse.getUsername());
    }
}
