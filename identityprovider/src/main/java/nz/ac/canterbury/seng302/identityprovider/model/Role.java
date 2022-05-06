package nz.ac.canterbury.seng302.identityprovider.model;
import javax.persistence.*;
import java.beans.ConstructorProperties;

/**
 * The entity representation for the Role and roles table
 * it is linked to the account profile in a many-to-one relationship
 */
@Entity
@Table(name = "Roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userRoleId;
    @Column(name = "role")
    private String role;
    @ManyToOne
    @JoinColumn(name="parent_account_id", nullable=false)
    private AccountProfile registeredUser;

    public Role() {}

    /**
     * Basic Role constructor
     * @param registeredUser the account to associate the role with
     * @param role The string of the role to store, in the form {number}{role} to allow for easy sorting
     */
    public Role(AccountProfile registeredUser, String role) {
        this.registeredUser = registeredUser;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public Long getUserRoleId(){
        return userRoleId;
    }

    public void setUserRoleId(Long id) {
        userRoleId = id;
    }
}
