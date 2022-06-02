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

class GroupClientServiceTest {
    // Note https://github.com/mockito/mockito/wiki/What's-new-in-Mockito-2#mock-the-unmockable-opt-in-mocking-of-final-classesmethods
    // Much of the implementation of mocking final methods came from here

    /**
     * The account server service we are testing in this class
     */
    @Autowired
    private static GroupClientService groupclientService = new GroupClientService();

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
        groupclientService.groupsServiceStub = groupServiceStub;
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