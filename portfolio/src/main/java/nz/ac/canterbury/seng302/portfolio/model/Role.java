package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import java.util.Locale;

public class Role {
    private String role;

    public Role(UserRole role) {
        this.role = role.toString();
    }

    @Override
    public String toString() {
        return role.toLowerCase();
    }
}
