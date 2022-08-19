package nz.ac.canterbury.seng302.portfolio.model.evidence;

import javax.persistence.*;

@Entity
public class EvidenceUser {

    /**
     * This links each EvidenceUser to many pieces of Evidence
     */
    @OneToMany
    @JoinColumn(name="parent_evidence_id", nullable=false)
    private List<Evidence> allEvidences;


    /**
     * The main constructor with all the required details to add to the GroupMembership table
     * @param tag
     * @param evidence
     */
    public EvidenceUser(String username, int evidenceUserId) {
        this.username = username;
        this.evidenceUserId = evidenceUserId;
    }

    public Evidence getParentEvidence() {
        return parentEvidence;
    }

}
