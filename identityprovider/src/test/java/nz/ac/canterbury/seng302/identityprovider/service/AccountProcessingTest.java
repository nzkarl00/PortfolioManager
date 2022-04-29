package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.Role;
import nz.ac.canterbury.seng302.identityprovider.model.RolesRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountProcessingTest {

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
     * testing with no roles to search from
     */
    @Test
    void updateUsersSortedTestNull() {
        ArrayList<AccountProfile> users = new ArrayList<>();
        AccountProcessing.updateUsersSorted(users, new ArrayList<>(), ass.repo);
        assertTrue(users.isEmpty());
    }

    @Test
    void updateUsersSortedTest() {
        // set up the profile repo findAll result with a valid account profile
        testProfiles = new ArrayList<>();
        testProfiles.add(testAccountProfile);
        testRoles.add(testRole);
        testAccountProfile.addRoleTestingOnly(testRole);

        // mock the repo response
        when(repo.findById(1)).thenReturn(testAccountProfile);

        ArrayList<AccountProfile> users = new ArrayList<>();
        AccountProcessing.updateUsersSorted(users, new ArrayList<>(), ass.repo);
        assertFalse(users.isEmpty());
        assertEquals(testAccountProfile, users.get(0));
    }
}