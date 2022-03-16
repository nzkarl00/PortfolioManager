package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
* This class specifies the attributes and methods associated with a user's account,
* This entity matches that found in the schema
*/
@Entity
public class AccountProfile {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer idnumber;
    private String accountname;
    private String registerdate;
    private String bio;
    private String email;
    private String photopath;

    protected AccountProfile() {}

    public AccountProfile(String accountname, String registerdate, String bio, String email, String photopath) {
        this.accountname = accountname;
        this.registerdate = registerdate;
        this.bio = bio;
        this.email = email;
        if(photopath != null) {
            this.photopath = photopath;
        } else {
            this.photopath = ""; //Path for default photo
        }
        //new AccountLogin(idnumber, hashPassword);
    }

    public String toString() {
        String AccountString = "Username: " + accountname + "\n";
        AccountString += "Date registered: " + registerdate + "\n";
        AccountString += "Personal biography: " + bio + "\n";
        AccountString += "Email: " + email + "\n";
        AccountString += "Path to photo: " + photopath + "\n";
        //AccountString += "Hashed password: " + login.getpasswordhash() + "\n";
        return AccountString;
    }

    public Integer getidnumber() {
        return idnumber;
    }

    public String getaccountname() {
        return accountname;
    }

    public String getregisterdate() {
        return registerdate;
    }

    public String getbio() {
        return bio;
    }

    public String getemail() {
        return email;
    }
    //Add photopath here
    public String getphotopath() {
        return photopath;
    }

    /*public Boolean validatePassword(int givenHashPassword) {
        return (login.getpasswordhash() == givenHashPassword);
    }*/

}