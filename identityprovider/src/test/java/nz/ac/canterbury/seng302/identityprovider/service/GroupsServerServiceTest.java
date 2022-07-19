package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class GroupsServerServiceTest {

    /**
     * The group server service we are testing in this class
     */
    @Autowired
    private static GroupsServerService gss = new GroupsServerService();

    /**
     * The repos we are testing in this class
     */
    @Autowired
    static GroupRepository groupRepo = Mockito.mock(GroupRepository.class);
    @Autowired
    static GroupMembershipRepository groupMembershipRepo = Mockito.mock(GroupMembershipRepository.class);
    @Autowired
    static AccountProfileRepository accountProfileRepo = Mockito.mock(AccountProfileRepository.class);
    @Autowired
    static RolesRepository rolesRepo = Mockito.mock(RolesRepository.class);



    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        gss.groupRepo = groupRepo;
        gss.groupMembershipRepo = groupMembershipRepo;
        gss.repo = accountProfileRepo;
        gss.roleRepo = rolesRepo;
    }

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<CreateGroupResponse> testCreateObserver = mock(StreamObserver.class);

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<DeleteGroupResponse> testDeleteObserver = mock(StreamObserver.class);

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<GroupDetailsResponse> testGetObserver = mock(StreamObserver.class);

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<PaginatedGroupsResponse> testGetPaginatedObserver = mock(StreamObserver.class);

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<ModifyGroupDetailsResponse> testModifyObserver = mock(StreamObserver.class);

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<AddGroupMembersResponse> testAddGroupMembersObserver = mock(StreamObserver.class);

    /**
     * Tests to make a valid group
     */
    @Test
    void createGroup_validNames() {
        List<Groups> groupsCheck = new ArrayList<>();
        when(groupRepo.findAllByGroupLongName("The Society of Pompous Rapscallions")).thenReturn(groupsCheck);
        when(groupRepo.findAllByGroupShortName("SPR")).thenReturn(groupsCheck);

        CreateGroupRequest request = CreateGroupRequest.newBuilder()
                .setLongName("The Society of Pompous Rapscallions")
                .setShortName("SPR").build();

        gss.createGroup(request, testCreateObserver);

        verify(testCreateObserver, times(1)).onCompleted();
        ArgumentCaptor<CreateGroupResponse> captor = ArgumentCaptor.forClass(CreateGroupResponse.class);
        verify(testCreateObserver, times(1)).onNext(captor.capture());
        CreateGroupResponse response = captor.getValue();
        assertTrue(response.getIsSuccess());
    }

    /**
     * Tests to make a duplicate group with existing long name
     */
    @Test
    void creatGroup_duplicateLongName_failTest() {
        List<Groups> groupsCheck = new ArrayList<>();
        List<Groups> duplicate = new ArrayList<>();
        duplicate.add(new Groups("The Society of Pompous Rapscallions", "SPR"));
        when(groupRepo.findAllByGroupLongName("The Society of Pompous Rapscallions")).thenReturn(duplicate);
        when(groupRepo.findAllByGroupLongName("SPR")).thenReturn(groupsCheck);
        when(groupRepo.findAllByGroupLongName("SPR")).thenReturn(duplicate);
        CreateGroupRequest invalidRequest = CreateGroupRequest.newBuilder()
                .setLongName("The Society of Pompous Rapscallions")
                .setShortName("SPR").build();

        gss.createGroup(invalidRequest, testCreateObserver);

        verify(testCreateObserver, times(1)).onCompleted();
        ArgumentCaptor<CreateGroupResponse> captor = ArgumentCaptor.forClass(CreateGroupResponse.class);
        verify(testCreateObserver, times(1)).onNext(captor.capture());
        CreateGroupResponse response = captor.getValue();
        assertFalse(response.getIsSuccess());
    }

    /**
     * Tests to make a duplicate group with existing short name
     */
    @Test
    void creatGroup_duplicateShortName_failTest() {
        List<Groups> groupsCheck = new ArrayList<>();
        List<Groups> duplicate = new ArrayList<>();
        duplicate.add(new Groups("The Society of Pompous Rapscallions", "SPR"));
        when(groupRepo.findAllByGroupLongName("The New Society of Pompous Rapscallions")).thenReturn(groupsCheck);
        when(groupRepo.findAllByGroupShortName("SPR")).thenReturn(groupsCheck);
        when(groupRepo.findAllByGroupShortName("SPR")).thenReturn(duplicate);
        CreateGroupRequest invalidRequest = CreateGroupRequest.newBuilder()
                .setLongName("The New Society of Pompous Rapscallions")
                .setShortName("SPR").build();

        gss.createGroup(invalidRequest, testCreateObserver);

        verify(testCreateObserver, times(1)).onCompleted();
        ArgumentCaptor<CreateGroupResponse> captor = ArgumentCaptor.forClass(CreateGroupResponse.class);
        verify(testCreateObserver, times(1)).onNext(captor.capture());
        CreateGroupResponse response = captor.getValue();
        assertFalse(response.getIsSuccess());
    }

    /**
     * Tests to make a new group with the same short name as an existing groups long name
     */
    @Test
    void creatGroup_duplicateShortNameInLongName_failTest() {
        List<Groups> groupsCheck = new ArrayList<>();
        List<Groups> duplicate = new ArrayList<>();
        duplicate.add(new Groups("SPR", "SP"));
        when(groupRepo.findAllByGroupShortName("SPR")).thenReturn(groupsCheck);
        when(groupRepo.findAllByGroupLongName("SPR")).thenReturn(duplicate);
        when(groupRepo.findAllByGroupLongName("The Owl Group")).thenReturn(groupsCheck);
        CreateGroupRequest invalidRequest = CreateGroupRequest.newBuilder()
                .setLongName("The Owl Group")
                .setShortName("SPR").build();

        gss.createGroup(invalidRequest, testCreateObserver);

        verify(testCreateObserver, times(1)).onCompleted();
        ArgumentCaptor<CreateGroupResponse> captor = ArgumentCaptor.forClass(CreateGroupResponse.class);
        verify(testCreateObserver, times(1)).onNext(captor.capture());
        CreateGroupResponse response = captor.getValue();
        assertFalse(response.getIsSuccess());

    }

    /**
     * Test to delete valid group
     */
    @Test
    void deleteGroup_validGroup() {

        List<Groups> groupsCheck = new ArrayList<>();
        groupsCheck.add(new Groups(1));
        when(groupRepo.findAllByGroupId(1)).thenReturn(groupsCheck);
        DeleteGroupRequest validDeleteRequest = DeleteGroupRequest.newBuilder()
                .setGroupId(1).build();

        gss.deleteGroup(validDeleteRequest, testDeleteObserver);

        verify(testDeleteObserver, times(1)).onCompleted();
        ArgumentCaptor<DeleteGroupResponse> captor = ArgumentCaptor.forClass(DeleteGroupResponse.class);
        verify(testDeleteObserver, times(1)).onNext(captor.capture());
        DeleteGroupResponse response = captor.getValue();
        assertTrue(response.getIsSuccess());
        assertEquals("group has been deleted", response.getMessage());


    }

    /**
     * Test to delete valid group with group members assigned
     */
    @Test
    void deleteGroup_validGroup_withMember() {

        Groups groups = new Groups(1);
        List<Groups> groupsCheck = new ArrayList<>();
        AccountProfile accountProfile = new AccountProfile();
        List<GroupMembership> groupList = new ArrayList<>();
        GroupMembership groupMembership = new GroupMembership(groups, accountProfile);
        groupMembership.setGroupMembershipId(0L);
        groupList.add(groupMembership);
        groups.setMembers(groupList);
        groupsCheck.add(groups);
        when(groupRepo.findByGroupId(1)).thenReturn(groups);
        when(groupMembershipRepo.findAllByRegisteredGroups(groups)).thenReturn(groupList);
        when(groupRepo.findAllByGroupId(1)).thenReturn(groupsCheck);

        DeleteGroupRequest validDeleteRequest = DeleteGroupRequest.newBuilder()
                .setGroupId(1).build();

        gss.deleteGroup(validDeleteRequest, testDeleteObserver);
        groupRepo.deleteById(1);

        verify(testDeleteObserver, times(1)).onCompleted();
        ArgumentCaptor<DeleteGroupResponse> captor = ArgumentCaptor.forClass(DeleteGroupResponse.class);
        verify(testDeleteObserver, times(1)).onNext(captor.capture());
        DeleteGroupResponse response = captor.getValue();
        assertTrue(response.getIsSuccess());
        assertEquals("group has been deleted", response.getMessage());

    }

    /**
     * Test to delete invalid group to get error - done by providing an incorrect groupId
     */
    @Test
    void deleteGroup_invalidGroup_TestForFailure() {

        List<Groups> groupsCheck = new ArrayList<>();
        Groups emptyGroup = new Groups();
        when(groupRepo.findAllByGroupId(1)).thenReturn(groupsCheck);
        when(groupRepo.findByGroupId(emptyGroup.getId())).thenReturn(emptyGroup);
        DeleteGroupRequest invalidDeleteRequest = DeleteGroupRequest.newBuilder()
                .setGroupId(2).build();

        gss.deleteGroup(invalidDeleteRequest, testDeleteObserver);

        verify(testDeleteObserver, times(1)).onCompleted();
        ArgumentCaptor<DeleteGroupResponse> captor = ArgumentCaptor.forClass(DeleteGroupResponse.class);
        verify(testDeleteObserver, times(1)).onNext(captor.capture());
        DeleteGroupResponse response = captor.getValue();
        assertFalse(response.getIsSuccess());
        assertEquals("group id is incorrect", response.getMessage());

    }

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<PaginatedGroupsResponse> groupsObserver = mock(StreamObserver.class);

    /**
     * test to get a valid set of groups
     */
    @Test
    void getPaginatedGroups_validRequest_blueSky() {
        Groups group =  new Groups("The Society of Pompous Rapscallions", "SPR");
        when(groupRepo.findAll(PageRequest.of(
                0, 10, Sort.by(Sort.Direction.ASC, "groupLongName"))))
                .thenReturn(new ArrayList<>(List.of(group)));
        when(groupMembershipRepo.findAllByRegisteredGroups(group)).thenReturn(new ArrayList<>());

        GetPaginatedGroupsRequest request = GetPaginatedGroupsRequest.newBuilder().setIsAscendingOrder(true).setLimit(10).setOffset(0).build();
        gss.getPaginatedGroups(request, groupsObserver);

        verify(groupsObserver, times(1)).onCompleted();
        ArgumentCaptor<PaginatedGroupsResponse> captor = ArgumentCaptor.forClass(PaginatedGroupsResponse.class);
        verify(groupsObserver, times(1)).onNext(captor.capture());
        PaginatedGroupsResponse response = captor.getValue();
        assertEquals(1, response.getGroupsCount()); // we have one group
        assertEquals(0, response.getGroups(0).getMembersCount()); //there are no members in the group
    }

    /**
     * test to get a invalid set of groups as the offset is -1, which is an invalid page
     */
    @Test
    void getPaginatedGroups_invalidRequest_testForFailure() {
        GetPaginatedGroupsRequest request = GetPaginatedGroupsRequest.newBuilder().setIsAscendingOrder(true).setLimit(10).setOffset(-1).build();
        assertThrows(IllegalArgumentException.class, () -> {
            gss.getPaginatedGroups(request, groupsObserver);
        });
    }

    @Test
    void buildGroup_validRequest_blueSky() {
        Groups group =  new Groups("The Society of Pompous Rapscallions", "SPR");
        when(groupMembershipRepo.findAllByRegisteredGroups(group)).thenReturn(new ArrayList<>());
        GroupDetailsResponse repsonse = gss.buildGroup(group);
        assertEquals("SPR", repsonse.getShortName());
        assertEquals("The Society of Pompous Rapscallions", repsonse.getLongName());
        assertEquals(0, repsonse.getMembersCount());
    }
    /**
     * Test that a group can be retrieved
     */
    @Test
    void get_group_validGroup() {
        Groups testGroup = new Groups();
        testGroup.setGroupShortName("TestShort");
        testGroup.setGroupLongName("TestLong");

        List<GroupMembership> members = new ArrayList<>();

        GetGroupDetailsRequest request = GetGroupDetailsRequest.newBuilder().setGroupId(1).build();
        when(groupRepo.findByGroupId(1)).thenReturn(testGroup);
        when(groupMembershipRepo.findAllByRegisteredGroups(testGroup)).thenReturn(members);

        gss.getGroupDetails(request, testGetObserver);

        verify(testGetObserver, times(1)).onCompleted();
        ArgumentCaptor<GroupDetailsResponse> captor = ArgumentCaptor.forClass(GroupDetailsResponse.class);
        verify(testGetObserver, times(1)).onNext(captor.capture());
        GroupDetailsResponse response = captor.getValue();
        assertEquals(response.getGroupId(), 0);
        assertEquals(response.getShortName(), "TestShort");
        assertEquals(response.getLongName(), "TestLong");
        assertEquals(response.getMembersList().size(), 0);

    }

    /**
     * Test getting a group that doesn't exist to get an error
     */
    @Test
    void get_group_invalidGroup() {

        List<GroupMembership> members = new ArrayList<>();

        GetGroupDetailsRequest request = GetGroupDetailsRequest.newBuilder().setGroupId(1).build();
        when(groupRepo.findByGroupId(1)).thenReturn(null);
        when(groupMembershipRepo.findAllByRegisteredGroups(null)).thenReturn(members);

        gss.getGroupDetails(request, testGetObserver);

        verify(testGetObserver, times(1)).onCompleted();
        ArgumentCaptor<GroupDetailsResponse> captor = ArgumentCaptor.forClass(GroupDetailsResponse.class);
        verify(testGetObserver, times(1)).onNext(captor.capture());
        GroupDetailsResponse response = captor.getValue();
        assertEquals(response.getGroupId(), -1);
        assertEquals(response.getShortName(), "");
        assertEquals(response.getLongName(), "");
        assertEquals(response.getMembersList().size(), 0);

    }

    /**
     * Test getting a group via pagination
     */
    @Test
    void get_group_paginated() {

        List<Groups> groupList = new ArrayList<>();
        Groups testGroup = new Groups();
        testGroup.setGroupShortName("TestShort");
        testGroup.setGroupLongName("TestLong");

        groupList.add(testGroup);

        List<GroupMembership> members = new ArrayList<>();


        GetPaginatedGroupsRequest request = GetPaginatedGroupsRequest.newBuilder().setOffset(0).setOrderBy("groupLongName").setLimit(1).setIsAscendingOrder(true).build();
        when(groupRepo.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "groupLongName")))).thenReturn(groupList);
        when(groupMembershipRepo.findAllByRegisteredGroups(testGroup)).thenReturn(members);

        gss.getPaginatedGroups(request, testGetPaginatedObserver);

        verify(testGetPaginatedObserver, times(1)).onCompleted();
        ArgumentCaptor<PaginatedGroupsResponse> captor = ArgumentCaptor.forClass(PaginatedGroupsResponse.class);
        verify(testGetPaginatedObserver, times(1)).onNext(captor.capture());
        PaginatedGroupsResponse response = captor.getValue();

        assertEquals(response.getResultSetSize(), 1);

    }

    /**
     * Test getting no groups via pagination
     */
    @Test
    void get_group_paginated_none() {

        List<Groups> groupList = new ArrayList<>();


        List<GroupMembership> members = new ArrayList<>();


        GetPaginatedGroupsRequest request = GetPaginatedGroupsRequest.newBuilder().setOffset(0).setOrderBy("groupLongName").setLimit(1).setIsAscendingOrder(true).build();
        when(groupRepo.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "groupLongName")))).thenReturn(groupList);


        gss.getPaginatedGroups(request, testGetPaginatedObserver);

        verify(testGetPaginatedObserver, times(1)).onCompleted();
        ArgumentCaptor<PaginatedGroupsResponse> captor = ArgumentCaptor.forClass(PaginatedGroupsResponse.class);
        verify(testGetPaginatedObserver, times(1)).onNext(captor.capture());
        PaginatedGroupsResponse response = captor.getValue();

        assertEquals(response.getResultSetSize(), 0);

    }

    /**
     * Test that a group can have just its long name modified
     */
    @Test
    void modify_groupDetails_validGroup_longName() {

        Groups testGroup = new Groups();
        testGroup.setGroupShortName("TestShort");
        testGroup.setGroupLongName("TestLong");

        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder().setGroupId(1).setLongName("EditLong").build();
        when(groupRepo.findByGroupId(1)).thenReturn(testGroup);

        gss.modifyGroupDetails(request, testModifyObserver);

        verify(testModifyObserver, times(1)).onCompleted();
        ArgumentCaptor<ModifyGroupDetailsResponse> captor = ArgumentCaptor.forClass(ModifyGroupDetailsResponse.class);
        verify(testModifyObserver, times(1)).onNext(captor.capture());
        ModifyGroupDetailsResponse response = captor.getValue();
        assertEquals(response.getIsSuccess(), true);
        assertEquals(response.getMessage(), "Edit successful");

    }

    /**
     * Test that the correct message is returned when a non-existent group is modified
     */
    @Test
    void modify_groupDetails_invalidGroup() {


        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder().setGroupId(1).setLongName("EditLong").build();
        when(groupRepo.findByGroupId(1)).thenReturn(null);

        gss.modifyGroupDetails(request, testModifyObserver);

        verify(testModifyObserver, times(1)).onCompleted();
        ArgumentCaptor<ModifyGroupDetailsResponse> captor = ArgumentCaptor.forClass(ModifyGroupDetailsResponse.class);
        verify(testModifyObserver, times(1)).onNext(captor.capture());
        ModifyGroupDetailsResponse response = captor.getValue();
        assertEquals(response.getIsSuccess(), false);
        assertEquals(response.getMessage(), "Edit failed, Group does not exist");
    }

    /**
     * Test modifying both names of a group that exists
     */
    @Test
    void modify_groupDetails_validGroup_longShortNames() {

        Groups testGroup = new Groups();
        testGroup.setGroupShortName("TestShort");
        testGroup.setGroupLongName("TestLong");

        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder().setGroupId(1).setLongName("EditLong").setShortName("EditShort").build();
        when(groupRepo.findByGroupId(1)).thenReturn(testGroup);

        gss.modifyGroupDetails(request, testModifyObserver);

        verify(testModifyObserver, times(1)).onCompleted();
        ArgumentCaptor<ModifyGroupDetailsResponse> captor = ArgumentCaptor.forClass(ModifyGroupDetailsResponse.class);
        verify(testModifyObserver, times(1)).onNext(captor.capture());
        ModifyGroupDetailsResponse response = captor.getValue();
        assertEquals(response.getIsSuccess(), true);
        assertEquals(response.getMessage(), "Edit successful");

    }

    /**
     * Test the function addGroupMembers()
     * given a user you want to add is non-special group A
     * addGroupMembers() will add the user into non-special group B
     */
    @Test
    void givenUserInGroupA_addGroupMembers_userWillBeInGroupB() {

        AccountProfile testUser = new AccountProfile();

        Groups groupA = new Groups();
        groupA.setGroupShortName("A");
        groupA.setGroupLongName("Group Alligators");

        GroupMembership groupAMembership = new GroupMembership(groupA, testUser);
        groupA.setMembers(new ArrayList<>(List.of(groupAMembership)));
        List<Groups> groupAMembers = new ArrayList<>(List.of(groupA));

        Groups groupB = new Groups();
        groupB.setGroupShortName("B");
        groupB.setGroupLongName("Group Baboons");
        groupB.setGroupId(4);

        Groups teacherGroup = new Groups();
        teacherGroup.setGroupShortName("TG");
        teacherGroup.setGroupLongName("Teacher Group");

        Groups mwagGroup = new Groups();
        mwagGroup.setGroupShortName("MWAG");
        mwagGroup.setGroupLongName("Members Without a Group");;

        when(groupRepo.findByGroupId(4)).thenReturn(groupB);
        when(accountProfileRepo.findById(1)).thenReturn(testUser);
        when(groupRepo.findAllByGroupShortName("MWAG")).thenReturn(new ArrayList<>(List.of(mwagGroup)));

        AddGroupMembersRequest request = AddGroupMembersRequest.newBuilder().setGroupId(4).addUserIds(1).build();
        gss.addGroupMembers(request, testAddGroupMembersObserver);

        verify(testAddGroupMembersObserver, times(1)).onCompleted();
        ArgumentCaptor<AddGroupMembersResponse> captor = ArgumentCaptor.forClass(AddGroupMembersResponse.class);
        verify(testAddGroupMembersObserver, times(1)).onNext(captor.capture());
        AddGroupMembersResponse response = captor.getValue();

        // Main part checking that a new memebership for group B is added.
        verify(groupMembershipRepo).save(refEq(new GroupMembership(groupB, testUser)));

        assertEquals(response.getIsSuccess(), true);
        assertEquals(response.getMessage(), "Users: " + request.getUserIdsList() + " added.");
    }

    /**
     * Test the function addGroupMembers()
     * given a user you want to add is in the Members Without a Group (MWAG)
     * addGroupMembers() will not only add the user in a new group, but also
     * satisfy the special group case so the user will no longer be in MWAG.
     */
    @Test
    void givenUserInMWAG_addGroupMembers_willRemoveUserFromMWAG() {

        AccountProfile testUser = new AccountProfile();

        Groups teacherGroup = new Groups();
        teacherGroup.setGroupShortName("TG");
        teacherGroup.setGroupLongName("Teacher Group");

        Groups mwagGroup = new Groups();
        mwagGroup.setGroupId(2);
        mwagGroup.setGroupShortName("MWAG");
        mwagGroup.setGroupLongName("Members Without a Group");
        GroupMembership mwagMembership = new GroupMembership(mwagGroup, testUser);
        mwagGroup.setMembers(new ArrayList<>(List.of(mwagMembership)));
        List<Groups> noMembers = new ArrayList<>(List.of(mwagGroup));

        when(groupRepo.findByGroupId(2)).thenReturn(mwagGroup);

        when(groupRepo.findAllByGroupShortName("TG")).thenReturn(new ArrayList<>(List.of(teacherGroup)));
        when(groupRepo.findAllByGroupShortName("MWAG")).thenReturn(noMembers);

        when(accountProfileRepo.findById(1)).thenReturn(testUser);

        when(groupRepo.findAllByGroupShortName("MWAG")).thenReturn(new ArrayList<>(List.of(mwagGroup)));

        AddGroupMembersRequest request = AddGroupMembersRequest.newBuilder().setGroupId(2).addUserIds(1).build();
        gss.addGroupMembers(request, testAddGroupMembersObserver);

        verify(testAddGroupMembersObserver, times(1)).onCompleted();
        ArgumentCaptor<AddGroupMembersResponse> captor = ArgumentCaptor.forClass(AddGroupMembersResponse.class);
        verify(testAddGroupMembersObserver, times(1)).onNext(captor.capture());
        // Main part checking that the user is being deleted from MWAG
        verify(groupMembershipRepo).deleteByRegisteredGroupsAndRegisteredGroupUser(noMembers.get(0), testUser);
        AddGroupMembersResponse response = captor.getValue();
        assertEquals(response.getIsSuccess(), true);
        assertEquals(response.getMessage(), "Users: " + request.getUserIdsList() + " added.");
    }

    /**
     * Test the function addGroupMembers()
     * given a user you want to add is being added to the teacher group
     * addGroupMembers() will not only add the user to the teacher group, but also
     * satisfy the special group case so the user will no longer be in MWAG
     * and the user will have a teacher role associated with it.
     */
    @Test
    void givenUser_addGroupMembersToTeacherGroup_willAddTeacherRole() {

        AccountProfile testUser = new AccountProfile();

        Groups teacherGroup = new Groups();
        teacherGroup.setGroupShortName("TG");
        teacherGroup.setGroupLongName("Teacher Group");
        teacherGroup.setGroupId(1);
        GroupMembership teacherMembership = new GroupMembership(teacherGroup, testUser);
        teacherGroup.setMembers(new ArrayList<>(List.of(teacherMembership)));

        Groups mwagGroup = new Groups();
        mwagGroup.setGroupShortName("MWAG");
        mwagGroup.setGroupLongName("Members Without a Group");

        when(groupRepo.findByGroupId(1)).thenReturn(teacherGroup);

        when(groupRepo.findAllByGroupShortName("TG")).thenReturn(new ArrayList<>(List.of(teacherGroup)));
        when(groupRepo.findAllByGroupShortName("MWAG")).thenReturn(new ArrayList<>(List.of(mwagGroup)));

        when(accountProfileRepo.findById(1)).thenReturn(testUser);

        when(groupRepo.findAllByGroupShortName("MWAG")).thenReturn(new ArrayList<>(List.of(mwagGroup)));

        AddGroupMembersRequest request = AddGroupMembersRequest.newBuilder().setGroupId(1).addUserIds(1).build();
        gss.addGroupMembers(request, testAddGroupMembersObserver);

        verify(testAddGroupMembersObserver, times(1)).onCompleted();
        ArgumentCaptor<AddGroupMembersResponse> captor = ArgumentCaptor.forClass(AddGroupMembersResponse.class);
        verify(testAddGroupMembersObserver, times(1)).onNext(captor.capture());
        // Main part checking that a teacher role is added.
        verify(rolesRepo).save(refEq(new Role(testUser, "2teacher")));
        AddGroupMembersResponse response = captor.getValue();
        assertEquals(response.getIsSuccess(), true);
        assertEquals(response.getMessage(), "Users: " + request.getUserIdsList() + " added.");
    }

    /**
     * Test the function addGroupMembers()
     * given a user you want to add is being added to the Members Without a Group (MWAG)
     * addGroupMembers() will not only add the user to the MWAG group, but also
     * satisfy the special group case so the user will not be in any other group
     */
    @Test
    void givenUser_addGroupMembersToMWAG_willDeleteAllOtherGroupMemberships() {

        AccountProfile testUser = new AccountProfile();

        Groups teacherGroup = new Groups();
        teacherGroup.setGroupShortName("TG");
        teacherGroup.setGroupLongName("Teacher Group");

        Groups mwagGroup = new Groups();
        mwagGroup.setGroupId(2);
        mwagGroup.setGroupShortName("MWAG");
        mwagGroup.setGroupLongName("Members Without a Group");
        GroupMembership mwagMembership = new GroupMembership(mwagGroup, testUser);
        mwagGroup.setMembers(new ArrayList<>(List.of(mwagMembership)));
        List<Groups> noMembers = new ArrayList<>(List.of(mwagGroup));

        when(groupRepo.findByGroupId(2)).thenReturn(mwagGroup);

        when(groupRepo.findAllByGroupShortName("TG")).thenReturn(new ArrayList<>(List.of(teacherGroup)));
        when(groupRepo.findAllByGroupShortName("MWAG")).thenReturn(noMembers);

        when(accountProfileRepo.findById(1)).thenReturn(testUser);

        when(groupRepo.findAllByGroupShortName("MWAG")).thenReturn(new ArrayList<>(List.of(mwagGroup)));

        GroupMembership groupMemberships = new GroupMembership(teacherGroup, testUser);
        when(groupMembershipRepo.findAllByRegisteredGroupUser(testUser)).thenReturn(new ArrayList<>(List.of(groupMemberships)));

        AddGroupMembersRequest request = AddGroupMembersRequest.newBuilder().setGroupId(2).addUserIds(1).build();
        gss.addGroupMembers(request, testAddGroupMembersObserver);

        verify(testAddGroupMembersObserver, times(1)).onCompleted();
        ArgumentCaptor<AddGroupMembersResponse> captor = ArgumentCaptor.forClass(AddGroupMembersResponse.class);
        verify(testAddGroupMembersObserver, times(1)).onNext(captor.capture());
        // Main part checking that the user is removed from all other groups.
        verify(groupMembershipRepo).deleteByGroupMembershipId(groupMemberships.getGroupMembershipId());
        AddGroupMembersResponse response = captor.getValue();
        assertEquals(response.getIsSuccess(), true);
        assertEquals(response.getMessage(), "Users: " + request.getUserIdsList() + " added.");
    }

    /**
     * Test the function addGroupMembers() will not add a user to a group if they
     * are already in the group, therefore preventing duplicate group members.
     */
    @Test
    void givenUserInAGroup_addGroupMembersToThatGroupAgain_willFail_andNotLetGroupMemberBeADuplicate() {

        AccountProfile testUser = new AccountProfile();

        Groups teacherGroup = new Groups();
        teacherGroup.setGroupShortName("TG");
        teacherGroup.setGroupLongName("Teacher Group");

        Groups mwagGroup = new Groups();
        mwagGroup.setGroupId(2);
        mwagGroup.setGroupShortName("MWAG");
        mwagGroup.setGroupLongName("Members Without a Group");
        GroupMembership mwagMembership = new GroupMembership(mwagGroup, testUser);
        mwagGroup.setMembers(new ArrayList<>(List.of(mwagMembership)));
        List<Groups> noMembers = new ArrayList<>(List.of(mwagGroup));

        when(groupRepo.findByGroupId(2)).thenReturn(mwagGroup);

        when(groupRepo.findAllByGroupShortName("TG")).thenReturn(new ArrayList<>(List.of(teacherGroup)));
        when(groupRepo.findAllByGroupShortName("MWAG")).thenReturn(noMembers);

        when(accountProfileRepo.findById(1)).thenReturn(testUser);

        when(groupRepo.findAllByGroupShortName("MWAG")).thenReturn(new ArrayList<>(List.of(mwagGroup)));

        GroupMembership duplicateGroupMembership = new GroupMembership(mwagGroup, testUser);
        when(groupMembershipRepo.findAllByRegisteredGroupsAndRegisteredGroupUser(mwagGroup,testUser)).thenReturn(new ArrayList<>(List.of(duplicateGroupMembership)));

        AddGroupMembersRequest request = AddGroupMembersRequest.newBuilder().setGroupId(2).addUserIds(1).build();
        gss.addGroupMembers(request, testAddGroupMembersObserver);

        verify(testAddGroupMembersObserver, times(1)).onCompleted();
        ArgumentCaptor<AddGroupMembersResponse> captor = ArgumentCaptor.forClass(AddGroupMembersResponse.class);
        verify(testAddGroupMembersObserver, times(1)).onNext(captor.capture());
        // Main part checking the duplicate group member does not get saved
        verify(groupMembershipRepo, never()).save(duplicateGroupMembership);
        AddGroupMembersResponse response = captor.getValue();
        assertEquals(response.getIsSuccess(), true);
        assertEquals(response.getMessage(), "Users: " + request.getUserIdsList() + " added.");
    }

}
