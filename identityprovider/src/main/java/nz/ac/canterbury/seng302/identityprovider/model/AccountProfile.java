package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.*;
import java.util.Date;

/**
* This class specifies the attributes and methods associated with a user's account,
* This entity matches that found in the schema
*/
@Entity
@Table(name = "AccountProfile")
public class AccountProfile {

    //Auto-generated ID is assigned to each persons account
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    //Personal details associated with a users account
    @Column(name = "username", length = 30)
    private String username;
    @Column(name = "passwordHash", length = 60)
    private String passwordHash;
    @Column(name = "registerDate")
    private Date registerDate;
    @Column(name = "bio", length = 1024)
    private String bio;
    @Column(name = "email", length = 30)
    private String email;
    @Column(name = "photoPath", length = 100)
    private String photoPath;

    //Necessary for Hibernate to work properly
    public AccountProfile() {}

    //Constructor for a new profile
    public AccountProfile(String username, String passwordHash, Date registerDate, String bio, String email, String photoPath) {
//        this.id = null;
        this.username = username;
        this.passwordHash = passwordHash;
        this.registerDate = registerDate;
        this.bio = bio;
        this.email = email;

        if(photoPath != null) {
            this.photoPath = photoPath;
        } else {
            this.photoPath = "identityprovider/src/main/resources/images/default_account_icon.png"; //Path for default photo
        }
    }

    //A pretty way to see the user's personal information, for logging purposes
    @Override
    public String toString() {
        String AccountString = "Username: " + username + "\n";
        AccountString += "Date registered: " + registerDate + "\n";
        AccountString += "Personal biography: " + bio + "\n";
        AccountString += "Email: " + email + "\n";
        AccountString += "Path to photo: " + photoPath;
        return AccountString;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public String getBio() {
        return bio;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setID(int newId) {
        this.id = newId;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }
}
