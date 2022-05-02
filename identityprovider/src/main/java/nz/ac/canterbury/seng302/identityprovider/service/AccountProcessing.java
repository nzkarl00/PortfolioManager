package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.Role;

import java.util.ArrayList;
import java.util.List;

public class AccountProcessing {

    /**
     * Updates the usersSorted list with the correct users in the order given by the sorted roles query
     * @param usersSorted the list to update
     * @param roles the order to base the update from
     */
    public static void updateUsersSorted(List<AccountProfile> usersSorted, List<Role> roles, AccountProfileRepository repo) {
        ArrayList<Long> userIds = new ArrayList<>(); // keeps track of users we already have
        for (Role role: roles) {
            Long userId = role.getUserRoleId();
            if (!userIds.contains(userId)){
                userIds.add(role.getUserRoleId());
                usersSorted.add(repo.findById(userId.intValue()));
            }
        }
    }
}
