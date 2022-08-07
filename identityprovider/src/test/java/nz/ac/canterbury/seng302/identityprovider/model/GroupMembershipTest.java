package nz.ac.canterbury.seng302.identityprovider.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupMembershipTest {

    @Test
    void check_account_profile_and_group_are_linked_to_group_membership_table() {

        String expectedUsername = "test username";
        String expectedPasswordHash = "Test Password";
        Long seconds = new Date().getTime();
        Date expectedRegisterDate = new Date(seconds);
        String expectedBio = "Test Bio";
        String expectedEmail = "test@test.Test";
        String expectedFirstName = "Firstname";
        String expectedLastName = ":Lastname";
        String expectedPronouns = "Pronouns";

        // make AccountProfile instance for testing
        AccountProfile mockAccountProfile = new AccountProfile(expectedUsername, expectedPasswordHash, expectedRegisterDate, expectedBio, expectedEmail, null, expectedFirstName, expectedLastName, expectedPronouns);

        String expectedLongGroupName = "Teacher Group";
        String expectedShortGroupName = "TG";

        // make Group instance for testing
        Groups mockGroups = new Groups(expectedLongGroupName, expectedShortGroupName);

        GroupMembership actualGroupMembership = new GroupMembership(mockGroups, mockAccountProfile);

        assertEquals(mockAccountProfile, actualGroupMembership.getRegisteredGroupUser());
        assertEquals(mockGroups, actualGroupMembership.getRegisteredGroups());

    }

}
