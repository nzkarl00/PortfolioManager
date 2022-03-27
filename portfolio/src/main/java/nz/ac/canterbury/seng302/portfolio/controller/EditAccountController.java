package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditAccountController {
    /* Create default project. TODO: use database to check for this*/

    String username = "GiveMeAnA+";
    String nickname = "Fabian";
    String password = "12345678";
    String passwordConfirm = "12345678";
    String bio = "Hey my name is john and I'm here to say, I like seng302 in a major way.";
    String firstname = "John";
    String lastname = "Fakename";
    String pronouns = "they/them";
    String email = "fakeemail@joke.co.nz";

    /**
     * Directs to the account edit, pulling user data to display
     * @param model The model to be used by the application for web integration
     * @return The html page to be used
     */
    @GetMapping("/edit-account")
    public String projectForm(Model model) {
        /* Add project details to the model */
        model.addAttribute("nickname", nickname);
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        model.addAttribute("passwordConfirm", passwordConfirm);
        model.addAttribute("bio", bio);
        model.addAttribute("firstname", firstname);
        model.addAttribute("lastname", lastname);
        model.addAttribute("pronouns", pronouns);
        model.addAttribute("email", email);

        /* Return the name of the Thymeleaf template */
        return "editAccount";
    }

    /**
     *
     * @param principal
     * @param nickname The nickname input (string)
     * @param password The password input (string password)
     * @param passwordConfirm The password second input (string password)
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
            @RequestParam(value="password") String password,
            @RequestParam(value="passwordConfirm") String passwordConfirm,
            @RequestParam(value="bio") String bio,
            @RequestParam(value="firstname") String firstname,
            @RequestParam(value="lastname") String lastname,
            @RequestParam(value="pronouns") String pronouns,
            @RequestParam(value="email") String email,
            Model model
    ) {
        // Here we would set the values anew
        return "redirect:/account";
    }



}
