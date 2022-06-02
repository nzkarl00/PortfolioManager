package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.*;
import nz.ac.canterbury.seng302.identityprovider.util.FileSystemUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * The GRPC server side service class
 * contains many of the protobuf implementations to allow communication between the idp and portfolio servers
 */
@GrpcService
public class GroupServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    @Autowired
    Account accountService;

    @Autowired
    AccountProfileRepository repo;

    @Autowired
    GroupMembershipRepository groupMemberRepo;

    @Autowired
    GroupRepository groupRepo;

    @Autowired
    RolesRepository roleRepo;

    @Autowired
    private FileSystemUtils fsUtils;

    /**
     * Takes a request and adds a list of users to a given group through the Group Membership table
     * @param request the request, containing the ids of the group and list of user ids
     * @param responseObserver sends a response back to the client
     */
    @Override
    public void addGroupMembers(AddGroupMembersRequest request, StreamObserver<AddGroupMembersResponse> responseObserver) {

        Groups currentGroup = groupRepo.findByGroupId(Long.valueOf(request.getGroupId()));

        for (int userId : request.getUserIdsList()) {
            AccountProfile user = repo.findById(userId);
            GroupMembership newGroupUser = new GroupMembership(user, currentGroup);
            groupMemberRepo.save(newGroupUser);
        }


        Boolean isSuccessful = true;
        String responseMessage = "Users: " + request.getUserIdsList() + " added.";

        addGroupMembersResponse(isSuccessful, responseMessage, responseObserver);
    }

    /**
     * Generates and returns a response for the addGroupMembers functionality
     * @param success true if the process was a success
     * @param messageResponse the message to be sent
     * @param responseObserver sends a response back to the client
     */
    private void addGroupMembersResponse(Boolean success, String messageResponse, StreamObserver<AddGroupMembersResponse> responseObserver) {
        AddGroupMembersResponse.Builder reply = AddGroupMembersResponse.newBuilder();
        reply
                .setIsSuccess(success)
                .setMessage(messageResponse);

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();

    }


    /**
     * Takes a request and removes a list of users from a given group through the Group Membership table
     * @param request the request, containing the ids of the group and list of user ids
     * @param responseObserver sends a response back to the client
     */
    @Override
    public void removeGroupMembers(RemoveGroupMembersRequest request, StreamObserver<RemoveGroupMembersResponse> responseObserver) {

        Groups parentGroup = groupRepo.findByGroupId(Long.valueOf(request.getGroupId()));

        List<GroupMembership> groupMemberships = groupMemberRepo.findAllByRegisteredGroups(parentGroup);
        for (GroupMembership userGroup : groupMemberships) {
            AccountProfile userAccount = userGroup.getRegisteredGroupUser();
            if ((request.getUserIdsList()).contains(userAccount.getId())) {
                Long membershipId = userGroup.getGroupMembershipId();
                groupMemberRepo.deleteById(membershipId);
            }
        }

        Boolean isSuccessful = true;
        String responseMessage = "Users: " + request.getUserIdsList() + " removed.";

        removeGroupMembersResponse(isSuccessful, responseMessage, responseObserver);
    }

    /**
     * Generates and returns a response for the removeGroupMembers functionality
     * @param success true if the process was a success
     * @param messageResponse the message to be sent
     * @param responseObserver sends a response back to the client
     */
    private void removeGroupMembersResponse(Boolean success, String messageResponse, StreamObserver<RemoveGroupMembersResponse> responseObserver) {
        RemoveGroupMembersResponse.Builder reply = RemoveGroupMembersResponse.newBuilder();
        reply
                .setIsSuccess(success)
                .setMessage(messageResponse);

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }


}
