package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.*;
import nz.ac.canterbury.seng302.identityprovider.util.FileSystemUtils;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupsServiceGrpc.GroupsServiceImplBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

import java.util.List;

/**
 * The GRPC server side service class specifically for the groups processing
 * contains many of the protobuf implementations to allow communication between the idp and portfolio servers
 */
@GrpcService
public class GroupsServerService extends GroupsServiceImplBase {

    @Autowired
    Account accountService;

    @Autowired
    GroupRepository groupRepo;

    @Autowired
    GroupMembershipRepository groupMembershipRepo;

    @Autowired
    RolesRepository roleRepo;

    @Autowired
    AccountProfileRepository repo;

    String TEACHER_LONG = "Teachers Group";
    String TEACHER_SHORT = "TG";
    String MWAG_LONG = "Members Without a Group";
    String MWAG_SHORT = "MWAG";
    String TEACHER_ROLE = "2teacher";

    /**
     * make the special groups if they don't already exist
     */
    @PostConstruct
    private void makeSpecialGroups() {

        Groups teachers = initGroup(TEACHER_LONG, TEACHER_SHORT);

        /*
        for (GroupMembership membership : teachers.getMembers()) {
            if (!membership.getRegisteredGroupUser().getRoles().contains(TEACHER_ROLE)) {
                roleRepo.save(new Role(membership.getRegisteredGroupUser(), TEACHER_ROLE));
            }
        }*/

        Groups noGroup = initGroup(MWAG_LONG, MWAG_SHORT);

        /*
        for (AccountProfile profile: repo.findAll()) {
            // if it has no groups, put it in the empty group
            if (profile.getGroups().isEmpty()) {
                groupMembershipRepo.save(new GroupMembership(profile, noGroup));
            // this > 1 avoids the case of deleting the MWAG group if it belongs there
            } else if (profile.getGroups().size() > 1) {
                groupMembershipRepo.deleteByRegisteredGroupsAndRegisteredGroupUser(noGroup, profile);
            }
        }*/
    }

    private Groups initGroup(String longName, String shortName) {
        List<Groups> currentGroup = groupRepo.findAllByGroupLongName(longName);
        Groups group;
        if (currentGroup.isEmpty()) {
            group = new Groups(longName, shortName);
            groupRepo.save(group);
        } else {
            group = currentGroup.get(0);
        }
        return group;
    }

