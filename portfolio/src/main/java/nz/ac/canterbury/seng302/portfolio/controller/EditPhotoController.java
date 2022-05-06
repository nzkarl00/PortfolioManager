package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;

@Controller
public class EditPhotoController {

    @Autowired
    private AccountClientService accountClientService;

    @GetMapping("/edit-photo")
    public String projectForm(Model model, @AuthenticationPrincipal AuthState principal) {
        Integer id = AuthStateInformer.getId(principal);
        /* Add project details to the model */

        UserResponse userReply;
        userReply = accountClientService.getUserById(id);
        model.addAttribute("photo", userReply.getProfileImagePath());
        return "editPhoto";
    }

    @PostMapping("/upload-photo")
    public String uploadPhoto(Model model,
                              @AuthenticationPrincipal AuthState principal,
                              @RequestParam(value="photo") File photo) {
        int id = AuthStateInformer.getId(principal);
        accountClientService.uploadPhoto(id, photo.toString(), photo);
        return "redirect:/editPhoto";
    }
}
