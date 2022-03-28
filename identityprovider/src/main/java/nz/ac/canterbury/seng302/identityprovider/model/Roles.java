package nz.ac.canterbury.seng302.identityprovider.model;
import javax.persistence.*;
import java.beans.ConstructorProperties;

@Entity
@Table(name = "Roles")
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userRoleId")
    private Long userRoleId;
    @Column(name = "registeredUser")
    private Long registeredUser;
    @Column(name = "roles")
    private String role;



    public Roles() {}

    public Roles(Long registeredUser, String student) {
        this.registeredUser = registeredUser;
        this.role = student;
    }
}
