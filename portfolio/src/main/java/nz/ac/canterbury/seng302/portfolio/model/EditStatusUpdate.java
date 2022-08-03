package nz.ac.canterbury.seng302.portfolio.model;

public class EditStatusUpdate {
    /**
     * the type of updates to do, sprint, event type etc...
     */
    private FetchUpdateType updateType;

    private String message = "";

    public EditStatusUpdate() {
    }

    /**
     * construct with the updateType
     * @param updateType the type of update for the client to request
     */
    public EditStatusUpdate(FetchUpdateType updateType) {
        this.updateType = updateType;
    }

    public EditStatusUpdate(FetchUpdateType updateType, String message) {
        this.updateType = updateType;
        this.message = message;
    }

    public FetchUpdateType getUpdateType() {
        return updateType;
    }

    public void setName(FetchUpdateType updateType) {
        this.updateType = updateType;
    }

    public String getMessage() {
        return message;
    }
}
