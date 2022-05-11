package nz.ac.canterbury.seng302.identityprovider.model;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class AccountProfileTest {

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

        // actual account string
        String actualAccountString = "Username: " + actual.getUsername() + "\n";
        actualAccountString += "Date registered: " + actual.getRegisterDate() + "\n";
        actualAccountString += "Personal biography: " + actual.getBio() + "\n";
        actualAccountString += "Email: " + actual.getEmail() + "\n";
        actualAccountString += "Path to photo: " + actual.getPhotoPath();

        assertEquals(expectedAccountString, actualAccountString);
    }


}