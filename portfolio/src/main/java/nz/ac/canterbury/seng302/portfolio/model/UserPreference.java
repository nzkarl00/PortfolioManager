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
    @Column(name = "sort_mode")
    private String sortMode;
    @Column(name = "sort_order")
    private int sortOrder;

    public UserPreference() {}

    public UserPreference(int registeredUserId, String sortMode, int sortOrder) {
        this.registeredUserId = registeredUserId;
        this.sortMode = sortMode;
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return String.format(
        "User Preference for [registeredUserId=%d] has sort mode of [sortMode='%s']",
        registeredUserId, sortMode);
    }


    /* Setters */

    public void setRegisteredUserId(int registeredUserId) { this.registeredUserId = registeredUserId; }

    public void setSortMode(String sortMode) { this.sortMode = sortMode; }

    /* Getters */

    public int getRegisteredUserId() { return registeredUserId; }

    public String getSortMode() { return sortMode; }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getSortOrder() {
        return sortOrder;
    }

}
