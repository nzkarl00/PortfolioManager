package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditAccountController {
    /* Create default project. */

    @Autowired
    private AccountClientService accountClientService;

    @Autowired
    private NavController navController;

    private final String displayNone = "display:none;";
    String editErrorShow = displayNone;
    String editSuccessShow = displayNone;
    String editSuccessCode = "successCode";

    /**
     * Directs to the account edit, pulling user data to display
     * @param model The model to be used by the application for web integration
     * @return The html page to be used
     */
    @GetMapping("/edit-account")
    public String projectForm(Model model,
                              @AuthenticationPrincipal AuthState principal) throws IOException {

        Integer id = AuthStateInformer.getId(principal);
        /* Add project details to the model */

        UserResponse userReply;
        userReply = accountClientService.getUserById(id);

        // Put the users details into the page
        navController.updateModelForNav(principal, model, userReply, id);
        model.addAttribute("email", userReply.getEmail());
        model.addAttribute("bio", userReply.getBio());
        model.addAttribute("nickname", userReply.getNickname());
        model.addAttribute("firstname", userReply.getFirstName());
        model.addAttribute("lastname", userReply.getLastName());
        model.addAttribute("pronouns", userReply.getPersonalPronouns());
        model.addAttribute("editErrorShow", editErrorShow);
        model.addAttribute("editSuccessShow", editSuccessShow);
        model.addAttribute("editSuccessCode", editSuccessCode);

        editErrorShow = displayNone;
        editSuccessShow = displayNone;
        editSuccessCode = "successCode";

        /* Return the name of the Thymeleaf template */
        return "editAccount";
    }

    /**
     * the method responsible for sending the edit request to the server
     * @param principal auth token
     * @param nickname The nickname input (string)
     * @param bio The bio input (string)
     * @param firstname The first name input (string)
     * @param lastname The last name input (string)
     * @param pronouns The pronouns of the user input (string)
     * @param email The email of the user input (string)
     * @param model The model to be used by the application for web integration
     * @return redirects to the account page
     */
    @PostMapping("/edit-account")
    public String projectSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="nickname") String nickname,
            @RequestParam(value="bio") String bio,
            @RequestParam(value="firstname") String firstname,
            @RequestParam(value="lastname") String lastname,
            @RequestParam(value="pronouns") String pronouns,
            @RequestParam(value="email") String email,
            Model model
    ) {
        Integer id = AuthStateInformer.getId(principal);
        EditUserResponse editUserResponse = accountClientService.editUser(id, firstname, "", lastname, nickname, bio, pronouns, email);

        editSuccessCode = editUserResponse.getMessage();
        if (editUserResponse.getIsSuccess()) {
            editErrorShow = displayNone;
            editSuccessShow = "";
        } else {
            editErrorShow = "";
            editSuccessShow = displayNone;
        }

        return "redirect:edit-account";
    }
}
