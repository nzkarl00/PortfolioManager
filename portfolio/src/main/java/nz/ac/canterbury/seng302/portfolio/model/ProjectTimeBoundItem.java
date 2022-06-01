package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * An abstract class designed to provide a base for the three specific types of time bound items related to a project.
 * They are Milestones, Deadlines and Events.
 * The distinction between a start and end date is not neccesary for some classes (Milestone), yet is maintained
 * in the interface to ensure we can treat all three classes similarly in some application code.
 * Each concrete implementation is free to implement the methods as most appropriate.
 */
@Entity
public abstract class ProjectTimeBoundItem {
    public static final int MAX_NAME_LENGTH = 60;
    public static final int MAX_DESCRIPTION_LENGTH = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="name", length = MAX_NAME_LENGTH)
    protected String name = "";
    @Column(name="description", length = MAX_DESCRIPTION_LENGTH)
    private String description = "";
    protected LocalDateTime startDate;
    @ManyToOne
    @JoinColumn(name="parent_project_id", nullable=false)
    private Project parentProject;

    protected ProjectTimeBoundItem() {}

    /**
     * Construct a project time bound item.
     * Precondition:
     * - Call validateProperties to verify parameters are valid.
     * @param parentProject the parent project to which the item belongs.
     * @param name  The name of the ProjectItem
     * @param description The description of the ProjectItem
     * @param startDate A single date relevant to the project item
     */
    public ProjectTimeBoundItem(Project parentProject, String name, String description, LocalDateTime startDate) {
        this.parentProject = parentProject;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
    }

    /**
     * Validate properties to store in a project item. Must be called before constructing an invalid project item.
     * @param name The name of the ProjectItem
     * @param description The description of the ProjectItem
     * @throws IllegalArgumentException If one argument is invalid, throws an exception
     */
    static void validateProperties(String name, String description) throws IllegalArgumentException {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(String.format("Name length must not exceed %d characters", MAX_NAME_LENGTH));
        } else if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(String.format(
                "Description length must not exceed %d characters",
                MAX_DESCRIPTION_LENGTH
            ));
        }
    }

    /**
     * Set the name of the item
     * @param name
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Set the description of the item.
     * @param description
     */
    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the start date of the item.
     * For some instances this may be equivalent to setting the end date.
     * @param startDate
     */
    abstract void setStartDate(LocalDateTime startDate);

    /**
     * Set the end date of the item.
     * For some instances this may be equivalent to setting the start date.
     * @param endDate
     */
    abstract void setEndDate(LocalDateTime endDate);

    /**
     * Get the item name
     * @return
     */
    String getName() {
        return name;
    }

    /**
     * Get the item description
     * @return
     */
    String getDescription() {
        return description;
    }

    /**
     * Get the start date
     * @return
     */
    abstract LocalDateTime getStartDate();

    /**
     * Get the end date.
     * @return
     */
    abstract LocalDateTime getEndDate();
}
