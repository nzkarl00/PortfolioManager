package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * the model of a group to get all relevant info from in the th model
 */
public class Group {
    public String longName;
    public String shortName;
    int id;
    List<User> members = new ArrayList<>();

    public static int MWAG_GROUP_ID = 2;
    public static int TEACHERS_GROUP_ID = 1;

    /**
     * build based on the grpc response
     * @param response the grpc response to build from
     */
    public Group (GroupDetailsResponse response) {
        longName = response.getLongName();
        shortName = response.getShortName();
        id = response.getGroupId();
        for (UserResponse user : response.getMembersList()) {
            members.add(new User(user));
        }
    }

    /**
     * Gets long name of the group
     * @return  long name of the group
     */
    public String getLongName() {return longName; }

    /**
     * Gets short name of the group
     * @return  short name of the group
     */
    public String getShortName() {return shortName; }

    /**
     * Gets number of member in the group
     * @return number of member in the group
     */
    public int getNumMembers() {
        return members.size();
    }

    /**
     * Gets id of the group
     * @return id of the group
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the list of members in the group
     * @return the list of members in the group
     */
    public List<User> getMembers() {
        return members;
    }

    /**
     * Checks if the group is one of the default groups, such as
     * Members without a Group and teachers group.
     * @return true if is a default group
     */
    public boolean isDefaultGroup() {
        return id == MWAG_GROUP_ID || id == TEACHERS_GROUP_ID;
    }
}
