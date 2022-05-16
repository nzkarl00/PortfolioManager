package nz.ac.canterbury.seng302.identityprovider.util;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

public class FileSystemUtils {

    @Value("${identityprovider.user-content-directory}")
    String userContentDirectory;

    @Value("classpath:images")
    Resource imagesDir;

    private FileSystemUtils instance;

    public FileSystemUtils() {

    }

    public FileSystemUtils getInstance() {
        if (instance != null) {
            instance = new FileSystemUtils();
        }
        return instance;
    }

    /**
     * Get the absolute path to the resources directory
     * @return The absolute path
     */
    public Path resourcesDirectory() {
        System.out.println("resources dir");
        try {
            System.out.println(Paths.get(imagesDir.getFile().getAbsolutePath()).toString());
            return Paths.get(imagesDir.getFile().getAbsolutePath());
        } catch (Exception e) {
            System.out.println(e);
        }
        return Paths.get(System.getProperty("user.dir")).resolve("src/main/resources");
    }

    /**
     * Resolve the directory in which user profile images should be stored.
     * @return A path to the directory in which user content should be stored
     */
    public Path userContentDirectory() {
        String projectDir = System.getProperty("user.dir");
        System.out.println(userContentDirectory);
        if (userContentDirectory.startsWith(".")) {
            return Paths.get(projectDir).resolve(userContentDirectory);
        } else {
            return Paths.get(userContentDirectory);
        }
    }

    /**
     * Resolve an absolute path to the file in which a user's profile photo is stored.
     * @param userId The user's ID
     * @param fileExtension The file extension, without leading '.'
     * @return An absolute path to the user's profile photo
     */
    public Path userProfilePhotoAbsolutePath(Integer userId, String fileExtension) {
        return userContentDirectory().resolve(userProfilePhotoRelativePath(userId, fileExtension));
    }

    /**
     * Resolve a relative path to the file in which a user's profile photo is stored.
     * Path is relative to the userContentDirectory
     * @param userId The user's ID
     * @param fileExtension The file extension, without leading '.'
     * @return An absolute path to the user's profile photo
     */
    public Path userProfilePhotoRelativePath(Integer userId, String fileExtension) {
        return Paths.get("user-images").resolve(userId + "." + fileExtension);
    }

    /**
     * Resolve the relative path of a user profile photo to an absolute path
     * @param relativePath The relative path from database
     * @return The absolute path
     */
    public Path resolveRelativeProfilePhotoPath(Path relativePath) {
        return userContentDirectory().resolve(relativePath);
    }

}
