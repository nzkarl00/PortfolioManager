package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.Role;
import nz.ac.canterbury.seng302.identityprovider.model.RolesRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    /**
     * Mocked account service so database checks can be replaced with fixed results
     */
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
        testAccountProfile.addRole(testRole);

        // mock the repo response
        when(repo.findAll()).thenReturn(testProfiles);

        // make the request
        GetPaginatedUsersRequest.Builder request = GetPaginatedUsersRequest.newBuilder()
            .setLimit(50)
            .setOffset(0);
        
        // run the method we are testing with the mocked observer
        ass.getPaginatedUsers(request.build(), testPaginatedObserver);

        // test we are receiving and processing correctly
        verify(testPaginatedObserver, times(1)).onCompleted();
        ArgumentCaptor<PaginatedUsersResponse> captor = ArgumentCaptor.forClass(PaginatedUsersResponse.class);
        verify(testPaginatedObserver, times(1)).onNext(captor.capture());
        PaginatedUsersResponse response = captor.getValue();

        // for debugging
        for (UserResponse userResponse : response.getUsersList()) {
            System.out.println(userResponse);
        }

        // make sure we get the user we put in during set up
        assertEquals(1, response.getUsersCount());
    }

    /**
     * Tests the grpc call to get paginated users that don't exist that "far down" in the repo
     */
    @Test
    void getPaginatedUsersOutOfRangeTest() {
        // set up the profile repo findAll result with a valid account profile
        testProfiles = new ArrayList<>();
        testProfiles.add(testAccountProfile);
        testRoles.add(testRole);
        testAccountProfile.addRole(testRole);

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

        // for debugging
        for (UserResponse userResponse : response.getUsersList()) {
            System.out.println(userResponse);
        }

        // make sure we get the user we put in during set up
        assertEquals(0, response.getUsersCount());
    }

    /**
     * Tests the grpc call to get paginated users that do exist that "far down" in the repo
     */
    @Test
    void getPaginatedUsersInRangeTest() {
        // set up the profile repo findAll result with valid account profiles
        testProfiles = new ArrayList<>();
        for (int i = 0; i < 51; i++) {
            testProfiles.add(testAccountProfile);
        }
        testRoles.add(testRole);
        testAccountProfile.addRole(testRole);

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

        // for debugging
        for (UserResponse userResponse : response.getUsersList()) {
            System.out.println(userResponse);
        }

        // make sure we get the user we put in during set up
        assertEquals(1, response.getUsersCount());
    }
}
