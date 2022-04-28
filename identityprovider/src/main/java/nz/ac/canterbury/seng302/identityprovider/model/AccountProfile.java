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
    private int id;
    //Personal details associated with a users account
    @Column(name = "username", length = 30)
    private String username;
    @Column(name = "password_hash", length = 60)
    private String passwordHash;
    @Column(name = "register_date")
    private Date registerDate;
    @Column(name = "bio", length = 1024)
    private String bio;
    @Column(name = "email", length = 30)
    private String email;
    @Column(name = "photo_path", length = 100)
    private String photoPath;
    @Column(name = "first_name", length = 30)
    private String firstName;
    @Column(name = "last_name", length = 30)
    private String lastName;
    @Column(name = "middle_name", length = 30)
    private String middleName;
    @Column(name = "nickname", length = 30)
    private String nickname;
    @Column(name = "pronouns", length = 10)
    private String pronouns;
    @OneToMany(mappedBy = "registeredUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Role> roles;

    //Necessary for Hibernate to work properly
    public AccountProfile() {}

    /**
     * The main constructor with all the required details
     * @param username users alias
     * @param passwordHash hashed password with salt
     * @param registerDate the dateTime the user registered
     * @param bio the users self-description
     * @param email the users unique email
     * @param photoPath the file path for the users photo
     * @param firstName first name
     * @param lastName last name
     * @param pronouns users preferred pronouns
     * @param token if a user is currently logged in, they will be set a unique token from the domain's cookie
     */
    public AccountProfile(String username, String passwordHash, Date registerDate, String bio, String email, String photoPath, String firstName, String lastName, String pronouns, String token) {
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

    /**
     * a toString to make logging and debugging easier
     * @return the string representation of the AccountProfile
     */
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

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public String getPronouns() {
        return pronouns;
    }

    public String getNickname() {
        return nickname;
    }

    public void setID(int newId) {
        this.id = newId;
    } // TODO should this be editable, is it not just a primary key?

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
