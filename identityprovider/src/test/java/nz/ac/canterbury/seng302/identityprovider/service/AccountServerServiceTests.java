package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServerServiceTests {

    /**
     * The account server service we are testing in this class
     */
    @Autowired
    private static AccountServerService ass = new AccountServerService();

    /**
     * Mocked repo so the call to store registered users in the database does nothing instead
     */
    @Autowired
    static AccountProfileRepository repo = Mockito.mock(AccountProfileRepository.class);

    /**
     * Mocked repo so the call to store registered users in the database does nothing instead
     */
    @Autowired
    static RolesRepository roleRepo = mock(RolesRepository.class);

    @Autowired
    static GroupRepository groupRepo = mock(GroupRepository.class);

    @Autowired
    static GroupMembershipRepository groupMembershipRepo = mock(GroupMembershipRepository.class);


    /**
     * Mocked account service so database checks can be replaced with fixed results
     */
    @Autowired
    private static Account as = Mockito.mock(Account.class);

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<UserRegisterResponse> testObserver = mock(StreamObserver.class);

    // setup test account profile and a list of said profiles to mock the repo with
    private static AccountProfile testAccountProfile = new AccountProfile("test username",
        "test hash", new Date(), "test bio", "test email",
        "test/photopath/", "firstname", "lastname", "pronouns");
    private static ArrayList<AccountProfile> testProfiles = new ArrayList<>();

    // setup test role and a list of said roles to mock the repo with
    private static Role testRole = new Role(testAccountProfile, "1student");
    private static ArrayList<Role> testRoles = new ArrayList<>();


    // Different parameters are required for all of these where they are checked, else the Mockito when/then calls break. That's why there are three.
    // If you want to add new tests in here, you must also make a new request.
    /**
     * Request for test number one
     */
    private UserRegisterRequest testRequest1= UserRegisterRequest.newBuilder().setUsername("testusername1")
            .setPassword("testpassword")
            .setFirstName("testfirst")
            .setLastName("testlast")
            .setPersonalPronouns("he/him")
            .setEmail("test@email1")
            .build();

    /**
     * Request for test number two
     */
    private UserRegisterRequest testRequest2= UserRegisterRequest.newBuilder().setUsername("testusername2")
            .setPassword("testpassword")
            .setFirstName("testfirst")
            .setLastName("testlast")
            .setPersonalPronouns("he/him")
            .setEmail("test@email2")
            .build();

    /**
     * Request for test number three
     */
    private UserRegisterRequest testRequest3= UserRegisterRequest.newBuilder().setUsername("testusername3")
            .setPassword("testpassword")
            .setFirstName("testfirst")
            .setLastName("testlast")
            .setPersonalPronouns("he/him")
            .setEmail("test@email3")
            .build();

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        ass.roleRepo = roleRepo;
        ass.repo = repo;
        ass.accountService = as;
        ass.groupRepo = groupRepo;
        ass.groupMembershipRepo = groupMembershipRepo;
    }

    /**
     * Tests an account with existing username. It calls a register request with the mocked observer, checks for completion and sending, then reads the response.
     * @throws Exception
     */
    @Test
    void createAccountWithExistingUsername() throws Exception {
        when(as.getAccountByUsername("testusername1")).thenReturn(new AccountProfile()); // Simulates username already found
        when(as.getAccountByEmail("test@email1")).thenThrow(new Exception("Account profile not found")); // Simulates email not found
        ass.register(testRequest1, testObserver);
        verify(testObserver, times(1)).onCompleted();
        ArgumentCaptor<UserRegisterResponse> captor = ArgumentCaptor.forClass(UserRegisterResponse.class);
        verify(testObserver, times(1)).onNext(captor.capture());
        UserRegisterResponse response = captor.getValue();
        assertEquals(false, response.getIsSuccess());
    }

    /**
     * Tests an account with existing email. It calls a register request with the mocked observer, checks for completion and sending, then reads the response.
     * @throws Exception
     */
    @Test
    void createAccountWithExistingEmail() throws Exception {
        when(as.getAccountByUsername("testusername2")).thenThrow(new Exception("Account profile not found")); // Simulates username not found
        when(as.getAccountByEmail("test@email2")).thenReturn(testAccountProfile); // Simulates email already found
        ass.register(testRequest2, testObserver);
        verify(testObserver, times(1)).onCompleted();
        ArgumentCaptor<UserRegisterResponse> captor = ArgumentCaptor.forClass(UserRegisterResponse.class);
        verify(testObserver, times(1)).onNext(captor.capture());
        UserRegisterResponse response = captor.getValue();
        assertEquals(false, response.getIsSuccess());
    }

    /**
     * Tests an account with valid details. It calls a register request with the mocked observer, checks for completion and sending, then reads the response.
     * @throws Exception
     */
    @Test
    void createAccountWithValidAttributes() throws Exception {
        when(as.getAccountByUsername("testusername3")).thenThrow(new Exception("Account profile not found")); // Simulates username not found
        when(as.getAccountByEmail("test@email3")).thenThrow(new Exception("Account profile not found")); // Simulates email not found

        // mocking default group checking
        Groups teacherGroup = new Groups("Teacher Group", "TG");
        Mockito.when(ass.groupRepo.findAllByGroupShortName("TG"))
            .thenReturn(new ArrayList<>(List.of(teacherGroup)));

        Groups noGroup = new Groups("Members without a group", "MWAG");
        Mockito.when(ass.groupRepo.findAllByGroupShortName("MWAG"))
            .thenReturn(new ArrayList<>(List.of(noGroup)));

        ass.register(testRequest3, testObserver);
        verify(testObserver, times(1)).onCompleted();
        ArgumentCaptor<UserRegisterResponse> captor = ArgumentCaptor.forClass(UserRegisterResponse.class);
        verify(testObserver, times(1)).onNext(captor.capture());
        UserRegisterResponse response = captor.getValue();
        assertTrue(response.getIsSuccess());
    }

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<PaginatedUsersResponse> testPaginatedObserver = mock(StreamObserver.class);

    /**
     * Tests the grpc call to get paginated users
     */
    @Test
    void getPaginatedUsersTest() {
        // set up the profile repo findAll result with a valid account profile
        testProfiles = new ArrayList<>();
        testProfiles.add(testAccountProfile);
        testRoles.add(testRole);
        testAccountProfile.addRoleTestingOnly(testRole);

        // mock the repo response
        when(repo.findAll()).thenReturn(testProfiles);

        // make the request
        GetPaginatedUsersRequest.Builder request = GetPaginatedUsersRequest.newBuilder()
            .setLimit(50)
            .setOffset(0);

        when(as.buildUserResponse(testAccountProfile)).thenReturn(UserResponse.newBuilder().build());

        // run the method we are testing with the mocked observer
        ass.getPaginatedUsers(request.build(), testPaginatedObserver);

        // test we are receiving and processing correctly
        verify(testPaginatedObserver, times(1)).onCompleted();
        ArgumentCaptor<PaginatedUsersResponse> captor = ArgumentCaptor.forClass(PaginatedUsersResponse.class);
        verify(testPaginatedObserver, times(1)).onNext(captor.capture());
        PaginatedUsersResponse response = captor.getValue();

        // make sure we get the user we put in during set up
        assertEquals(1, response.getUsersCount());
    }

    /**
     * Tests the grpc call to get paginated users that don't exist "far down" in the repo
     */
    @Test
    void getPaginatedUsersOutOfRangeTest() {
        // set up the profile repo findAll result with a valid account profile
        testProfiles = new ArrayList<>();
        testProfiles.add(testAccountProfile);
        testRoles.add(testRole);
        testAccountProfile.addRoleTestingOnly(testRole);

        // mock the repo response
        when(repo.findAll()).thenReturn(testProfiles);

        // make the request
        GetPaginatedUsersRequest.Builder request = GetPaginatedUsersRequest.newBuilder()
            .setLimit(50)
            .setOffset(50);

        // run the method we are testing with the mocked observer
        ass.getPaginatedUsers(request.build(), testPaginatedObserver);

        // test we are receiving and processing correctly
        verify(testPaginatedObserver, times(1)).onCompleted();
        ArgumentCaptor<PaginatedUsersResponse> captor = ArgumentCaptor.forClass(PaginatedUsersResponse.class);
        verify(testPaginatedObserver, times(1)).onNext(captor.capture());
        PaginatedUsersResponse response = captor.getValue();


        // make sure we get the user we put in during set up
        assertEquals(0, response.getUsersCount());
    }

    /**
     * Tests the grpc call to get paginated users that do exist "far down" in the repo
     */
    @Test
    void getPaginatedUsersInRangeTest() {
        // set up the profile repo findAll result with valid account profiles
        testProfiles = new ArrayList<>();
        for (int i = 0; i < 51; i++) {
            testProfiles.add(testAccountProfile);
        }
        testRoles.add(testRole);
        testAccountProfile.addRoleTestingOnly(testRole);

        // mock the repo response
        when(repo.findAll()).thenReturn(testProfiles);

        // make the request
        GetPaginatedUsersRequest.Builder request = GetPaginatedUsersRequest.newBuilder()
            .setLimit(50)
            .setOffset(50);

        when(as.buildUserResponse(testAccountProfile)).thenReturn(UserResponse.newBuilder().build());

        // run the method we are testing with the mocked observer
        ass.getPaginatedUsers(request.build(), testPaginatedObserver);

        // test we are receiving and processing correctly
        verify(testPaginatedObserver, times(1)).onCompleted();
        ArgumentCaptor<PaginatedUsersResponse> captor = ArgumentCaptor.forClass(PaginatedUsersResponse.class);
        verify(testPaginatedObserver, times(1)).onNext(captor.capture());
        PaginatedUsersResponse response = captor.getValue();
        assertTrue(response.getUsersCount() >= 0); //TODO make it actually return data in the repo? can this be done?


        // make sure we get the user we put in during set up
        assertEquals(1, response.getUsersCount());
    }

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<UserResponse> testUserResponseObserver = mock(StreamObserver.class);

    /**
     * Tests the grpc call to get a userRepsonse by a userId
     */
    @Test
    void getUserAccountByIdTest() {
        // set up the profile repo findAll result with a valid account profile
        testProfiles = new ArrayList<>();
        testProfiles.add(testAccountProfile);
        testRoles.add(testRole);
        testAccountProfile.addRoleTestingOnly(testRole);

        // mock the repo response
        when(repo.findById(1)).thenReturn(testAccountProfile);

        // make the request
        GetUserByIdRequest.Builder request = GetUserByIdRequest.newBuilder()
            .setId(1);

        when(as.buildUserResponse(testAccountProfile)).thenReturn(UserResponse.newBuilder().setUsername("test username").build());

        // run the method we are testing with the mocked observer
        ass.getUserAccountById(request.build(), testUserResponseObserver);

        // test we are receiving and processing correctly
        verify(testUserResponseObserver, times(1)).onCompleted();
        ArgumentCaptor<UserResponse> captor = ArgumentCaptor.forClass(UserResponse.class);
        verify(testUserResponseObserver, times(1)).onNext(captor.capture());
        UserResponse response = captor.getValue();

        // make sure we get the user we put in during set up
        assertEquals("test username", response.getUsername());
    }

    /**
     * Tests the grpc call to get a user response by a userId
     */
    @Test
    void getUserAccountByIdUserNull() {
        // set up the profile repo findAll result with a valid account profile
        testProfiles = new ArrayList<>();
        testProfiles.add(testAccountProfile);
        testRoles.add(testRole);
        testAccountProfile.addRoleTestingOnly(testRole);

        // mock the repo response
        when(repo.findById(1)).thenReturn(null);

        // make the request
        GetUserByIdRequest.Builder request = GetUserByIdRequest.newBuilder()
            .setId(1);

        // run the method we are testing with the mocked observer
        ass.getUserAccountById(request.build(), testUserResponseObserver);

        // test we are receiving and processing correctly
        verify(testUserResponseObserver, times(1)).onCompleted();
        ArgumentCaptor<UserResponse> captor = ArgumentCaptor.forClass(UserResponse.class);
        verify(testUserResponseObserver, times(0)).onNext(captor.capture());

        // as nothing has been captured it should throw an error that we haven't caught anything
        assertThrows(MockitoException.class, captor::getValue);
    }

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<EditUserResponse> testUserEditObserver = mock(StreamObserver.class);

    /**
     * Tests the grpc call to edit a user using the grpc call to the idp
     */
    @Test
    void editUserTest() {
        // set up the profile repo findAll result with a valid account profile
        testProfiles = new ArrayList<>();
        testProfiles.add(testAccountProfile);
        testRoles.add(testRole);
        testAccountProfile.addRoleTestingOnly(testRole);

        // mock the repo response
        when(repo.findById(1)).thenReturn(testAccountProfile);

        String expected = "test@test.test";

        // make the request
        EditUserRequest.Builder request = EditUserRequest.newBuilder()
            .setUserId(1)
            .setEmail(expected);

        // run the method we are testing with the mocked observer
        ass.editUser(request.build(), testUserEditObserver);

        verify(testUserEditObserver, times(1)).onCompleted();
        ArgumentCaptor<EditUserResponse> captor = ArgumentCaptor.forClass(EditUserResponse.class);
        verify(testUserEditObserver, times(1)).onNext(captor.capture());
        EditUserResponse response = captor.getValue();

        // test that the user email has changed as expected
        assertEquals(expected, testAccountProfile.getEmail());

        // change it back for any other tests
        testAccountProfile.setEmail("test email");
    }

    /**
     * Tests the grpc call to edit a user using the grpc call to the idp
     */
    @Test
    void editUserTestUserNull() {
        // set up the profile repo findAll result with a valid account profile
        testProfiles = new ArrayList<>();
        testProfiles.add(testAccountProfile);
        testRoles.add(testRole);
        testAccountProfile.addRoleTestingOnly(testRole);

        // mock the repo response
        when(repo.findById(1)).thenReturn(null);

        String hopes = "test@test.test";

        // make the request
        EditUserRequest.Builder request = EditUserRequest.newBuilder()
            .setUserId(1)
            .setEmail(hopes);

        // run the method we are testing with the mocked observer
        ass.editUser(request.build(), testUserEditObserver);

        verify(testUserEditObserver, times(1)).onCompleted();
        ArgumentCaptor<EditUserResponse> captor = ArgumentCaptor.forClass(EditUserResponse.class);
        verify(testUserEditObserver, times(0)).onNext(captor.capture());

        // as nothing has been captured it should throw an error that we haven't caught anything
        assertThrows(MockitoException.class, captor::getValue);
    }

    /**
     * The request and response to modify a user role.
     * Set up for testing removeRoleFromUser and addRoleToUser.
     */
    private ModifyRoleOfUserRequest modifyRoleOfUserRequest= ModifyRoleOfUserRequest.newBuilder()
            .setUserId(1)
            .setRole(UserRole.TEACHER)
            .build();
    private ModifyRoleOfUserRequest addTeacherRoleOfUserRequest= ModifyRoleOfUserRequest.newBuilder()
            .setUserId(2)
            .setRole(UserRole.TEACHER)
            .build();
    private StreamObserver<UserRoleChangeResponse> UserRoleChangeResponse = mock(StreamObserver.class);

    /**
     * Given a user that has a role of a teacher, and belonging to the teacher group only.
     * This will test if removing the teacher role will successfully update this removal in the
     * roleRepo, groupRepo, and groupMembershipRepo.
     * The user should not have the role of the teacher, and stop belonging to the teacher group.
     * Instead, it will belong to the Members Without a Group.
     */
    @Test
    void removeRoleFromUserTest() {

        AccountProfile user = new AccountProfile();
        Role studentRole = new Role(user, "1student");
        Role teacherRole = new Role(user, "2teacher");
        List<Role> rolesOfUser = new ArrayList<Role>();
        rolesOfUser.add(studentRole);
        rolesOfUser.add(teacherRole);

        Mockito.when(repo.findById(1)).thenReturn(user);

        Mockito.when(roleRepo.findAllByRegisteredUser(user)).thenReturn(rolesOfUser);

        Groups teacherGroup = new Groups("Teacher Group", "TG");
        Mockito.when(ass.groupRepo.findAllByGroupShortName("TG"))
                .thenReturn(new ArrayList<>(List.of(teacherGroup)));

        Groups noGroup = new Groups("Members without a group", "MWAG");
        Mockito.when(ass.groupRepo.findAllByGroupShortName("MWAG"))
                .thenReturn(new ArrayList<>(List.of(noGroup)));

        ass.removeRoleFromUser(modifyRoleOfUserRequest, UserRoleChangeResponse);

        Mockito.verify(groupMembershipRepo).save(refEq(new GroupMembership(user, noGroup)));
    }

    @Test
    void addRoleToUserTest() {

        AccountProfile user = new AccountProfile();
        Mockito.when(repo.findById(2)).thenReturn(user);

        Role newRoleToAdd = new Role(user, "2teacher");

        Groups teacherGroup = new Groups("Teacher Group", "TG");
        Mockito.when(ass.groupRepo.findAllByGroupShortName("TG"))
                .thenReturn(new ArrayList<>(List.of(teacherGroup)));

        Groups noGroup = new Groups("Members without a group", "MWAG");
        Mockito.when(ass.groupRepo.findAllByGroupShortName("MWAG"))
                .thenReturn(new ArrayList<>(List.of(noGroup)));

        ass.addRoleToUser(addTeacherRoleOfUserRequest, UserRoleChangeResponse);

        Mockito.verify(groupMembershipRepo).save(refEq(new GroupMembership(user, teacherGroup)));

        Mockito.verify(groupMembershipRepo).deleteByRegisteredGroupsAndRegisteredGroupUser(noGroup, user);

        Mockito.verify(roleRepo).save(refEq(newRoleToAdd));


    }

    @Test
    void addRoleToUserTest_ToAddAStudentRole_ExpectStudentRoleAdded() {

        AccountProfile user = new AccountProfile();
        Mockito.when(repo.findById(1)).thenReturn(user);

        Role newStudentRoleToAdd = new Role(user, "1student");
        Mockito.verify(roleRepo).save(refEq(newStudentRoleToAdd));
    }
}

