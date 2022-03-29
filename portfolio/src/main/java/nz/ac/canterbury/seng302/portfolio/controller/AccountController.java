package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
public class AccountController {

    @Autowired
    private AccountClientService accountClientService;

    @Autowired
    private GreeterClientService greeterClientService;

    @GetMapping("/account")
    public String account(
        @AuthenticationPrincipal AuthState principal,
        @RequestParam(name="name", required=false, defaultValue="Blue") String favouriteColour,
        Model model
    ) {
        // Talk to the GreeterService on the IdP to get a message, we'll tell them our favourite colour too

        Integer id = Integer.valueOf(principal.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("nameid"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse("-100"));

        String username = principal.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("name"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse("-100");

        UserResponse userReply;
        userReply = accountClientService.getUserById(id);
        Long seconds = userReply.getCreated().getSeconds();
        Date date = new Date(seconds * 1000); // turn into millis
        SimpleDateFormat dateFormat = new SimpleDateFormat( "dd LLLL yyyy" );
        String stringDate = dateFormat.format( date );
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        Calendar currentCalendar = Calendar.getInstance();
        cal.setTime(new Date());
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        int totalMonth = (currentMonth - month) + 12 * (currentYear - year);
        if (totalMonth > 0){
            if (totalMonth > 1) {
                stringDate += " (" + totalMonth + " Months)";
            } else {
                stringDate += " (" + totalMonth + " Month)";
            }
        }

        String roles = "";
        for (UserRole role : userReply.getRolesList()) {
            roles += role.toString() + ", ";
        }
        roles = roles.substring(0, roles.length() - 2);

        String name = userReply.getFirstName() + " " +  userReply.getLastName();
        model.addAttribute("roles", roles);
        model.addAttribute("pronouns", userReply.getPersonalPronouns());
        model.addAttribute("name",  name);
        model.addAttribute("nickname",  userReply.getNickname());
        model.addAttribute("date",  stringDate);
        model.addAttribute("username", userReply.getUsername());
        model.addAttribute("email", userReply.getEmail());
        model.addAttribute("bio", userReply.getBio());

        // Generate our own message, based on the information we have available to us
        String portfolioMessage = String.format(
            "The portfolio service (which is serving you this message) knows you are logged in as '%s' (role='%s'), with ID=%d",
            principal.getName(),
            roles,
            id
        );
        model.addAttribute("portfolioMessage", portfolioMessage);

        // Also pass on just the favourite colour value on its own to use
        model.addAttribute("currentFavouriteColour", favouriteColour);

        return "account";
    }
}
