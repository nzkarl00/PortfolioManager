package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditAccountController {
    /* Create default project. */

    @Autowired
    private AccountClientService accountClientService;

    String nickname = "Fabian"; //TODO these still need db implementation
    String password = "12345678";
    String passwordConfirm = "12345678";
    String firstname = "John";
    String lastname = "Fakename";
    String pronouns = "they/them";

    /**
     * Directs to the account edit, pulling user data to display
     * @param model The model to be used by the application for web integration
     * @return The html page to be used
     */
    @GetMapping("/edit-account")
    public String projectForm(Model model,
                              @AuthenticationPrincipal AuthState principal) {

        Integer id = AuthStateInformer.getId(principal);
        /* Add project details to the model */

        String username = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("name"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100");

        UserResponse userReply;
        userReply = accountClientService.getUserById(id);

        // Put the users details into the page
        model.addAttribute("date", DateParser.displayDate(userReply));
        model.addAttribute("username", userReply.getUsername());
        model.addAttribute("email", userReply.getEmail());
        model.addAttribute("bio", userReply.getBio());
        model.addAttribute("nickname", nickname);
        model.addAttribute("password", password);
        model.addAttribute("passwordConfirm", passwordConfirm);
        model.addAttribute("firstname", firstname);
        model.addAttribute("lastname", lastname);
        model.addAttribute("pronouns", pronouns);

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
        Integer id = AuthStateInformer.getId(principal);

        EditUserResponse response = accountClientService.editUser(id, firstname, "", lastname, nickname, bio, pronouns, email);
        System.out.println(response.getMessage());
        return "redirect:/account";
    }



}
