package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupsServiceGrpc.GroupsServiceImplBase;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The GRPC server side service class specifically for the groups processing
 * contains many of the protobuf implementations to allow communication between the idp and portfolio servers
 */
@GrpcService
public class GroupsServerService extends GroupsServiceImplBase {
    @Autowired
    Account accountService;

    @Autowired
    AccountProfileRepository repo;

    @Autowired
    GroupRepository groupRepo;

    @Autowired
    GroupMembershipRepository groupMembershipRepo;

    /**
     * Create the group based on the details given in the request
     * @param request the request containing the group info
     * @param responseObserver where to send the response back to
     */
    @Override
    public void createGroup(CreateGroupRequest request, StreamObserver<CreateGroupResponse> responseObserver) {
        CreateGroupResponse.Builder reply = CreateGroupResponse.newBuilder();

        if (groupRepo.findAllByShortName(request.getShortName()).isEmpty() && groupRepo.findAllByLongName(request.getLongName()).isEmpty() && groupRepo.findAllByLongName(request.getShortName()).isEmpty())  {
            Groups newGroup = new Groups(request.getLongName(), request.getShortName());
            groupRepo.save(newGroup);
            reply
                    .setIsSuccess(true)
                    .setMessage("group successfully made")
                    .setNewGroupId(newGroup.getId());
        } else {

            reply
                    .setIsSuccess(false)
                    .setMessage("group name already in use");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Delete the group based on the details given in the request
     * @param request the request containing the group info
     * @param responseObserver where to send the response back to
     */
    @Override
    public void deleteGroup(DeleteGroupRequest request, StreamObserver<DeleteGroupResponse> responseObserver) {

        groupRepo.deleteById((long) request.getGroupId());
        DeleteGroupResponse.Builder reply = DeleteGroupResponse.newBuilder();
        reply
                .setIsSuccess(true)
                .setMessage("group has been deleted");
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();


    }



}
