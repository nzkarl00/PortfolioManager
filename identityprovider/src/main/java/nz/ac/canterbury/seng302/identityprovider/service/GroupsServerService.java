package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupsServiceGrpc.GroupsServiceImplBase;
import nz.ac.canterbury.seng302.shared.util.PaginationRequestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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

    Logger logger = LoggerFactory.getLogger(GroupsServerService.class);

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
     * Takes a request and adds a list of users to a given group through the Group Membership table.
     * Adding a user to a group involves to remove it from the Members Without A Group.
     * Adding a user to a teacher group involves updating the role for that user to a teacher too.
     * Adding a user t0 MWAG group would involve removing that user from all other groups.
     * Adding a user to a group it is a part of already should be ignored to avoid duplicates.
     * @param request the request, containing the ids of the group and list of user ids
     * @param responseObserver sends a response back to the client
     */
    @Transactional
    @Override
    public void addGroupMembers(AddGroupMembersRequest request, StreamObserver<AddGroupMembersResponse> responseObserver) {

        Groups groupToAddTo = groupRepo.findByGroupId(request.getGroupId());

        for (int userId : request.getUserIdsList()) {
            AccountProfile user = repo.findById(userId);

            // if a group membership contains MWAG, remove it, see @PostConstruct
            List<Groups> noMembers = groupRepo.findAllByGroupShortName(MWAG_GROUP_NAME_SHORT);
            groupMembershipRepo.deleteByRegisteredGroupsAndRegisteredGroupUser(noMembers.get(0), user);

            // if the user is being added to the teacher group, then update this change for the user role too.
            if (groupToAddTo.getGroupShortName().equals(TEACHER_GROUP_NAME_SHORT) && !user.getRoles().stream().map(role -> role.getRole()).collect(Collectors.toList()).contains(TEACHER_ROLE)) {
                roleRepo.save(new Role(user, TEACHER_ROLE));
            }

            // if the user is being added to the MWAG group, then update this change by removing this member from all groups.
            else if (groupToAddTo.getGroupShortName().equals(MWAG_GROUP_NAME_SHORT)) {
                List<GroupMembership> groupMemberships = groupMembershipRepo.findAllByRegisteredGroupUser(user);
                for (GroupMembership groupMembership : groupMemberships) {
                    groupMembershipRepo.deleteByGroupMembershipId(groupMembership.getGroupMembershipId());
                }
                if (user.getRoles().stream().map(role -> role.getRole()).collect(Collectors.toList()).contains(TEACHER_ROLE)){
                    List<Role> rolesOfUser = roleRepo.findAllByRegisteredUser(user); // List of roles held by that user
                    for (Role role: rolesOfUser) {
                        // Out of the roles held by that user, update the repos with the requested role to be removed.
                        if (role.getRole().equals(TEACHER_ROLE)) {
                            Long roleIdToRemove = role.getUserRoleId();
                            roleRepo.deleteById(roleIdToRemove);
                        }
                    }
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

        List<GroupMembership> groupMemberships = groupMembershipRepo.findAllByRegisteredGroups(groupToRemoveFrom);

        for (GroupMembership groupMember : groupMemberships) {
            AccountProfile user = groupMember.getRegisteredGroupUser();
            List<GroupMembership> usersGroups = groupMembershipRepo.findAllByRegisteredGroupUser(user);

            //If this is their last group, add to the MWAG
            if (usersGroups.size() <= 1) {
                List<Groups> noMembership = groupRepo.findAllByGroupShortName(MWAG_GROUP_NAME_SHORT);
                groupMembershipRepo.deleteByRegisteredGroupsAndRegisteredGroupUser(noMembership.get(0), user);
                groupMembershipRepo.save(new GroupMembership(noMembership.get(0), user));
            }

            // If this user from the groupToRemoveFrom is the actual user requested for removal.
            if ((request.getUserIdsList()).contains(user.getId())) {
                if (!groupToRemoveFrom.getGroupShortName().equals(MWAG_GROUP_NAME_SHORT)) {
                    groupMembershipRepo.deleteByRegisteredGroupsAndRegisteredGroupUser(groupToRemoveFrom, user);
                }

                //if the user has no groups left, add the to the MWAG
                if (user.getGroups().isEmpty()) {
                    List<Groups> noMembership = groupRepo.findAllByGroupShortName(MWAG_GROUP_NAME_SHORT);
                    groupMembershipRepo.save(new GroupMembership(noMembership.get(0), user));
                }

                // if the groupToRemoveFrom is the teacher group, remove their teacher role
                if (groupToRemoveFrom.getGroupShortName().equals(TEACHER_GROUP_NAME_SHORT)) {
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
    @Transactional
    public void deleteGroup(DeleteGroupRequest request, StreamObserver<DeleteGroupResponse> responseObserver) {
        Groups groupToBeDeleted = groupRepo.findByGroupId(Integer.valueOf(request.getGroupId()));
        if(groupToBeDeleted != null && !groupToBeDeleted.getMembers().isEmpty()){

            // get all members in this group
            List<GroupMembership> allMembers = groupMembershipRepo.findAllByRegisteredGroups(groupToBeDeleted);

            // loop through all members and delete them one by one from the GroupMembership Repo
            for (int i=0; i < allMembers.size(); i++) {
                groupMembershipRepo.deleteByGroupMembershipId(allMembers.get(i).getGroupMembershipId());
            }
        }
        groupRepo.deleteById(Integer.valueOf(request.getGroupId()));

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

    /**
     * build a group response to send to portfolio
     * @param group the group to build the response from
     * @return the build response
     */
    public GroupDetailsResponse buildGroupNoUsers(Groups group) {
        // set the group details
        GroupDetailsResponse.Builder response = GroupDetailsResponse.newBuilder()
            .setGroupId(group.getId())
            .setLongName(group.getGroupLongName())
            .setShortName(group.getGroupShortName());

        return response.build();
    }

    /**
     * get the pagiantion specified groups that belong to a specified user
     * @param request the request containing the pagination options and the userId
     * @param observer the place to send the response to
     * @throws Exception service exception
     */
    @Override
    public void getPaginatedGroupsForUser(GetPaginatedGroupsForUserRequest request, StreamObserver<PaginatedGroupsResponse> observer) {
        AccountProfile user = null;

        PaginatedGroupsResponse.Builder reply = PaginatedGroupsResponse.newBuilder();
        try {
            user = repo.findById(request.getUserId());

            PaginationRequestOptions options = request.getPaginationRequestOptions();
            logger.info("[GetPaginatedGroupsForUser] Getting all the group memberships for the given user");
            List<GroupMembership> memberships;
            // allow for -1 to specify get all groups that the user is a part of.
            if (options.getLimit() < 0) {
                memberships = groupMembershipRepo.findAllByRegisteredGroupUser(user);
            } else {
                memberships = groupMembershipRepo.findAllByRegisteredGroupUser(user, PageRequest.of(options.getOffset(), options.getLimit()));
            }

            reply.setResultSetSize(memberships.size());

            logger.info("[GetPaginatedGroupsForUser] Building paginated groups response");
            // build the group responses for the paginated response
            for (GroupMembership group: memberships) {
                GroupDetailsResponse response = buildGroupNoUsers(group.getRegisteredGroups());
                reply.addGroups(response);
            }
        } catch (Exception e) {

            logger.error(e.getMessage());
            // If an error occurs e.g not provided a user id that is registered
            reply.setResultSetSize(-1);
        }

        logger.info("[GetPaginatedGroupsForUser] Sending response");
        observer.onNext(reply.build());
        observer.onCompleted();
        logger.info("[GetPaginatedGroupsForUser] Response sent");

    }
}
