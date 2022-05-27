package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * The GRPC client side service class
 * contains many of the protobuf implementations to allow communication between the idp and portfolio servers
 */
public class GroupClientService extends GroupsServiceGrpc.GroupsServiceImplBase {

    @GrpcClient("identity-provider-grpc-server")
    GroupsServiceGrpc.GroupsServiceBlockingStub groupsServiceStub;


    /**
     * Takes a group ID and list of User IDs to be added as members to that group and sends it to the server
     * @param groupId the ID of the group that users should be made members of
     * @param userIds the list of IDs of the users to be added to the group
     * @return a response from the server if successful
     */
    public AddGroupMembersResponse addUserToGroup(int groupId, ArrayList<Integer> userIds) {
        AddGroupMembersRequest.Builder request = AddGroupMembersRequest.newBuilder().setGroupId(groupId);

        for (Integer userId : userIds) {
            request.addUserIds(userId);
        }

        return groupsServiceStub.addGroupMembers(request.build());
    }

    /**
     * Takes a group ID and list of User IDs to be removed from that group and sends it to the server
     * @param groupId the ID of the group that users should be removed from
     * @param userIds the list of IDs of the users to be removed from the group
     * @return a response from the server if successful
     */
    public RemoveGroupMembersResponse removeUserFromGroup(int groupId, ArrayList<Integer> userIds) {
        RemoveGroupMembersRequest.Builder request = RemoveGroupMembersRequest.newBuilder().setGroupId(groupId);

        for (Integer userId : userIds) {
            request.addUserIds(userId);
        }

        return groupsServiceStub.removeGroupMembers(request.build());
    }

}
