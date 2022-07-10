package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Deadline.
 * Represents a point in time (Date & Time) at which something is due.
 * The startDate and endDate concept of ProjectItem class, are treated as the same thing, only one date exists.
 */
@Entity
@Table(name="event")
public class Event extends ProjectTimeBoundItem {

    protected Event() {}

    /**
     * Construct a new event.
     * Parameters must be valid for the given project context before construction.
     * Preconditions:
     * - Validate parameters with the validateProperties method.
     * @param parentProject
     * @param name
     * @param description
     * @param startDate
     * @param endDate
     */
    public Event(Project parentProject, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        super(parentProject, name, description, startDate, endDate);
    }

    public static void validateProperties(String name, String description) throws IllegalArgumentException {
        ProjectTimeBoundItem.validateProperties(name, description);
    }

    /**
     * Set the start date of the event.
     * @param startDate The start date time of the event
     */
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * Set the end date of the event.
     * @param endDate the end date time of the event.
     */
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * Get the start of the event.
     * @return
     */
    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    /**
     * Get the end of the event.
     * @return
     */
    public LocalDateTime getEndDate() {
        return this.endDate;
    }

    @Override
    /**
     * Gets the type of date
     */
    public String getType() {
        return "Event";
    }

    /**
     * Gets the date in format YYYY-MM-dd
     * @return
     */
    public String getHTMLStartDate() { return htmlDateFormat.format(startDate); }

    /**
     * get the name of the event
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * get the id of the event
     * @return the id
     */
    public int getId() {
        return this.id;
    }
}