package nz.ac.canterbury.seng302.identityprovider.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemUtilsTest {

    FileSystemUtils utils = new FileSystemUtils();

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(utils, "userContentDirectory",
            "./user-content");
    }

    /**
     * test to see if the content directory gets a child directory of the working directory
     */
    @Test
    void userContentDirectoryTest() {
        Path actual = utils.userContentDirectory();
        String projectDir = System.getProperty("user.dir");
        Path expectedParent = Paths.get(projectDir);
        assertTrue(actual.startsWith(expectedParent));
    }

    /**
     * test to see if the content directory gets a child directory of the working directory
     */
    @Test
    void userProfileAbsolutePathTest() {
        Path actual = utils.userProfilePhotoAbsolutePath(1, "jpg");
        Path expectedParent =
            utils.userContentDirectory().resolve(Paths.get("user-images"));
        assertTrue(actual.startsWith(expectedParent));
    }

    /**
     * test to see if the content directory gets a child directory of the working directory
     */
    @Test
    void userProfileRelativePathTest() {
        Path actual = utils.userProfilePhotoAbsolutePath(1, "jpg");
        String projectDir = System.getProperty("user.dir");
        Path notExpectedParent =
            Paths.get(projectDir).resolve(Paths.get("user-images"));
        assertFalse(actual.startsWith(notExpectedParent));
    }

    @Test
    void resolveRelativeProfilePhotoPath_blueSky() {
        Path expected = Paths.get(System.getProperty("user.dir") + "/./user-content/timmy");
        Path passIn = Paths.get("timmy");
        Path actual = utils.resolveRelativeProfilePhotoPath(passIn);
        assertEquals(expected, actual);
    }
}