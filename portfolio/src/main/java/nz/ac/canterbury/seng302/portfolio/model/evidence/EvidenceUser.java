package nz.ac.canterbury.seng302.portfolio.model.evidence;

import nz.ac.canterbury.seng302.portfolio.model.userGroups.User;

import javax.persistence.*;

@Entity
public class EvidenceUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", unique = true)
    protected int id;

    /**
     * This links a single user to an evidence user
     */
    @OneToOne
    @JoinColumn(name="parent_evidence_id", nullable=false)
    private Evidence parentEvidence;

    /**
     * This links a single evidence user to a piece of evidence
     */
    @ManyToOne
    @JoinColumn(name="parent_skill_tag_id", nullable = false)
    private SkillTag parentSkillTag;

    public EvidenceUser() {};

    /**
     * The main constructor with all the required details to add to the GroupMembership table
     * @param tag
     * @param evidence
     */
    public EvidenceUser(User User, Evidence evidence) {
        parentEvidence = evidence;
        parentAuthor = user;
    }

    public Evidence getParentEvidence() {
        return parentEvidence;
    }

    public SkillTag getParentSkillTag() { return parentSkillTag; }
}
