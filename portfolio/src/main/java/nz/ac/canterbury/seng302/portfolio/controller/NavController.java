package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.ui.Model;

/**
 * Note this isn't exactly a controller but something that will alloow all controllers to feed the correct data to the nav
 * by updating the model
 */
public class NavController {
    public static void updateModelForNav(AuthState principal, Model model, UserResponse userReply, int id) {
        model.addAttribute("photo", "/images/" + id + "/" + id + ".jpg");
        model.addAttribute("username", userReply.getUsername());
        model.addAttribute("date", DateParser.displayDate(userReply));
    }
}
