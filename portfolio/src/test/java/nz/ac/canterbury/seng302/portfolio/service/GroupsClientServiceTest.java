package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GroupsClientServiceTest {

    /**
     * The group server service we are testing in this class
     */
    @Autowired
    private static GroupsClientService groupClientService = new GroupsClientService();

    /**
     * The stub to contact for and mock back grpc responses
     */
    @Autowired
    private GroupsServiceGrpc.GroupsServiceBlockingStub groupServiceStub = mock(GroupsServiceGrpc.GroupsServiceBlockingStub.class);

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        groupClientService.groupServiceStub = groupServiceStub;
    }

    /**
     * Test is for creating a group
     */
    @Test
    void createGroup() {
        CreateGroupResponse expected = CreateGroupResponse.newBuilder()
                .setIsSuccess(true)
                .setMessage("group has been successfully been made")
                .setNewGroupId(1)
                .build();

        CreateGroupRequest expectedInput = CreateGroupRequest.newBuilder()
                .setLongName("The Owl Group")
                .setShortName("TOG")
                .build();

        when(groupServiceStub.createGroup(expectedInput)).thenReturn(expected);
        CreateGroupResponse actual = groupClientService.create("TOG", "The Owl Group");
        assertEquals(expected, actual);

    }

    /**
     * Test is for deleting a group
     */
    @Test
    void deleteGroup() {
        DeleteGroupResponse expected = DeleteGroupResponse.newBuilder()
                .setIsSuccess(true)
                .setMessage("group has been successfully deleted")
                .build();


        DeleteGroupRequest expectedInput = DeleteGroupRequest.newBuilder()
                .setGroupId(1)
                .build();

        when(groupServiceStub.deleteGroup(expectedInput)).thenReturn(expected);
        DeleteGroupResponse actual = groupClientService.delete(1);
        assertEquals(expected, actual);

    }


    /**
     * Test is for getting details of a group
     */
    @Test
    void getGroupDetails() {

        UserResponse user = UserResponse.newBuilder()
                .setUsername("")
                .setFirstName("")
                .setLastName("")
                .setNickname("")
                .setBio("")
                .setPersonalPronouns("He/Him")
                .setEmail("lra63@uclive.ac.nz")
                .build();

        GroupDetailsResponse expected = GroupDetailsResponse.newBuilder()
                .setLongName("Long")
                .setShortName("Short")
                .setGroupId(1)
                .addMembers(user)
                .build();

        GetGroupDetailsRequest expectedInput = GetGroupDetailsRequest.newBuilder()
                .setGroupId(1)
                .build();

        when(groupServiceStub.getGroupDetails(expectedInput)).thenReturn(expected);
        GroupDetailsResponse actual = groupClientService.getGroup(1);
        assertEquals(expected, actual);

    }

    /**
     * Test is for getting multiple group responses via pagination
     */
    @Test
    void getGroups() {

        UserResponse user = UserResponse.newBuilder()
                .setUsername("")
                .setFirstName("")
                .setLastName("")
                .setNickname("")
                .setBio("")
                .setPersonalPronouns("He/Him")
                .setEmail("lra63@uclive.ac.nz")
                .build();

        GroupDetailsResponse group = GroupDetailsResponse.newBuilder()
                .setLongName("Long")
                .setShortName("Short")
                .setGroupId(1)
                .addMembers(user)
                .build();

        PaginatedGroupsResponse expected = PaginatedGroupsResponse.newBuilder()
                .addGroups(group)
                .build();


        GetPaginatedGroupsRequest expectedInput = GetPaginatedGroupsRequest.newBuilder()
                .setLimit(1)
                .setOffset(0)
                .setIsAscendingOrder(true).build();

        when(groupServiceStub.getPaginatedGroups(expectedInput)).thenReturn(expected);
        PaginatedGroupsResponse actual = groupClientService.getGroups(1, 0, true);
        assertEquals(expected, actual);

    }

    /**
     * Test is for modifying the names of a group
     */
    @Test
    void modifyGroup() {

        ModifyGroupDetailsResponse expected = ModifyGroupDetailsResponse.newBuilder()
                .setIsSuccess(true)
                .setMessage("Edit successful")
                .build();

        ModifyGroupDetailsRequest expectedInput = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(1)
                .setShortName("EditShort")
                .setLongName("EditLong")
                .build();

        when(groupServiceStub.modifyGroupDetails(expectedInput)).thenReturn(expected);
        ModifyGroupDetailsResponse actual = groupClientService.modifyGroup(1, "EditLong", "EditShort");
        assertEquals(expected, actual);

    }

    /**
     * note this mainly tests the ability to make the request, and send it
     */
    @Test
    void addUsersToGroupValidTest() {
        ArrayList<Integer> UserIdsList = new ArrayList<>();
        UserIdsList.add(1);
        UserIdsList.add(2);
        UserIdsList.add(3);
        UserIdsList.add(4);
        AddGroupMembersResponse expected = AddGroupMembersResponse.newBuilder().setIsSuccess(true).setMessage("Users: " + UserIdsList + " added.").build();
        AddGroupMembersRequest.Builder request = AddGroupMembersRequest.newBuilder()
                .setGroupId(1);

        for (Integer userId : UserIdsList) {
            request.addUserIds(userId);
        }

        AddGroupMembersRequest expectedInput = request.build();



        when(groupServiceStub.addGroupMembers(expectedInput)).thenReturn(expected);
        AddGroupMembersResponse actual = groupServiceStub.addGroupMembers(request.build());
        assertEquals(expected, actual);
    }

    /**
     * Tests that an invalid request does not send the same response
     */
    @Test
    void addUsersToGroupInvalidTest() {
        ArrayList<Integer> UserIdsList = new ArrayList<>();
        UserIdsList.add(1);
        UserIdsList.add(2);
        UserIdsList.add(3);
        UserIdsList.add(4);
        AddGroupMembersResponse expected = AddGroupMembersResponse.newBuilder().setIsSuccess(true).setMessage("Users: " + UserIdsList + " added.").build();
        AddGroupMembersRequest.Builder request = AddGroupMembersRequest.newBuilder()
                .setGroupId(0);

        for (Integer userId : UserIdsList) {
            request.addUserIds(userId);
        }

        AddGroupMembersRequest expectedInput = request.build();
        AddGroupMembersRequest fakeRequest = AddGroupMembersRequest.newBuilder().build();


        when(groupServiceStub.addGroupMembers(expectedInput)).thenReturn(expected);
        AddGroupMembersResponse actual = groupServiceStub.addGroupMembers(fakeRequest);
        assertNotEquals(expected, actual);
    }


    /**
     * note this mainly tests the ability to make the request, and send it
     */
    @Test
    void removeUsersFromGroupValidTest() {
        ArrayList<Integer> UserIdsList = new ArrayList<>();
        UserIdsList.add(1);
        UserIdsList.add(2);
        UserIdsList.add(3);
        UserIdsList.add(4);
        RemoveGroupMembersResponse expected = RemoveGroupMembersResponse.newBuilder().setIsSuccess(true).setMessage("Users: " + UserIdsList + " removed.").build();
        RemoveGroupMembersRequest.Builder request = RemoveGroupMembersRequest.newBuilder()
                .setGroupId(1);

        for (Integer userId : UserIdsList) {
            request.addUserIds(userId);
        }

        RemoveGroupMembersRequest expectedInput = request.build();



        when(groupServiceStub.removeGroupMembers(expectedInput)).thenReturn(expected);
        RemoveGroupMembersResponse actual = groupServiceStub.removeGroupMembers(request.build());
        assertEquals(expected, actual);
    }

    /**
     * Tests that an invalid request does not send the same response
     */
    @Test
    void removeUsersFromGroupInvalidTest() {
        ArrayList<Integer> UserIdsList = new ArrayList<>();
        UserIdsList.add(1);
        UserIdsList.add(2);
        UserIdsList.add(3);
        UserIdsList.add(4);
        RemoveGroupMembersResponse expected = RemoveGroupMembersResponse.newBuilder().setIsSuccess(true).setMessage("Users: " + UserIdsList + " removed.").build();
        RemoveGroupMembersRequest.Builder request = RemoveGroupMembersRequest.newBuilder()
                .setGroupId(1);

        for (Integer userId : UserIdsList) {
            request.addUserIds(userId);
        }

        RemoveGroupMembersRequest expectedInput = request.build();
        RemoveGroupMembersRequest fakeRequest = RemoveGroupMembersRequest.newBuilder().build();



        when(groupServiceStub.removeGroupMembers(expectedInput)).thenReturn(expected);
        RemoveGroupMembersResponse actual = groupServiceStub.removeGroupMembers(fakeRequest);
        assertNotEquals(expected, actual);
    }

}
