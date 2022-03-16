package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
* This class specifies the attributes and methods associated with a user's account,
* This entity matches that found in the schema
*/
@Entity
public class AccountProfile {

    //Auto-generated ID is assigned to each persons account
    //Personal details associated with a users account
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String username;
    private String passwordHash;
    private String registerDate;
    private String bio;
    private String email;
    private String photoPath;

    //Necessary for Hibernate to work properly
    protected AccountProfile() {}
 
    //Constructor for a new profile
    public AccountProfile(String username, String passwordHash, String registerDate, String bio, String email, String photoPath) {
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

    public Boolean validatePassword(String givenPasswordHash) {
        return (passwordHash == givenPasswordHash);
    }

    @Override
    public String toString() {
        String AccountString = "Username: " + username + "\n";
        AccountString += "Date registered: " + registerDate + "\n";
        AccountString += "Personal biography: " + bio + "\n";
        AccountString += "Email: " + email + "\n";
        AccountString += "Path to photo: " + photoPath + "\n";
        return AccountString;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRegisterDate() {
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
}