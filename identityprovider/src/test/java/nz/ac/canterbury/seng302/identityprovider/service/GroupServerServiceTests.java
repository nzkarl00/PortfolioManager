package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupServerServiceTests {

    /**
     * The account server service we are testing in this class
     */
    @Autowired
    private static GroupServerService groupsSS = new GroupServerService();

    /**
     * Mocked repo so the call to add and remove memberships users in the database does nothing instead
     */
    @Autowired
    static GroupRepository groupRepo = Mockito.mock(GroupRepository.class);

    /**
     * Mocked repo so the call to add and remove memberships users in the database does nothing instead
     */
    @Autowired
    static AccountProfileRepository accountRepo = Mockito.mock(AccountProfileRepository.class);

    /**
     * Mocked repo so the call to add and remove membership of users in the database does nothing instead
     */
    @Autowired
    static GroupMembershipRepository groupMemberRepo = Mockito.mock(GroupMembershipRepository.class);


    /**
     * Mocked account service so database checks can be replaced with fixed results
     */
    private static Account as = Mockito.mock(Account.class);

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<AddGroupMembersResponse> testObserverAdd = mock(StreamObserver.class);

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<RemoveGroupMembersResponse> testObserverRemove = mock(StreamObserver.class);

    /**
     * Request for test number one
     */
    private AddGroupMembersRequest testRequest1= AddGroupMembersRequest.newBuilder()
            .setGroupId(1)
            .addUserIds(1)
            .addUserIds(2)
            .addUserIds(3)
            .addUserIds(4)
            .build();

    /**
     * Request for test number one
     */
    private AddGroupMembersRequest testRequest1b= AddGroupMembersRequest.newBuilder()
            .setGroupId(1)
            .build();

    /**
     * Request for test number one
     */
    private AddGroupMembersRequest testRequest1c= AddGroupMembersRequest.newBuilder()
            .setGroupId(0)
            .build();
    /**
     * Request for test number two
     */
    private RemoveGroupMembersRequest testRequest2= RemoveGroupMembersRequest.newBuilder()
            .setGroupId(1)
            .addUserIds(1)
            .addUserIds(2)
            .addUserIds(3)
            .addUserIds(4)
            .build();


    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        groupsSS.groupRepo = groupRepo;
        groupsSS.groupMemberRepo = groupMemberRepo;
        groupsSS.accountService = as;
        groupsSS.repo = accountRepo;
    }

    private static AccountProfile testAccountProfile = new AccountProfile("test username",
            "test hash", new Date(), "test bio", "test email",
            "test/photopath/", "firstname", "lastname", "pronouns");

    /**
     * Tests adding multiple members to a given group
     * @throws Exception
     */
    @Test
    void addFourMembersToGroup() throws Exception {

        when(groupRepo.findByGroupId(Long.valueOf(1))).thenReturn(new Groups(1)); // Simulates username not found
        when(accountRepo.findById(Long.valueOf(1))).thenReturn(Optional.ofNullable(testAccountProfile)); // Simulates username not found


        groupsSS.addGroupMembers(testRequest1, testObserverAdd);
        verify(testObserverAdd, times(1)).onCompleted();
        ArgumentCaptor<AddGroupMembersResponse> captor = ArgumentCaptor.forClass(AddGroupMembersResponse.class);
        verify(testObserverAdd, times(1)).onNext(captor.capture());
        AddGroupMembersResponse response = captor.getValue();
        assertTrue(response.getIsSuccess());
    }

    /**
     * Tests adding one member to a given group
     * @throws Exception
     */
    @Test
    void addOneMemberToGroup() throws Exception {

        when(groupRepo.findByGroupId(1)).thenReturn(new Groups(1)); // Simulates username not found
        when(accountRepo.findById(1)).thenReturn(testAccountProfile); // Simulates username not found


        groupsSS.addGroupMembers(testRequest1b, testObserverAdd);
        verify(testObserverAdd, times(1)).onCompleted();
        ArgumentCaptor<AddGroupMembersResponse> captor = ArgumentCaptor.forClass(AddGroupMembersResponse.class);
        verify(testObserverAdd, times(1)).onNext(captor.capture());
        AddGroupMembersResponse response = captor.getValue();
        assertTrue(response.getIsSuccess());
    }

    /**
     * Tests adding one member to a given group that does not exist, ensuring it doesn't fail
     * @throws Exception
     */
    @Test
    void addOneMemberToGroupInvalid() throws Exception {

        when(groupRepo.findByGroupId(1)).thenReturn(new Groups(1)); // Simulates username not found
        when(accountRepo.findById(1)).thenReturn(testAccountProfile); // Simulates username not found


        groupsSS.addGroupMembers(testRequest1c, testObserverAdd);
        verify(testObserverAdd, times(1)).onCompleted();
        ArgumentCaptor<AddGroupMembersResponse> captor = ArgumentCaptor.forClass(AddGroupMembersResponse.class);
        verify(testObserverAdd, times(1)).onNext(captor.capture());
        AddGroupMembersResponse response = captor.getValue();
        assertTrue(response.getIsSuccess());
    }

    /**
     * Tests adding a huge number of members to a given group
     * @throws Exception
     */
    @Test
    void addManyMembersToGroup() throws Exception {

        when(groupRepo.findByGroupId(1)).thenReturn(new Groups(1)); // Simulates username not found
        when(accountRepo.findById(1)).thenReturn(testAccountProfile); // Simulates username not found

        AddGroupMembersRequest.Builder testRequestMany = AddGroupMembersRequest.newBuilder()
                .setGroupId(1);

        for (Integer i = 1; i < 500; i++) {
            testRequestMany.addUserIds(i);
        }


        groupsSS.addGroupMembers(testRequestMany.build(), testObserverAdd);
        verify(testObserverAdd, times(1)).onCompleted();
        ArgumentCaptor<AddGroupMembersResponse> captor = ArgumentCaptor.forClass(AddGroupMembersResponse.class);
        verify(testObserverAdd, times(1)).onNext(captor.capture());
        AddGroupMembersResponse response = captor.getValue();
        assertTrue(response.getIsSuccess());
    }


    /**
     * Tests adding multiple members to a given group
     * @throws Exception
     */
    @Test
    void removeFourMembersFromGroup() throws Exception {

        when(groupRepo.findByGroupId(1)).thenReturn(new Groups(1)); // Simulates username not found
        when(accountRepo.findById(1)).thenReturn(testAccountProfile); // Simulates username not found


        groupsSS.removeGroupMembers(testRequest2, testObserverRemove);
        verify(testObserverRemove, times(1)).onCompleted();
        ArgumentCaptor<RemoveGroupMembersResponse> captor = ArgumentCaptor.forClass(RemoveGroupMembersResponse.class);
        verify(testObserverRemove, times(1)).onNext(captor.capture());
        RemoveGroupMembersResponse response = captor.getValue();
        assertTrue(response.getIsSuccess());
    }

}
