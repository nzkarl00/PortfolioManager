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
    int id = 1;
    List<User> members = new ArrayList<>();

    /**
     * build based on the grpc response
     * @param response the grpc response to build from
     */
    public Group (GroupDetailsResponse response) {
        longName = response.getLongName();
        shortName = response.getShortName();
        for (UserResponse user : response.getMembersList()) {
            members.add(new User(user));
        }
    }

    public int getNumMembers() {
        return members.size();
    }

    public int getId() {
        return id;
    }

    public List<User> getMembers() {
        return members;
    }
}
