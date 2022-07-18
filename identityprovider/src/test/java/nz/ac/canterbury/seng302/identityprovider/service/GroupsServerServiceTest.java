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

    @Autowired
    static GroupRepository groupRepo = Mockito.mock(GroupRepository.class);

    @Autowired
    static GroupMembershipRepository groupMembershipRepo = Mockito.mock(GroupMembershipRepository.class);

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        gss.groupRepo = groupRepo;
        gss.groupMembershipRepo = groupMembershipRepo;
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
     * Test to delete invalid group to get error - done by providing an incorrect groupId
     */
    @Test
    void deleteGroup_invalidGroup_TestForFailure() {

        List<Groups> groupsCheck = new ArrayList<>();
        when(groupRepo.findAllByGroupId(1)).thenReturn(groupsCheck);
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
     * given a user you want to add is in the Members Without a Group (MWAG)
     * addGroupMembers() will not only add the user in a new group, but also
     * satisfy the special group case so the user will no longer be in MWAG
     */
    @Test
    void givenUserInMWAG_addGroupMembers_willRemoveUserFromMWAG() {

        Groups mwagGroup = new Groups();
        mwagGroup.setGroupShortName("MWAG");
        mwagGroup.setGroupLongName("Members Without a Group");

        AccountProfile testUser = new AccountProfile();

        when(groupRepo.findByGroupId(1)).thenReturn(mwagGroup);
        when(mwagGroup.getMembers()).thenReturn((List<GroupMembership>) testUser);

        int isSpecialGroup = gss.checkIsSpecialGroup(mwagGroup);

        AddGroupMembersRequest request = AddGroupMembersRequest.newBuilder().setGroupId(1).setUserIds(1, 1).build();
        gss.addGroupMembers(request, testAddGroupMembersObserver);

        verify(testAddGroupMembersObserver, times(1)).onCompleted();
        ArgumentCaptor<AddGroupMembersResponse> captor = ArgumentCaptor.forClass(AddGroupMembersResponse.class);
        verify(testAddGroupMembersObserver, times(1)).onNext(captor.capture());
        AddGroupMembersResponse response = captor.getValue();
        assertEquals(response.getIsSuccess(), true);
        assertEquals(response.getMessage(), "Users: " + request.getUserIdsList() + " added.");
        assertEquals(isSpecialGroup, 2);
    }

}