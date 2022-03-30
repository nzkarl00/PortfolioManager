package nz.ac.canterbury.seng302.identityprovider.model;
import javax.persistence.*;
import java.beans.ConstructorProperties;

@Entity
@Table(name = "Roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ROLE_ID")
    private Long userRoleId;
    @Column(name = "user_role")
    private String role;
    @ManyToOne(fetch = FetchType.EAGER)
    private AccountProfile registeredUser;



    public Role() {}

    public Role(AccountProfile registeredUser, String role) {
        this.registeredUser = registeredUser;
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