    /**
     * Takes a request and adds a list of users to a given group through the Group Membership table
     * @param request the request, containing the ids of the group and list of user ids
     * @param responseObserver sends a response back to the client
     */
    @Transactional
    @Override
    public void addGroupMembers(AddGroupMembersRequest request, StreamObserver<AddGroupMembersResponse> responseObserver) {
        boolean isTeacherGroup = false;

        Groups currentGroup = groupRepo.findByGroupId(Long.valueOf(request.getGroupId()));
        Groups teacherGroup = groupRepo.findAllByGroupShortName(TEACHER_SHORT).get(0);
        if (currentGroup.equals(teacherGroup)) {
            isTeacherGroup = true;
        }

        for (int userId : request.getUserIdsList()) {
            AccountProfile user = repo.findById(userId);

            // if a group membership contains MWAG, remove it, see @PostConstruct
            List<Groups> noMembers = groupRepo.findAllByGroupShortName(MWAG_SHORT);
            groupMembershipRepo.deleteByRegisteredGroupsAndRegisteredGroupUser(noMembers.get(0), user);

            if (isTeacherGroup) {
                roleRepo.save(new Role(user, TEACHER_ROLE));
            }

            GroupMembership newGroupUser = new GroupMembership(user, currentGroup);
            groupMembershipRepo.save(newGroupUser);
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
    @Transactional
    @Override
    public void removeGroupMembers(RemoveGroupMembersRequest request, StreamObserver<RemoveGroupMembersResponse> responseObserver) {

        boolean isTeacherGroup = false;

        Groups currentGroup = groupRepo.findByGroupId(Long.valueOf(request.getGroupId()));
        Groups teacherGroup = groupRepo.findAllByGroupShortName(TEACHER_SHORT).get(0);
        if (currentGroup.equals(teacherGroup)) {
            isTeacherGroup = true;
        }

        List<GroupMembership> groupMemberships = groupMembershipRepo.findAllByRegisteredGroups(currentGroup);
        for (GroupMembership userGroup : groupMemberships) {
            AccountProfile userAccount = userGroup.getRegisteredGroupUser();
            if ((request.getUserIdsList()).contains(userAccount.getId())) {
                Long membershipId = userGroup.getGroupMembershipId();
                groupMembershipRepo.deleteById(membershipId);
                //if the user has no groups left, add the to the MWAG
                if (userAccount.getGroups().isEmpty()) {
                    List<Groups> noMembership = groupRepo.findAllByGroupShortName(MWAG_SHORT);
                    groupMembershipRepo.save(new GroupMembership(noMembership.get(0), userAccount));
                }

                // if the group is the teacher group, remove their teacher role
                if (isTeacherGroup) {
                    List<Role> roles = roleRepo.findAllByRegisteredUser(userAccount);
                    for (Role role: roles) {
                        if (role.getRole().equals(TEACHER_ROLE)) {
                            Long roleId = role.getUserRoleId();
                            roleRepo.deleteById(roleId);
                        }
                    }
                }
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



    /**
     * Create the group based on the details given in the request
     * @param request the request containing the group info
     * @param responseObserver where to send the response back to
     */
    @Override
    public void createGroup(CreateGroupRequest request, StreamObserver<CreateGroupResponse> responseObserver) {
        CreateGroupResponse.Builder reply = CreateGroupResponse.newBuilder();

        if (groupRepo.findAllByGroupShortName(request.getShortName()).isEmpty() && groupRepo.findAllByGroupLongName(request.getLongName()).isEmpty() && groupRepo.findAllByGroupLongName(request.getShortName()).isEmpty())  {
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
        if (!(groupRepo.findAllByGroupId(request.getGroupId()).isEmpty())){
            reply
                    .setIsSuccess(true)
                    .setMessage("group has been deleted");
        } else {
            reply
                    .setIsSuccess(false)
                    .setMessage("group id is incorrect");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Get a bunch of groups for the portfolio
     * @param request contains the details of the groups we are looking for
     * @param observer where to send the groups to
     */
    @Override
    public void getPaginatedGroups(GetPaginatedGroupsRequest request, StreamObserver<PaginatedGroupsResponse> observer) {

        Sort.Direction direction;
        if (request.getIsAscendingOrder()) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        if (request.getOffset() < 0) {
            throw new IllegalArgumentException("offset must be greater than 0");
        }

        // as there is only really one thing to sort by, names, we sort by the long name always, and the set limits
        List<Groups> groups = groupRepo.findAll(PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(direction, "groupLongName")));

        PaginatedGroupsResponse.Builder reply = PaginatedGroupsResponse.newBuilder();

        // build the group responses for the paginated response
        for (Groups group: groups) {
            GroupDetailsResponse response = buildGroup(group);
            reply.addGroups(response);
        }

        observer.onNext(reply.build());
        observer.onCompleted();
    }

    /**
     * build a group response to send to portfolio
     * @param group the group to build the response from
     * @return the build response
     */
    public GroupDetailsResponse buildGroup(Groups group) {
        // set the group details
        GroupDetailsResponse.Builder response = GroupDetailsResponse.newBuilder()
            .setGroupId(group.getId())
            .setLongName(group.getGroupLongName())
            .setShortName(group.getGroupShortName());

        // get all the memberships, from that build the details response members
        List<GroupMembership> members = groupMembershipRepo.findAllByRegisteredGroups(group);
        for (GroupMembership membership : members) {
            response.addMembers(accountService.buildUserResponse(membership.getRegisteredGroupUser()));
        }

        return response.build();
    }

}
