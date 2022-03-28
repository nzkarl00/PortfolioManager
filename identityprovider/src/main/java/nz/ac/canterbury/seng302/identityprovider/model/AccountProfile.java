package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
* This class specifies the attributes and methods associated with a user's account,
* This entity matches that found in the schema
*/
@Entity
@Table(name = "AccountProfile")
public class AccountProfile {

    //Auto-generated ID is assigned to each persons account
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
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
    @Column(name = "firstName", length = 30)
    private String firstName;
    @Column(name = "lastName", length = 30)
    private String lastName;
    @Column(name = "middleName", length = 30)
    private String middleName;
    @Column(name = "nickname", length = 30)
    private String nickname;
    @Column(name = "pronouns", length = 10)
    private String pronouns;
    @OneToMany(mappedBy = "registeredUser", cascade = CascadeType.ALL)
    private List<Roles> roles;


    //Necessary for Hibernate to work properly
    public AccountProfile() {}

    //Constructor for a new profile
    public AccountProfile(String username, String passwordHash, Date registerDate, String bio, String email, String photoPath, String firstName, String lastName, String pronouns) {
//        this.id = null;
        this.username = username;
        this.passwordHash = passwordHash;
        this.registerDate = registerDate;
        this.bio = bio;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = "";
        this.nickname = "";
        this.pronouns = pronouns;
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

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
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

    public void setID(Long newId) {
        this.id = newId;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }
}
