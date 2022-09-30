package nz.ac.canterbury.seng302.portfolio.model.evidence;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity()
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"parent_evidence_id", "parent_user_id"})})
public class HighFive {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", unique = true)
    private int id;

    /**
     * A high five can be associated with one piece of evidence.
     * An evidence can have one or more high five
     */
    @ManyToOne
    @JsonBackReference // This prevents infinite reference looping between tables
    @JoinColumn(name="parent_evidence_id", nullable=false)
    protected Evidence parentEvidence;

    /**
     * The user who placed the high hive on a piece of evidence.
     */
    @Column(name="parent_user_id", nullable=false)
    protected int parentUserId;

    protected String firstName;
    protected String lastName;

    public HighFive() {}

    /**
     * A high five on a parent evidence
     * @param parentEvidence that this high five is associated with
     * @param userId that made the high five
     * @param firstName of the user
     * @param lastName of the user
     */
    public HighFive(Evidence parentEvidence, int userId, String firstName, String lastName) {
        this.parentEvidence = parentEvidence;
        this.parentUserId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getId() {
        return id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Evidence getParentEvidence() {
        return parentEvidence;
    }

    public int getParentUserId() {
        return parentUserId;
    }
}
