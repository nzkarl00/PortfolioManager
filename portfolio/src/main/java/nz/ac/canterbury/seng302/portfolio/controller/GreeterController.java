package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.Date;

@Controller
public class GreeterController {

    @Autowired
    private AccountClientService accountClientService;

    @Autowired
    private GreeterClientService greeterClientService;


    @GetMapping("/greeting")
    public String greeting(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="name", required=false, defaultValue="Blue") String favouriteColour,
            Model model
    ) {
        // Talk to the GreeterService on the IdP to get a message, we'll tell them our favourite colour too
        String idpMessage = greeterClientService.receiveGreeting(favouriteColour);
        model.addAttribute("idpMessage", idpMessage);

        String role = AuthStateInformer.getRole(principal);

        Integer id = AuthStateInformer.getId(principal);

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

        return "greeting";
    }

    @PostMapping("/favouriteColour")
    public String favouriteColour(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="favouriteColour") String favouriteColour,
            Model model
    ) {
        return "redirect:/greeting?name=" + favouriteColour;
    }
}
