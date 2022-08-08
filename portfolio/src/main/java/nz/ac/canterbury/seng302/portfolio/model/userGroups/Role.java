package nz.ac.canterbury.seng302.portfolio.model.userGroups;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

/**
 * A class mainly responsible to parse, convert the GRPC UserRoles to this more usable class Role
 */
public class Role {
    private String role;

    public Role(UserRole role) {
        this.role = role.toString();
    }

    @Override
    public String toString() {

        String roleString = role.toLowerCase();
        if (roleString.equals("course_administrator")) {
            roleString = "admin";
        }
        return roleString;
    }

    public String toStringPresent() {
        String roleString = role.toLowerCase();
        if (roleString.equals("course_administrator")) {
            roleString = "admin";
        }
        return roleString;
    }
}
