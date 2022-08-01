package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The entity representation for a user's application preferences, such as:
 * - the user's last sorting mode (sorting column and sorting order)
 */
@Entity
public class UserPreference {

    @Id
    @Column(name = "registered_user_id")
    private int registeredUserId;
    @Column(name = "sort_column")
    private String sortCol;
    @Column(name = "sort_order")
    private int sortOrder;

    public UserPreference() {}

    public UserPreference(int registeredUserId, String sortCol, int sortOrder) {
        this.registeredUserId = registeredUserId;
        this.sortCol = sortCol; // The column in the user table to sort, e.g. by last name, by alias etc.
        this.sortOrder = sortOrder; // The order to sort, e.g. asc or desc
    }

    @Override
    public String toString() {
        return String.format(
        "User Preference for [registeredUserId=%d] has their user table sorted by [sortCol='%s'] and order [sortOrder='%s']",
        registeredUserId, sortCol, sortOrder);
    }


    /* Setters */

    public void setRegisteredUserId(int registeredUserId) { this.registeredUserId = registeredUserId; }

    public void setSortCol(String sortCol) { this.sortCol = sortCol; }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /* Getters */

    public int getRegisteredUserId() { return registeredUserId; }

    public String getSortCol() { return sortCol; }

    public int getSortOrder() {
        return sortOrder;
    }

}
