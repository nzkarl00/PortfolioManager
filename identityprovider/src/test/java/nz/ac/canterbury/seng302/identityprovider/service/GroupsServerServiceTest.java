package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.model.Groups;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

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

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        gss.groupRepo = groupRepo;
    }

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<CreateGroupResponse> testCreateObserver = mock(StreamObserver.class);

    /**
     * Tests to make a valid group
     */
    @Test
    void createGroup_validNames() {
        List<Groups> groupsCheck = new ArrayList<>();
        when(groupRepo.findAllByLongName("The Society of Pompous Rapscallions")).thenReturn(groupsCheck);
        when(groupRepo.findAllByShortName("SPR")).thenReturn(groupsCheck);

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
        when(groupRepo.findAllByLongName("The Society of Pompous Rapscallions")).thenReturn(duplicate);
        when(groupRepo.findAllByLongName("SPR")).thenReturn(groupsCheck);
        when(groupRepo.findAllByLongName("SPR")).thenReturn(duplicate);
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
        when(groupRepo.findAllByLongName("The New Society of Pompous Rapscallions")).thenReturn(groupsCheck);
        when(groupRepo.findAllByShortName("SPR")).thenReturn(groupsCheck);
        when(groupRepo.findAllByShortName("SPR")).thenReturn(duplicate);
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
        when(groupRepo.findAllByShortName("SPR")).thenReturn(groupsCheck);
        when(groupRepo.findAllByLongName("SPR")).thenReturn(duplicate);
        when(groupRepo.findAllByLongName("The Owl Group")).thenReturn(groupsCheck);
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





    }


}