package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class EditPhotoController {

    @Autowired
    private AccountClientService accountClientService;

    @Autowired
    private UploadService uploadService;

    @GetMapping("/edit-photo")
    public String projectForm(Model model, @AuthenticationPrincipal AuthState principal) {
        Integer id = AuthStateInformer.getId(principal);
        /* Add project details to the model */

        // UserResponse userReply = accountClientService.getUserById(id);
        // model.addAttribute("photo", userReply.getPhotoPath());
        model.addAttribute("photo", "/images/" + id + "/Cat03.jpg");
        model.addAttribute("message", "");
        return "editPhoto";
    }

    @PostMapping("/upload-photo")
    public String uploadPhoto(Model model,
                              @AuthenticationPrincipal AuthState principal,
                              @RequestParam(value="filename") MultipartFile file) throws IOException {
        int id = AuthStateInformer.getId(principal);
        uploadService.uploadPhoto(id, file);
        return "redirect:/edit-photo";
    }
}
