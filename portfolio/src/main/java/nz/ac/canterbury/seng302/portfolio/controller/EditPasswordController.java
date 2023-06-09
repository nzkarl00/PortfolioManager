package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ChangePasswordResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for the edit password page
 */
@Controller
public class EditPasswordController {
    /* Create default project. */

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private AccountClientService accountClientService;

    @Autowired
    private NavController navController;

    private String password = "";
    private String passwordConfirm = "";
    private String passwordErrorShow = "display:none;";
    private String passwordSuccessShow = "display:none;";
    private String passwordSuccessCode = "successCode";

    /**
     * Directs to the password edit page, adding attributes to display
     * @param model The model to be used by the application for web integration
     * @return The html page to be used
     */
    @GetMapping("/edit-password")
    public String passwordForm(Model model,
                              @AuthenticationPrincipal AuthState principal) {

        int id = AuthStateInformer.getId(principal);
        UserResponse userReply = accountClientService.getUserById(id);

        navController.updateModelForNav(principal, model, userReply, id);

        model.addAttribute("password", password);
        model.addAttribute("passwordConfirm", passwordConfirm);
        model.addAttribute("passwordErrorShow", passwordErrorShow);
        model.addAttribute("passwordSuccessShow", passwordSuccessShow);
        model.addAttribute("passwordSuccessCode", passwordSuccessCode);

        passwordErrorShow = "display:none;";
        passwordSuccessShow = "display:none;";
        passwordSuccessCode = "successCode";

        return "editPassword";
    }

    /**
     * the method responsible for sending the edit password request to the server
     * @param principal auth token
     * @param newPassword The password input (string password)
     * @param currentPassword the existing password to confirm user validity (string password)
     * @param passwordConfirm The password second input (string password)
     * @param model The model to be used by the application for web integration
     * @return redirects to the account page
     */
    @PostMapping("/edit-password")
    public String passwordSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="newPassword") String newPassword,
            @RequestParam(value="currentPassword") String currentPassword,
            @RequestParam(value="passwordConfirm") String passwordConfirm,
            Model model
    ) {
        Integer id = AuthStateInformer.getId(principal);
        ChangePasswordResponse changePasswordResponse = accountClientService.editPassword(id, currentPassword, newPassword);

        passwordSuccessCode = changePasswordResponse.getMessage();
        logger.debug("[YIYANG HERE]" + changePasswordResponse);
        if (!changePasswordResponse.getIsSuccess()) {
            passwordErrorShow = "";
            passwordSuccessShow = "display:none;";
        } else {
            passwordErrorShow = "display:none;";
            passwordSuccessShow = "";
        }

        return "redirect:edit-password";
    }
}
