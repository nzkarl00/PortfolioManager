package nz.ac.canterbury.seng302.portfolio.model;

public class EventUpdate {
    private FetchUpdateType updateType;

    public EventUpdate() {
    }

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
