package nz.ac.canterbury.seng302.portfolio.model.evidence;

import javax.persistence.*;
import java.util.List;

@Entity
public class EvidenceUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", unique = true)
    protected int id;

    /**
     * This links each EvidenceUser to many pieces of Evidence
     */
    @ManyToOne
    @JoinColumn(name="parent_evidence_id", nullable=false)
    private Evidence parentEvidence;

    @Column(name="user_id", nullable = false)
    protected Integer userid;

    @Column(name="username", nullable = false)
    protected String username;

    protected EvidenceUser() {

    }

    /**
     * build from userId and Evidence
     * @param userId the id of the user we want to represent
     * @param parentEvidence the piece of evidence we are linking to
     */
    public EvidenceUser(int userId, String username, Evidence parentEvidence) {
        this.username = username;
        this.parentEvidence = parentEvidence;
        this.userid = userId;
    }

    public Evidence getParentEvidence() {
        return parentEvidence;
    }

    public Integer getUserid() {
        return userid;
    }

}
