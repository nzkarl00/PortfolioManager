package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Note this isn't exactly a controller but something that will alloow all controllers to feed the correct data to the nav
 * by updating the model
 */
public class NavController {
    public static void updateModelForNav(AuthState principal, Model model, UserResponse userReply, int id) throws IOException {
        String photoString = "data:image/jpeg;base64,";
        Path path = Paths.get(userReply.getProfileImagePath());
        //Path path = Paths.get("E:/IdeaProjects/team-700/identityprovider/user-images/1/1.jpeg");
        byte[] bytes = Files.readAllBytes(path);
        bytes = Base64Utils.encode(bytes);
        photoString += new String(bytes);
        model.addAttribute("photo", photoString);
        model.addAttribute("username", userReply.getUsername());
        model.addAttribute("date", DateParser.displayDate(userReply));
    }
}
