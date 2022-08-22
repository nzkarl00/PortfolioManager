package nz.ac.canterbury.seng302.portfolio.model.evidence;

import com.fasterxml.jackson.annotation.JsonBackReference;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static nz.ac.canterbury.seng302.portfolio.service.ValidateService.validateEnoughCharacters;

/**
 * An abstract class designed to provide a base for the three specific types of time bound items related to a project.
 * They are Milestones, Deadlines and Events.
 * The distinction between a start and end date is not neccesary for some classes (Milestone), yet is maintained
 * in the interface to ensure we can treat all three classes similarly in some application code.
 * Each concrete implementation is free to implement the methods as most appropriate.
 */
@Entity
public class Evidence {
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 2000;
    public static final int QUALITATIVE_SKILLS = 1; // 2^0
    public static final int QUANTITATIVE_SKILLS = 2; // 2^1
    public static final int SERVICE = 4; // 2^2
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", unique = true)
    protected int id;

    /**
     * The user to which the piece of evidence belongs.
     * This is the primary identifier for a collection of evidence.
     * Should not be modifiable.
     */
    @Column(name="parent_user_id", nullable=false)
    protected int parentUserId;

    /**
     * A list of all users for this piece of evidence
     * Creates a relationship with evidence and users, where evidence may be associated to many users
     * We cannot create a direct mapping as they are in different databases so this is a work around
     */
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "evidence_user_id")
    protected List<EvidenceUser> evidenceUsersId;

    /**
     * The project the piece of evidence belongs to.
     * Note that the parent user is the identifying property.
     */
    @ManyToOne
    @JoinColumn(name="associated_project_id", nullable=false)
    protected Project associatedProject;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name="evidence_tag_id")
    protected List<EvidenceTag> evidenceTags;

    @Column(name="title", length = MAX_TITLE_LENGTH, nullable = false)
    protected String title = "";
    @Column(name="description", length = MAX_DESCRIPTION_LENGTH, nullable = false)
    protected String description = "";
    @Column(name="date", nullable = false)
    protected LocalDate date;
    @Column(name="categories")
    protected int categories = 0;

    /**
     * The list of links associated with this peice of evidence
     */
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "parentEvidence", cascade = CascadeType.ALL)
    @JsonBackReference
    protected List<WebLink> links;

    public Evidence() {}

    /**
     * Construct a piece of evidence.
     * Precondition:
     * - Call validateProperties to verify properties are valid.
     * @param parentUserId The user ID (from idp) to which the piece of evidence belongs
     * @param associatedProject The project to which the piece of evidence is contained
     * @param title The title for the piece of evidence
     * @param description The description of the ProjectItem
     * @param date A single date relevant to the project item
     */
    public Evidence(
        int parentUserId,
        Project associatedProject,
        String title,
        String description,
        LocalDate date,
        int categories
    ) {
        this.parentUserId = parentUserId;
        this.evidenceUsersId = evidenceUsersId;
        this.associatedProject = associatedProject;
        this.title = title;
        this.description = description;
        this.date = date;
        this.categories = categories;
    }

    /**
     * Validate properties to store in a project item. Must be called before constructing an invalid project item.
     * @param parentProject The parent project that the piece of evidence is related to.
     * @param title The title of the piece of evidence
     * @param description The description of the piece of evidence
     * @param date The local date when that the piece of evidence is referenced to
     * @throws IllegalArgumentException If one argument is invalid, throws an exception
     */
    public static void validateProperties(Project parentProject, String title, String description, LocalDate date) throws IllegalArgumentException {
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(String.format("Title length must not exceed %d characters", MAX_TITLE_LENGTH));
        } else if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(String.format(
                    "Description length must not exceed %d characters",
                    MAX_DESCRIPTION_LENGTH
            ));
        } else if (date.isAfter(parentProject.getLocalEndDate())) {
            throw new IllegalArgumentException("Evidence date is after parent project end date");
        } else if (date.isBefore(parentProject.getLocalStartDate())) {
            throw new IllegalArgumentException("Evidence date is before parent project start date");
        }
        validateEnoughCharacters(title);
        validateEnoughCharacters(description);
    }

    /**
     * Validates that the new associated is valid to attach the evidence to.
     * This means checking the evidence still lies within the date range.
     * @param newProject The new project to potentially associate with the piece of Evidence
     */
    void newAssociatedProjectIsValid(Project newProject) throws IllegalArgumentException {
        if (date.isAfter(newProject.getLocalEndDate())) {
            throw new IllegalArgumentException("New project ends before evidence date");
        } else if (date.isBefore(newProject.getLocalStartDate())) {
            throw new IllegalArgumentException("New project starts after evidence date");
        }
    }

    /**
     * Validates that the new associated is valid to attach the evidence to.
     * This means checking the evidence still lies within the date range.
     * @param newDate The new project to potentially associate with the piece of Evidence
     */
    void newDateIsValid(LocalDate newDate) throws IllegalArgumentException {
        if (newDate.isAfter(associatedProject.getLocalEndDate())) {
            throw new IllegalArgumentException("New evidence date is after parent project end date");
        } else if (newDate.isBefore(associatedProject.getLocalStartDate())) {
            throw new IllegalArgumentException("New evidence date is before parent project start date");
        }
    }

    /**
     * Sets the associated project to which the piece of evidence is associated
     * @param associatedProject
     */
    public void setAssociatedProject(Project associatedProject) throws IllegalArgumentException {
        newAssociatedProjectIsValid(associatedProject);
        this.associatedProject = associatedProject;
    }

    /**
     * Set the title of the piece of evidence
     * @param title name
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the description of the evidence.
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the date of the piece of evidence.
     * @param date
     */
    public void setDate(LocalDate date) throws IllegalArgumentException {
        newDateIsValid(date);
        this.date = date;
    };

    /**
     * Get the ID of the piece of evidence
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Get the ID of the parent user to which the piece of evidence belongs
     * @return
     */
    public int getParentUserId() {
        return parentUserId;
    }

    /**
     * Get the associated project to which the piece of evidence is associated.
     * @return
     */
    public Project getAssociatedProject() {
        return associatedProject;
    }


    /**
     * Get the title
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the description
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the date
     * @return
     */
    public LocalDate getDate() {
        return date;
    };

    public List<EvidenceTag> getEvidenceTags() {
        return evidenceTags;
    }

    /**
     * Gets a list of all the links associated with this evidence
     * @return links
     */
    public List<WebLink> getLinks() {
        return links;
    }

    /**
     * Extracts which categories are present in the bit representation
     * @return A list of string representations of the categories on a given piece of evidence
     */
    public List<String> getCategoryStrings() {
        List<String> categoryStrings = new ArrayList<>();
        if ((categories & QUALITATIVE_SKILLS) > 0) { // Checks if category int has a 1 in the first position
            categoryStrings.add("Qualitative Skills");
        }
        if ((categories & QUANTITATIVE_SKILLS) > 0) { // Checks if category int has a 1 in the second position
            categoryStrings.add("Quantitative Skills");
        }
        if ((categories & SERVICE) > 0) { // Checks if category int has a 1 in the third position
            categoryStrings.add("Service");
        }
        return categoryStrings;
    }

    public static int categoryStringToInt(String categories) {
        int categoryInt = 0;
        if (categories.contains("Qualitative Skills")) {
            categoryInt += QUALITATIVE_SKILLS;
        }
        if (categories.contains("Quantitative Skills")) {
            categoryInt += QUANTITATIVE_SKILLS;
        }
        if (categories.contains("Service")) {
            categoryInt += SERVICE;
        }
        return categoryInt;
    }

    public void setCategories(int categories) {
        this.categories = categories;
    }
}
