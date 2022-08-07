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

@Controller
public class ProfilePhotoController {

    @Autowired
    AccountProfileRepository repo;

    @Autowired
    FileSystemUtils fsUtils;

    @RequestMapping(value = "/image/{personId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPhoto(@PathVariable int personId) throws IOException {
        String photoRelPath = repo.findById(personId).getPhotoPath();
        InputStream inputStream;
        if (photoRelPath.equals("DEFAULT")) {
            inputStream = new ClassPathResource("images/default_account_icon.jpeg").getInputStream();
        } else {
            Path photoAbsPath = fsUtils.resolveRelativeProfilePhotoPath(Paths.get(photoRelPath));
            inputStream = new FileInputStream(photoAbsPath.toFile());
        }

        byte[] bytes = StreamUtils.copyToByteArray(inputStream);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }
}
