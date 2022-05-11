package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.RolesRepository;
import nz.ac.canterbury.seng302.identityprovider.service.Account;
import nz.ac.canterbury.seng302.identityprovider.util.FileSystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ProfilePhotoController {

    @Autowired
    Account accountService;

    @Autowired
    AccountProfileRepository repo;

    @Autowired
    RolesRepository roleRepo;

    @Autowired
    FileSystemUtils fsUtils;

    @RequestMapping("/image/{personId}")
    @ResponseBody
    public HttpEntity<byte[]> getPhoto(@PathVariable int personId) throws IOException {
        String photoRelPath = repo.findById(personId).getPhotoPath();
        Path photoAbsPath;
        if (photoRelPath.equals("DEFAULT")) {
            photoAbsPath = fsUtils.resourcesDirectory().resolve("images/default_account_icon.jpeg");
        } else {
            photoAbsPath = fsUtils.resolveRelativeProfilePhotoPath(Paths.get(photoRelPath));
        }
        byte[] bytes = Files.readAllBytes(photoAbsPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(bytes.length);
        return new HttpEntity<byte[]>(bytes, headers);
    }
}
