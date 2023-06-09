package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.UserTemplate;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.User;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SignupController {

    @Autowired
    private AccountClientService accountClientService;

    @Autowired
    private LoginController loginController;

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    String errorShow = "display:none;";
    String successShow = "display:none;";
    String successCode = "successCode";

    /**
     * Redirects to the signup page
     * @param model The model to be used by the application for web integration
     * @return signup page reference
     */
    @GetMapping("/signup")
    public String signup(
        Model model
    ) {
        model.addAttribute("userForm", new UserTemplate());
        model.addAttribute("testData", "null");
        model.addAttribute("errorShow", errorShow);
        model.addAttribute("successShow", successShow);
        model.addAttribute("successCode", successCode);

        errorShow = "display:none;";
        successShow = "display:none;";
        successCode = "successCode";

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
        @ModelAttribute("userForm") UserTemplate user,
        BindingResult result,
        Model model
    )
    {
        UserRegisterResponse registerReply;
        registerReply = accountClientService.register(username, password, firstname, lastname, pronouns, email);
        successCode = registerReply.getMessage();
        logger.debug(successCode);
        if (!successCode.contains("Created account")) {
            result.addError(new ObjectError("globalError", successCode));
        }

        // Tries to auto authenticate a login after signing up
        AuthenticateResponse authenticateResponse = loginController.authenticateLogin(username, password, model);
        logger.trace("[LOGIN] Authenticate Response received");
        if (result.hasErrors()) {
            return "signup";
        }
        if (authenticateResponse == null) {
            return "redirect:signup";
        }
        // If authenticating a login is successful, then the cookie will be set in the domain.
        if (authenticateResponse.getSuccess()) {
            loginController.setCookie(request, response, authenticateResponse);
            return "redirect:account";
        }

        model.addAttribute("loginMessage", authenticateResponse.getMessage());
        logger.info("[SIGNUP] Signup successful for user: " + username.replaceAll("[\n\r\t]", "_"));
        return "redirect:signup";
    }
}
