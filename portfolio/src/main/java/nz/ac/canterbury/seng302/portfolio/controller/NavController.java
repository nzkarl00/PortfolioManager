package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import java.util.Date;

/**
 * Note this isn't exactly a controller but something that will allow all controllers to feed the correct data to the nav
 * by updating the model
 */
public class NavController {
    @Value("${portfolio.idp-url-prefix}")
    String idpLocation;

    /**
     * injects the values we want into the nav portion of the model
     * @param principal The authstate to validate what a user can access, not yet used, but is here as it might be
     * @param model the model to inject attributes into
     * @param userReply the user's grpc response to use to inject values for said user
     * @param id user id
     */
    public void updateModelForNav(AuthState principal, Model model, UserResponse userReply, int id) {
        String request = idpLocation + "/image/" + id;
        model.addAttribute("photo", request);
        model.addAttribute("username", userReply.getUsername());
        model.addAttribute("date", DateParser.displayDate(userReply, new Date()));
    }
}
