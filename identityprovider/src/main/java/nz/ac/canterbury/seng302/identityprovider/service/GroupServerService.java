package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.*;
import nz.ac.canterbury.seng302.identityprovider.util.FileSystemUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.apache.catalina.Group;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
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
    GroupMembershipRepository groupmemberrepo;

    @Autowired
    GroupRepository grouprepo;

    @Autowired
    RolesRepository roleRepo;

    @Autowired
    private FileSystemUtils fsUtils;


    private AddGroupMembersResponse addGroupMembers(AddGroupMembersRequest request) {

        Groups currentGroup = grouprepo.findById(request.getGroupId());

        for (int userId : request.getUserIdsList()) {
            AccountProfile user = repo.findById(userId);
            GroupMembership newGroupUser = new GroupMembership(user, currentGroup);
            groupmemberrepo.save(newGroupUser);
        }

        Boolean isSuccessful = true;
        String responseMessage = "Users: " + request.getUserIdsList() + " added.";

        return addGroupMembersResponse(isSuccessful, responseMessage);
    }

    private AddGroupMembersResponse addGroupMembersResponse(Boolean success, String messageResponse) {
        AddGroupMembersResponse.Builder reply = AddGroupMembersResponse.newBuilder();
        reply
                .setIsSuccess(success)
                .setMessage(messageResponse);

        return reply.build();
    }

    private RemoveGroupMembersResponse removeGroupMembers(AddGroupMembersRequest request) {

        Groups currentGroup = grouprepo.findById(request.getGroupId());

        for (int userId : request.getUserIdsList()) {
            AccountProfile user = repo.findById(userId);
            GroupMembership newGroupUser = new GroupMembership(user, currentGroup);
            groupmemberrepo.save(newGroupUser);
        }

        Boolean isSuccessful = true;
        String responseMessage = "Users: " + request.getUserIdsList() + " added.";

        return removeGroupMembersResponse(isSuccessful, responseMessage);
    }

    private RemoveGroupMembersResponse removeGroupMembersResponse(Boolean success, String messageResponse) {
        RemoveGroupMembersResponse.Builder reply = RemoveGroupMembersResponse.newBuilder();
        reply
                .setIsSuccess(success)
                .setMessage(messageResponse);

        return reply.build();
    }


}
