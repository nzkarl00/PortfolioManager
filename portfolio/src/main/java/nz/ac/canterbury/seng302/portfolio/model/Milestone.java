package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Milestone.
 * Represents a point in time (Date & Time) at which something is due.
 * The date concept of ProjectItem class, are treated as the same thing, only one date exists.
 */
@Entity
@Table(name="milestone")
public class Milestone extends ProjectTimeBoundItem {
    protected Milestone() {}

    /**
     * Construct a new Milestone.
     * Parameters must be valid for the given project context before construction.
     * Preconditions:
     * - Validate parameters with the validateProperties method.
     * @param parentProject
     * @param name
     * @param description
     * @param date
     */
    public Milestone(Project parentProject, String name, String description, LocalDateTime date) {
        super(parentProject, name, description, date);
    }

    /**
     * Calls the parent class to check to validate properties
     * @param name of the milestone
     * @param description description
     */
    public static void validateProperties(String name, String description) throws IllegalArgumentException {
        ProjectTimeBoundItem.validateProperties(name, description);
    }

    /**
     * Set the start date of the Milestone.
     * This actually updates the only internal date as there is no concept of a start and end date of a milestone.
     * @param startDate The date time of the milestone
     */
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * Set the end date of the Milestone.
     * This actually updates the only internal date as there is no logical concept of two dates in a milestone.
     * @param startDate the date time of the milestone.
     */
    public void setEndDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * Get the start of the milestone.
     * Same date time as the end of the milestone.
     * @return
     */
    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    /**
     * Get the end of the milestone.
     * Same date time as the end of the milestone.
     * @return
     */
    public LocalDateTime getEndDate() {
        return this.startDate;
    }

    /**
     * Get the name of the milestone.
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the id of the milestone.
     * @return
     */
    public Integer getId() {
        return this.id;
    }
}
