package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.util.FileSystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ProfilePhotoController {

    @Autowired
    AccountProfileRepository repo;

    @Autowired
    FileSystemUtils fsUtils;

    Logger logger = LoggerFactory.getLogger(ProfilePhotoController.class);

    @RequestMapping(value = "/image/{personId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPhoto(@PathVariable int personId) throws IOException {
        logger.info(String.format("[PHOTO] Received request to get photo for user ID: %d", personId));
        String photoRelPath = repo.findById(personId).getPhotoPath();
        logger.trace(String.format("[PHOTO] Resolved photo path for user ID: %d", personId));
        InputStream inputStream;
        if (photoRelPath.equals("DEFAULT")) {
            inputStream = new ClassPathResource("images/default_account_icon.jpeg").getInputStream();
        } else {
            Path photoAbsPath = fsUtils.resolveRelativeProfilePhotoPath(Paths.get(photoRelPath));
            inputStream = new FileInputStream(photoAbsPath.toFile());
        }

        byte[] bytes = StreamUtils.copyToByteArray(inputStream);

        logger.info(String.format("[PHOTO] Finished resolving photo for user ID: %d", personId));

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }
}
