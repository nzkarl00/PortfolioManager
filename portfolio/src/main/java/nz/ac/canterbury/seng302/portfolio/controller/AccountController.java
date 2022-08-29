package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Responsible for the account details page
 */
@Controller
public class AccountController {

    @Autowired
    private AccountClientService accountClientService;

    @Autowired
    private NavController navController;

    /**
     * control the displaying of account details
     * @param principal the auth token
     * @param model the model to add attributes to, to template from
     * @return string of where to go next
     */
    @GetMapping("/account")
    public String account(
        @AuthenticationPrincipal AuthState principal,
        @RequestParam(value = "id") Optional<Integer> userId,
        Model model
    ) {
        int id = userId.orElse(AuthStateInformer.getId(principal));

        // Attributes For header
        UserResponse userReply;
        try {
            userReply = accountClientService.getUserById(id); // Get the user
        } catch(Exception e) {
            userReply = accountClientService.getUserById(AuthStateInformer.getId(principal)); // Get the user
            id = AuthStateInformer.getId(principal);
        }


        model.addAttribute("isSelf",id == AuthStateInformer.getId(principal));

        // Put the users details into the page
        String roles = "";
        int index = 0;
        for (UserRole role : userReply.getRolesList()) {
            if (index == 0) {
                roles += role.toString();
            } else {
                roles += ", " + role.toString();
            }
            index++;
        }

        navController.updateModelForNav(principal, model, userReply, id);

        String name = userReply.getFirstName() + " " +  userReply.getLastName();
        model.addAttribute("roles", roles);
        model.addAttribute("userId", userReply.getId());
        model.addAttribute("pronouns", userReply.getPersonalPronouns());
        model.addAttribute("name",  name);
        model.addAttribute("nickname",  userReply.getNickname());
        // End of Attributes for header
        model.addAttribute("email", userReply.getEmail());
        model.addAttribute("bio", userReply.getBio());

        return "account";
    }

    @GetMapping("/evidenceList")
    public String getAccountEvidence(@AuthenticationPrincipal AuthState principal,
                                     Model model) {
        Integer user_id = AuthStateInformer.getId(principal);
        return "redirect:evidence?ui=" + String.valueOf(user_id);

    }
}
