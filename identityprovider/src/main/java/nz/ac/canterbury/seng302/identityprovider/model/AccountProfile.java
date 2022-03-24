package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.*;

/**
* This class specifies the attributes and methods associated with a user's account,
* This entity matches that found in the schema
*/
@Entity
@Table(name = "AccountProfile")
public class AccountProfile {

    //Auto-generated ID is assigned to each persons account
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    //Personal details associated with a users account
    private String username;
    @Column(name = "passwordHash")
    private String passwordHash;
    @Column(name = "registerDate")
    private String registerDate;
    @Column(name = "bio")
    private String bio;
    @Column(name = "email")
    private String email;
    @Column(name = "photoPath")
    private String photoPath;

    //Necessary for Hibernate to work properly
//    public AccountProfile() {}
 
    //Constructor for a new profile
    public AccountProfile(String username, String passwordHash, String registerDate, String bio, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.registerDate = registerDate;
        this.bio = bio;
        this.email = email;
        this.photoPath = "identityprovider/src/main/resources/images/default_account_icon.png"; //Path for default photo
    }

    /** Given the user's password from the client side, compare with the password stored in the database
    * @param givenPasswordHash The given password should be hashed before passing it into
    */
    public Boolean validatePassword(String givenPasswordHash) {
        return (passwordHash == givenPasswordHash);
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