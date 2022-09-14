package nz.ac.canterbury.seng302.portfolio.model.evidence;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class EvidenceTag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", unique = true)
    protected int id;

    /**
     * This links a single EvidenceTag to a piece of Evidence
     */
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="parent_evidence_id", nullable=false)
    protected Evidence parentEvidence;

    /**
     * This links a single EvidenceTag to a SkillTag
     */
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="parent_skill_tag_id", nullable = false)
    private SkillTag parentSkillTag;

    public EvidenceTag() {};

    /**
     * The main constructor with all the required details to add to the GroupMembership table
     * @param tag
     * @param evidence
     */
    public EvidenceTag(SkillTag tag, Evidence evidence) {
        parentEvidence = evidence;
        parentSkillTag = tag;
    }

    /**
     * Get the ID of the evidence tag
     * @return
     */
    public int getId() {return id; }

    public Evidence getParentEvidence() {
        return parentEvidence;
    }

    public SkillTag getParentSkillTag() { return parentSkillTag; }
}
