package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Deadline.
 * Represents a point in time (Date & Time) at which something is due.
 * The startDate and endDate concept of ProjectItem class, are treated as the same thing, only one date exists.
 */
@Entity
@Table(name="deadline")
public class Deadline extends ProjectTimeBoundItem {
    protected Deadline() {}

    /**
     * Construct a new deadline.
     * Parameters must be valid for the given project context before construction.
     * Preconditions:
     * - Validate parameters with the validateProperties method.
     * @param parentProject
     * @param name
     * @param description
     * @param startDate
     */
    public Deadline(Project parentProject, String name, String description, LocalDateTime startDate) {
        super(parentProject, name, description, startDate);
    }

    public static void validateProperties(String name, String description) throws IllegalArgumentException {
        ProjectTimeBoundItem.validateProperties(name, description);
    }

    /**
     * Set the start date of the deadline.
     * This actually updates the only internal date as there is no concept of a start and end date of a deadline.
     * @param startDate The date time of the deadline
     */
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * Set the end date of the deadline.
     * This actually updates the only internal date as there is no logical concept of two dates in a deadline.
     * @param startDate the date time of the deadline.
     */
    public void setEndDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * Get the start of the deadline.
     * Same date time as the end of the deadline.
     * @return
     */
    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    /**
     * Get the end of the deadline.
     * Same date time as the end of the deadline.
     * @return
     */
    public LocalDateTime getEndDate() {
        return this.startDate;
    }

    @Override
    /**
     * Gets the type of date
     */
    public String getType() {
        return "Deadline";
    }

    /**
     * Get the name of the deadline
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the id of the deadline
     * @return
     */
    public Integer getId() {return this.id; }

    /**
     * Get the description of the deadline
     * @return desc
     */
    public String getDescription() {return this.description; }

    public String getHTMLStartDate() { return htmlDateFormat.format(startDate); }
}
