package nz.ac.canterbury.seng302.portfolio.model.evidence;

import nz.ac.canterbury.seng302.portfolio.model.Project;

import javax.persistence.*;
import java.util.List;

@Entity
public class SkillTag {
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 2000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", unique = true)
    protected int id;

    /**
     * There can be many SkillTags for a parent Project
     */
    @ManyToOne
    @JoinColumn(name="parent_project_id", nullable=false)
    protected Project parentProject;

    /**
     * There can be many Evidences with many SkillTags
     */
    @OneToMany
    @JoinColumn(name="evidence_id")
    protected List<EvidenceTag> evidenceTags;

    @Column(name="title", length = MAX_TITLE_LENGTH, nullable = false)
    protected String title = "";

    @Column(name="description", length = MAX_DESCRIPTION_LENGTH, nullable = false)
    protected String description = "";

    protected SkillTag(){};

    public SkillTag(Project parentProject, String title) {
        this.parentProject = parentProject;
        this.title = title;
    }

    public List<EvidenceTag> getEvidenceTags() {
        return evidenceTags;
    }

    public int getId() {
        return id;
    }

    public Project getParentProject() {
        return parentProject;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}