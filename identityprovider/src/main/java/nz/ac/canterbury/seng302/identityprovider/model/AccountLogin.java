package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AccountLogin {
    @Id
    private Integer registereduser;
    private Integer hashPassword;

    private AccountLogin() {}

    protected AccountLogin(Integer registereduser, Integer hashPassword) {
        this.registereduser = registereduser;
        this.hashPassword = hashPassword;
    }

    protected Integer getpasswordhash() {
        return hashPassword;
    }
    
}
