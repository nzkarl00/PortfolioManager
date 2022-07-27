package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    public static final DateTimeFormatter htmlDateFormat = DateTimeFormatter.ofPattern("YYYY-MM-dd");

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
     * The project the piece of evidence belongs to.
     * Note that the parent user is the identifying property.
     */
    @ManyToOne
    @JoinColumn(name="associated_project_id", nullable=false)
    protected Project associatedProject;

    @Column(name="title", length = MAX_TITLE_LENGTH, nullable = false)
    protected String title = "";
    @Column(name="description", length = MAX_DESCRIPTION_LENGTH, nullable = false)
    protected String description = "";
    @Column(name="date", nullable = false)
    protected LocalDate date;

    protected Evidence() {}

    /**
     * Construct a piece of evidence.
     * Precondition:
     * - Call validateProperties to verify properties are valid.
     * @param parentUser The user to which the piece of evidence belongs
     * @param associatedProject The project to which the piece of evidence is contained
     * @param title The title for the piece of evidence
     * @param description The description of the ProjectItem
     * @param startDate A single date relevant to the project item
     */
    public Evidence(
        int parentUserId,
        Project associatedProject,
        String title,
        String description,
        LocalDate date
    ) {
        this.parentUserId = parentUserId;
        this.associatedProject = associatedProject;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    /**
     * Validate properties to store in a project item. Must be called before constructing an invalid project item.
     * @param name The name of the ProjectItem
     * @param description The description of the ProjectItem
     * @throws IllegalArgumentException If one argument is invalid, throws an exception
     */
    static void validateProperties(Project parentProject, String title, String description, LocalDate date) throws IllegalArgumentException {
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
    }

    /**
     * Validates that the new associated is valid to attach the evidence to.
     * This means checking the evidence still lies within the date range.
     * @param The new project to potentially associate with the piece of Evidence
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
     * @param The new project to potentially associate with the piece of Evidence
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
     * @param name
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
}
