package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.*;

/**
* This class stores information about the composite attribute that is a user's name associated with their account,
* This entity matches the Account_Name entity found in the schema.sql file
*/
@Entity
public class AccountName {

    //The person's ID associated with their account, this value is added automatically upon creation
    @Id
    private Long registeredUser;
    private String firstName;
    private String lastName;
    //A person's preferred alias, what they like to be called
    private String nickName;
    //A person's middle name(s), or other names that they go by
    //In order to keep things simple, even if a person has multiple middle names it is stored as just one string 
    private String otherMiddleName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private AccountProfile profile;

    protected AccountName(Long id, String firstName, String lastName, String otherMiddleName, String nickName) {
        registeredUser = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.otherMiddleName = otherMiddleName;
        this.nickName = nickName;
    }

    public Long getId() {
        return registeredUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getOtherMiddleName() {
        return otherMiddleName;
    }

    public String getNickName() {
        return nickName;
    }
}