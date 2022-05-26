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


    public AddGroupMembersResponse addUserToGroup(int groupId, ArrayList<Integer> userIds) {
        AddGroupMembersRequest.Builder request = AddGroupMembersRequest.newBuilder().setGroupId(groupId);

        for (Integer userId : userIds) {
            request.addUserIds(userId);
        }

        return groupsServiceStub.addGroupMembers(request.build());
    }

    public RemoveGroupMembersResponse removeUserFromGroup(int groupId, ArrayList<Integer> userIds) {
        RemoveGroupMembersRequest.Builder request = RemoveGroupMembersRequest.newBuilder().setGroupId(groupId);

        for (Integer userId : userIds) {
            request.addUserIds(userId);
        }

        return groupsServiceStub.removeGroupMembers(request.build());
    }

}
