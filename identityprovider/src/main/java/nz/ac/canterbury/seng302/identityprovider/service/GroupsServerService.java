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
    AccountProfileRepository repo;

    @Autowired
    RolesRepository roleRepo;

    // Constant strings to set.
    public static String TEACHER_GROUP_NAME_LONG = "Teachers Group";
    public static String TEACHER_GROUP_NAME_SHORT = "TG";
    public static String MWAG_GROUP_NAME_LONG = "Members Without a Group";
    public static String MWAG_GROUP_NAME_SHORT = "MWAG";
    public static String STUDENT_ROLE = "1student";
    public static String TEACHER_ROLE = "2teacher";
    public static String ADMIN_ROLE = "3admin";

    /**
     * The function that initialize all the special groups required for the application
     * if they don't exist already. E.g. Teacher Group & Members Without A Group
     */
    @PostConstruct
    private void initAllSpecialGroup() {
        Groups teachers = initGroup(TEACHER_GROUP_NAME_LONG, TEACHER_GROUP_NAME_SHORT);
        Groups noGroup = initGroup(MWAG_GROUP_NAME_LONG, MWAG_GROUP_NAME_SHORT);
    }

    /**
     * Initialize the group into the Group repo by either creating a new group,
     * or retrieving an existing one, and then returning that group.
     * @param longName of the Group name to initialize
     * @param shortName of the Group name to initialize
     */
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
     * A function to check if the given group is a special group and return the answer as an int.
     * Special groups are: teachers group, members without a group.
     * Checks by comparing the group IDs.
     * @param groupToCheck
     * @return an int where 0, 1, 2, means no special group, teacher group, and MWAG group are identified, respectively.
     */
    public int checkIsSpecialGroup(Groups groupToCheck) {
        Integer teacherGroupId = groupRepo.findAllByGroupShortName(TEACHER_GROUP_NAME_SHORT).get(0).getId();
        Integer MWAGGroupId = groupRepo.findAllByGroupShortName(MWAG_GROUP_NAME_SHORT).get(0).getId();

        if (groupToCheck.getId() == teacherGroupId) { return 1; }
        if (groupToCheck.getId() == MWAGGroupId) { return 2; }
        return 0;
    }

    /**
     * Takes a request and adds a list of users to a given group through the Group Membership table.
     * Adding a user to a group involves to remove it from the Members Without A Group.
     * Adding a user to a teacher group involves updating the role for that user to a teacher too.
     * @param request the request, containing the ids of the group and list of user ids
     * @param responseObserver sends a response back to the client
     */
    @Transactional
    @Override
    public void addGroupMembers(AddGroupMembersRequest request, StreamObserver<AddGroupMembersResponse> responseObserver) {

        Groups groupToAddTo = groupRepo.findByGroupId(request.getGroupId());
        int isSpecialGroup = checkIsSpecialGroup(groupToAddTo);

        for (int userId : request.getUserIdsList()) {
            AccountProfile user = repo.findById(userId);

            // if a group membership contains MWAG, remove it, see @PostConstruct
            List<Groups> noMembers = groupRepo.findAllByGroupShortName(MWAG_GROUP_NAME_SHORT);
            groupMembershipRepo.deleteByRegisteredGroupsAndRegisteredGroupUser(noMembers.get(0), user);

            // if the user is being added to the teacher group, then update this change for the user role too.
            if (isSpecialGroup == 1) {
                roleRepo.save(new Role(user, TEACHER_ROLE));
            }

            // if the user is being added to the MWAG group, then update this change by removing this member from all groups.
            if (isSpecialGroup == 2) {
                List<GroupMembership> groupMemberships = groupMembershipRepo.findAllByRegisteredGroupUser(user);
                for (GroupMembership groupMembership : groupMemberships) {
                    groupMembershipRepo.deleteById(groupMembership.getGroupMembershipId());
                }
            }

            // if the user being added is not already in the group, aka it is not a duplicate group member
            // then all good to go and officially add the user to the group.
            List<GroupMembership> duplicateGroupMembership = groupMembershipRepo.findAllByRegisteredGroupsAndRegisteredGroupUser(groupToAddTo, user);
            if (duplicateGroupMembership.size() == 0) {
                groupMembershipRepo.save(new GroupMembership(user, groupToAddTo));
            }
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

        Groups groupToRemoveFrom = groupRepo.findByGroupId(request.getGroupId());
        int isSpecialGroup = checkIsSpecialGroup(groupToRemoveFrom);

        List<GroupMembership> groupMemberships = groupMembershipRepo.findAllByRegisteredGroups(groupToRemoveFrom);

        for (GroupMembership groupMember : groupMemberships) {
            AccountProfile user = groupMember.getRegisteredGroupUser();

            // If this user from the groupToRemoveFrom is the actual user requested for removal.
            if ((request.getUserIdsList()).contains(user.getId())) {
                Long membershipIdToRemove = groupMember.getGroupMembershipId();
                groupMembershipRepo.deleteById(membershipIdToRemove);

                //if the user has no groups left, add the to the MWAG
                if (user.getGroups().isEmpty()) {
                    List<Groups> noMembership = groupRepo.findAllByGroupShortName(MWAG_GROUP_NAME_SHORT);
                    groupMembershipRepo.save(new GroupMembership(noMembership.get(0), user));
                }

                // if the groupToRemoveFrom is the teacher group, remove their teacher role
                if (isSpecialGroup == 1) {
                    List<Role> rolesOfUser = roleRepo.findAllByRegisteredUser(user);
                    for (Role role: rolesOfUser) {
                        if (role.getRole().equals(TEACHER_ROLE)) {
                            Long roleIdToRemove = role.getUserRoleId();
                            roleRepo.deleteById(roleIdToRemove);
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

        if (groupRepo.findAllByGroupShortName(request.getShortName()).isEmpty() &&
                groupRepo.findAllByGroupLongName(request.getLongName()).isEmpty() &&
                groupRepo.findAllByGroupLongName(request.getShortName()).isEmpty())  {
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
     * Modifies a given group
     * @param request contains the details of the groups we are changing and what to change it to
     * @param observer where to send the groups to
     */
    @Override
    public void modifyGroupDetails(ModifyGroupDetailsRequest request, StreamObserver<ModifyGroupDetailsResponse> observer) {
        ModifyGroupDetailsResponse.Builder reply = ModifyGroupDetailsResponse.newBuilder();
        Groups targetGroup = groupRepo.findByGroupId(request.getGroupId());
        if (!(targetGroup == null)) {
            if (!request.getShortName().isEmpty()) { targetGroup.setGroupShortName(request.getShortName()); }
            if (!request.getLongName().isEmpty()) { targetGroup.setGroupLongName(request.getLongName()); }
            groupRepo.save(targetGroup);
            reply.setIsSuccess(true)
                    .setMessage("Edit successful");

            observer.onNext(reply.build());
            observer.onCompleted();
        } else {
            reply.setIsSuccess(false)
                    .setMessage("Edit failed, Group does not exist");

            observer.onNext(reply.build());
            observer.onCompleted();
        }
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
        reply.setResultSetSize(groups.size());

        // build the group responses for the paginated response
        for (Groups group: groups) {
            GroupDetailsResponse response = buildGroup(group);
            reply.addGroups(response);
        }

        observer.onNext(reply.build());
        observer.onCompleted();
    }


    @Override
    public void getGroupDetails(GetGroupDetailsRequest request, StreamObserver<GroupDetailsResponse> observer) {
        Groups targetGroup = groupRepo.findByGroupId(request.getGroupId());
        GroupDetailsResponse groupInfo;
        if (!(targetGroup == null)) {
            groupInfo = buildGroup(targetGroup);
        } else {
            groupInfo = GroupDetailsResponse.newBuilder().setGroupId(-1).setLongName("").setLongName("").build();
        }
        observer.onNext(groupInfo);
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
