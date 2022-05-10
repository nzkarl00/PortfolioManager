package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.AccountPhotoService;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
public class AccountPhotoController {

    @Autowired
    private AccountClientService accountClientService;

    @Autowired
    private AccountPhotoService photoService;

    @GetMapping("/edit-photo")
    public String projectForm(Model model, @AuthenticationPrincipal AuthState principal) throws IOException {
        Integer id = AuthStateInformer.getId(principal);
        /* Add project details to the model */

        // UserResponse userReply = accountClientService.getUserById(id);
        // model.addAttribute("photo", userReply.getPhotoPath());

        UserResponse userReply = accountClientService.getUserById(id);

        NavController.updateModelForNav(principal, model, userReply, id);

        model.addAttribute("photo", "/images/" + id + "/" + id + ".jpg");
        return "editPhoto";
    }

    @PostMapping("/upload-photo")
    public String uploadPhoto(Model model,
                              @AuthenticationPrincipal AuthState principal,
                              @RequestParam(value="filename") MultipartFile file) throws IOException {
        int id = AuthStateInformer.getId(principal);
        photoService.uploadPhoto(id, file);
        return "redirect:edit-photo";
    }

    @RequestMapping(value="/delete-photo", method = RequestMethod.DELETE)
    public String deletePhoto(Model model,
                              @AuthenticationPrincipal AuthState principal) {
        int id = AuthStateInformer.getId(principal);
        accountClientService.deleteUserProfilePhoto(id);
        return "redirect:edit-photo";
    }
}
