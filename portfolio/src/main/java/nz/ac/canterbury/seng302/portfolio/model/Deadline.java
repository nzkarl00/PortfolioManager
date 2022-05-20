package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Deadline.
 * Represents a point in time (Date & Time) at which something is due.
 * The startDate and endDate concept of ProjectItem class, are treated as the same thing, only one date exists.
 */
@Entity
@Table(name="Deadline")
public class Deadline extends ProjectItem {
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
        ProjectItem.validateProperties(name, description);
    }

    /**
     * Set the start date of the deadline.
     * This actually updates the only internal date as there is no concept of a start and end date of a deadline.
     * @param startDate
     */
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public LocalDateTime getEndDate() {
        return this.startDate;
    }
}
