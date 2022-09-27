package nz.ac.canterbury.seng302.portfolio.model.evidence;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

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

    public HighFive() {}

    /**
     * A high five on a parent evidence
     * @param parentEvidence that this high five is associated with
     * @param parentUserId that made the high five
     */
    public HighFive(
            Evidence parentEvidence,
            int parentUserId
    ) {
        this.parentEvidence = parentEvidence;
        this.parentUserId = parentUserId;
    }

    public int getId() {
        return id;
    }

    public Evidence getParentEvidence() {
        return parentEvidence;
    }

    public int getParentUserId() {
        return parentUserId;
    }
}
