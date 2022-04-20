package nz.ac.canterbury.seng302.identityprovider.model;
import javax.persistence.*;
import java.beans.ConstructorProperties;

@Entity
@Table(name = "Roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userRoleId;
    @Column(name = "role")
    private String role;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="parent_account_id", nullable=false)
    private AccountProfile registeredUser;

    public Role() {}

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
}
