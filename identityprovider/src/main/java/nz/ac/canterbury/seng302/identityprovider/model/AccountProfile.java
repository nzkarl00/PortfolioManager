package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.*;

/**
* This class specifies the attributes and methods associated with a user's account,
* This entity matches the Account_Profile entity found in the schema.sql file
*/
@Entity
public class AccountProfile {

    //Auto-generated ID is assigned to each persons account
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    //Personal details associated with a users account
    private String username;
    private String passwordHash;
    private String registerDate;
    private String bio = "";
    private String email = "";
    private String photoPath;

    //This is a one to one relationship, each account has one set of information about their name
    @OneToOne(fetch = FetchType.EAGER)
    //Registered user refers to user's id, the user's id is automatically added upon creation of their 
    @JoinColumn(name = "registeredUser")
    private AccountName name;
 
    //Constructor for a new profile
    public AccountProfile(String username, String passwordHash, String registerDate, String bio, String email,
                            String firstName, String lastName, String nickName, String otherMiddleName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.registerDate = registerDate;
        this.bio = bio;
        this.email = email;
        this.photoPath = "identityprovider/src/main/resources/images/default_account_icon.png"; //Path for default photo
        name = new AccountName(id, firstName, lastName, nickName, otherMiddleName);
    }

    /** Given the user's password from the client side, compare with the password stored in the database
    * @param givenPasswordHash The given password should be hashed before passing it into this function
    */
    public Boolean validatePassword(String givenPasswordHash) {
        return (passwordHash == givenPasswordHash);
    }

    //A pretty way to see the user's personal information, for logging purposes
    public String profileToString() {
        String accountString = "Username: " + username + "\n";
        accountString += "Date registered: " + registerDate + "\n";
        accountString += "Personal biography: " + bio + "\n";
        accountString += "Email: " + email + "\n";
        accountString += "Path to photo: " + photoPath;
        return accountString;
    }

    //An easy way to get the person's name as a block of a single string, for logging purposes
    public String nameToString() {
        String nameString = "First name: " + name.getFirstName() + "\n";
        nameString += "Last name: " + name.getLastName() + "\n";
        nameString += "Nickname / alias: " + name.getNickName() + "\n";
        nameString += "Any other or middle names: " + name.getOtherMiddleName() + "\n";
        return nameString;
    }

    @Override
    //Gets a person's full information
    public String toString() {
        String fullString = profileToString() + "\n";
        fullString += nameToString();
        return fullString;
    }

    public void setPhotoPath(String newPhotoPath) {
        photoPath = newPhotoPath;
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