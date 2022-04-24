package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String firstName;
    public String lastName;
    public String username;
    public String nickname;
    public Integer id;
    public List<Role> roles = new ArrayList<>();

    public User(UserResponse response) {
        firstName = response.getFirstName();
        lastName = response.getLastName();
        username = response.getUsername();
        nickname = response.getNickname();
        id = 0;
        List<UserRole> tempRoles = response.getRolesList();
        for (UserRole userRole : tempRoles) {
            Role role = new Role(userRole);
            roles.add(role);
        }
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUsername() {
        return username;
    }
    public String getNickname() {
        return nickname;
    }

    public String roles() {
        String output = "";
        for (Role role: roles) {
            output += role.toString() + ", ";
        }
        return output.substring(0, output.length() - 2);
    }
    public List<String> listRoles() {
        List<String> output = new ArrayList<>();
        for (Role role: roles) {
            output.add(role.toString());
        }
        return output;
    }
}
