package nz.ac.canterbury.seng302.identityprovider.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AccountProfileTest {

    private static AccountProfile testAccountProfile = new AccountProfile("test username",
        "test hash", new Date(), "test bio", "test email",
        "test/photopath/", "firstname", "lastname", "pronouns");

    private static Role testRole = new Role(testAccountProfile, "1student");

    /**
     * use the constructor with many variables to create the accountProfile
     */
    @Test
    void accountProfileTest() {

        // create variables to be passed to the constructor
        String expectedUsername = "test username";
        String expectedPasswordHash = "password!21212ws";
        Long seconds = new Date().getTime();
        Date expectedRegisterDate = new Date(seconds);
        String expectedBio = "This is me";
        String expectedEmail = "test@test.com";
        String expectedFirstName = "first";
        String expectedLastName = "last";
        String expectedPronouns = "pronoun";

        // make account instance for testing
        AccountProfile actual = new AccountProfile(expectedUsername, expectedPasswordHash, expectedRegisterDate, expectedBio, expectedEmail, null, expectedFirstName, expectedLastName, expectedPronouns);

        assertEquals(expectedUsername, actual.getUsername());
        assertEquals(expectedPasswordHash, actual.getPasswordHash());
        assertEquals(expectedBio, actual.getBio());
        assertEquals(expectedRegisterDate, actual.getRegisterDate());
        assertEquals(expectedBio, actual.getBio());
        assertEquals(expectedEmail, actual.getEmail());
        assertEquals(expectedFirstName, actual.getFirstName());
        assertEquals(expectedLastName, actual.getLastName());
        assertEquals(expectedPronouns, actual.getPronouns());
    }

    /**
     * test the toString method
     */
    @Test
    void toStringTest() {
        // create variables to be passed to the constructor for actual
        Long seconds = new Date().getTime();
        Date expectedRegisterDate = new Date(seconds);;

        // make account instance for testing
        AccountProfile actual = new AccountProfile("user", "", expectedRegisterDate, "testing bio", "test1@test.com", null, "", "", "");


        // expected account string
        String expectedAccountString = "Username: " + "user" + "\n";
        expectedAccountString += "Date registered: " + actual.getRegisterDate() + "\n";
        expectedAccountString += "Personal biography: " + "testing bio" + "\n";
        expectedAccountString += "Email: " + "test1@test.com" + "\n";
        expectedAccountString += "Path to photo: " + "DEFAULT";

        assertEquals(expectedAccountString, actual.toString());
    }

    /**
     * test the get highest role with just student role
     */
    @Test
    void getHighestRole_blueSky() {
        testAccountProfile.addRoleTestingOnly(testRole);
        assertEquals(testRole, testAccountProfile.getHighestRole());
        assertEquals(testRole, testAccountProfile.getRoles().get(0));
    }

    /**
     * test the get highest role with teacher
     */
    @Test
    void getHighestRole_teacher() {
        Role teacherRole = new Role(testAccountProfile, "2teacher");
        testAccountProfile.addRoleTestingOnly(teacherRole);
        assertEquals(teacherRole, testAccountProfile.getHighestRole());
        assertEquals(teacherRole, testAccountProfile.getRoles().get(0));
    }

    /**
     * test the get highest role returning admin
     */
    @Test
    void getHighestRole_admin() {
        Role adminRole = new Role(testAccountProfile, "3admin");
        testAccountProfile.addRoleTestingOnly(adminRole);
        assertEquals(adminRole, testAccountProfile.getHighestRole());
        assertEquals(adminRole, testAccountProfile.getRoles().get(0));
    }

    /**
     * make a new profile with no roles, and assert the null return
     */
    @Test
    void getHighestRole_failNoRoles() {
        // create variables to be passed to the constructor for actual
        Long seconds = new Date().getTime();
        Date expectedRegisterDate = new Date(seconds);;

        AccountProfile actual = new AccountProfile("user", "", expectedRegisterDate, "testing bio", "test1@test.com", null, "", "", "");

        assertNull(actual.getHighestRole());
    }

    /**
     * test the empty constructor and then making the accountProfile with the setters
     */
    @Test
    void emptyConstructorTest_blueSky() {
        AccountProfile actual = new AccountProfile();

        // create variables to be set
        String expectedUsername = "test username";
        String expectedPasswordHash = "password!21212ws";
        Long seconds = new Date().getTime();
        Date expectedRegisterDate = new Date(seconds);
        String expectedBio = "This is me";
        String expectedEmail = "test@test.com";
        String expectedFirstName = "first";
        String expectedMiddleName = "middle";
        String expectedLastName = "last";
        String expectedPronouns = "pronoun";
        String expectedNickname = "nickName";
        String expectedPhotoPath = "testPath";

        actual.setUsername(expectedUsername);
        actual.setPasswordHash(expectedPasswordHash);
        actual.setRegisterDate(expectedRegisterDate);
        actual.setBio(expectedBio);
        actual.setEmail(expectedEmail);
        actual.setFirstName(expectedFirstName);
        actual.setMiddleName(expectedMiddleName);
        actual.setLastName(expectedLastName);
        actual.setPronouns(expectedPronouns);
        actual.setNickname(expectedNickname);
        actual.setPhotoPath(expectedPhotoPath);

        assertEquals(expectedUsername, actual.getUsername());
        assertEquals(expectedPasswordHash, actual.getPasswordHash());
        assertEquals(expectedNickname, actual.getNickname());
        assertEquals(expectedBio, actual.getBio());
        assertEquals(expectedRegisterDate, actual.getRegisterDate());
        assertEquals(expectedBio, actual.getBio());
        assertEquals(expectedEmail, actual.getEmail());
        assertEquals(expectedFirstName, actual.getFirstName());
        assertEquals(expectedMiddleName, actual.getMiddleName());
        assertEquals(expectedLastName, actual.getLastName());
        assertEquals(expectedPronouns, actual.getPronouns());
        assertEquals(expectedPhotoPath, actual.getPhotoPath());
    }
}