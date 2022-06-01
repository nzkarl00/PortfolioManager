package nz.ac.canterbury.seng302.portfolio.model;

/**
 * the message type to indicate what kind of updates for the client to request from the server
 */
public class EventUpdate {
    /**
     * the type of updates to do, sprint, event type etc...
     */
    private FetchUpdateType updateType;

    public EventUpdate() {
    }

    /**
     * construct with the updateType
     * @param updateType the type of update for the client to request
     */
    public EventUpdate(FetchUpdateType updateType) {
        this.updateType = updateType;
    }

    public FetchUpdateType getUpdateType() {
        return updateType;
    }

    public void setName(FetchUpdateType updateType) {
        this.updateType = updateType;
    }
}
