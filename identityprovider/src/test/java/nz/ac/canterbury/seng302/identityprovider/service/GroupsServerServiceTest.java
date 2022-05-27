package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.model.Groups;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
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

    @Test
    void creatGroup_duplicateName_failTest() {
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

    @Test
    void creatGroup_duplicateLongName_failTest() {
        List<Groups> duplicate = new ArrayList<>();
        duplicate.add(new Groups("The Society of Pompous Rapscallions", "SPTR"));
        when(groupRepo.findAllByLongName("The Society of Pompous Rapscallions")).thenReturn(duplicate);
        when(groupRepo.findAllByShortName("SPR")).thenReturn(duplicate);
        CreateGroupRequest invalidRequest = CreateGroupRequest.newBuilder()
                .setLongName("The Society of Pompous Rapscallions")
                .setShortName("TSPOR").build();

        gss.createGroup(invalidRequest, testCreateObserver);

        verify(testCreateObserver, times(1)).onCompleted();
        ArgumentCaptor<CreateGroupResponse> captor = ArgumentCaptor.forClass(CreateGroupResponse.class);
        verify(testCreateObserver, times(1)).onNext(captor.capture());
        CreateGroupResponse response = captor.getValue();
        assertFalse(response.getIsSuccess());
    }

//    @Test
//    void creatGroup_duplicateShortName_failTest() {
//        List<Groups> duplicate = new ArrayList<>();
//        duplicate.add(new Groups("This is a new group", "rrt"));
//        when(groupRepo.findAllByLongName("The Society of Pompous Rapscallions")).thenReturn(duplicate);
//        when(groupRepo.findAllByShortName("rrt")).thenReturn(duplicate);
//        CreateGroupRequest invalidRequest = CreateGroupRequest.newBuilder()
//                .setLongName("The Society of Pompous Rapscallions")
//                .setShortName("rrt").build();
//
//        gss.createGroup(invalidRequest, testCreateObserver);
//
//        verify(testCreateObserver, times(1)).onCompleted();
//        ArgumentCaptor<CreateGroupResponse> captor = ArgumentCaptor.forClass(CreateGroupResponse.class);
//        verify(testCreateObserver, times(1)).onNext(captor.capture());
//        CreateGroupResponse response = captor.getValue();
//        assertFalse(response.getIsSuccess());
//    }
//
//


}