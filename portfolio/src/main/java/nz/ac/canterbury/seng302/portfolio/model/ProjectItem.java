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
public abstract class ProjectItem {
    public static final int MAX_NAME_LENGTH = 60;
    public static final int MAX_DESCRIPTION_LENGTH = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="name", length = MAX_NAME_LENGTH)
    private String name = "";
    @Column(name="description", length = MAX_DESCRIPTION_LENGTH)
    private String description = "";
    protected LocalDateTime startDate;
    @ManyToOne
    @JoinColumn(name="parent_project_id", nullable=false)
    private Project parentProject;

    protected ProjectItem() {}

    /**
     * Construct a project item.
     * Precondition:
     * - Call validateProperties to verify parameters are valid.
     * @param name  The name of the ProjectItem
     * @param description The description of the ProjectItem
     * @param startDate A single date relevant to the project item
     */
    public ProjectItem(Project parentProject, String name, String description, LocalDateTime startDate) {
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

    void setName(String name) {
        this.name = name;
    }
    void setDescription(String description) {
        this.description = description;
    }

    abstract void setStartDate(LocalDateTime startDate);

    abstract void setEndDate(LocalDateTime endDate);

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    abstract LocalDateTime getStartDate();

    abstract LocalDateTime getEndDate();
}
