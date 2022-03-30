package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    @Autowired
    private AccountClientService accountClientService;

    @Autowired
    private GreeterClientService greeterClientService;

    /**
     * control the displaying of account details
     * @param principal the auth token
     * @param favouriteColour idk
     * @param model
     * @return string of where to go next
     */
    @GetMapping("/account")
    public String account(
        @AuthenticationPrincipal AuthState principal,
        @RequestParam(name="name", required=false, defaultValue="Blue") String favouriteColour,
        Model model
    ) {
        Integer id = AuthStateInformer.getId(principal);
        String role = AuthStateInformer.getRole(principal);

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id); // Get the user

        // Put the users details into the page
        model.addAttribute("date", DateParser.displayDate(userReply));
        model.addAttribute("username", userReply.getUsername());
        // End of Attributes for header
        model.addAttribute("email", userReply.getEmail());
        model.addAttribute("bio", userReply.getBio());

        // Generate our own message, based on the information we have available to us
        String portfolioMessage = String.format(
            "The portfolio service (which is serving you this message) knows you are logged in as '%s' (role='%s'), with ID=%d",
            principal.getName(),
            role,
            id
        );
        model.addAttribute("portfolioMessage", portfolioMessage);

        // Also pass on just the favourite colour value on its own to use
        model.addAttribute("currentFavouriteColour", favouriteColour);

        return "account";
    }
}
