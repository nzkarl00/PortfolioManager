package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.Role;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AccountTest {

    @Mock
    static AccountProfileRepository repository;

    @InjectMocks
    private static Account account = new Account();

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    AccountProfile testAccount = new AccountProfile("String username", "String passwordHash",
        new Date(), "String bio", "String email", "DEFAULT",
        "String firstName", "String lastName", "String pronouns");

    @Test
    public void getAllAccounts_blueSky() {
        List<AccountProfile> expected = new ArrayList<>(List.of(testAccount));
        when(repository.findAll()).thenReturn(expected);
        assertEquals(expected, account.getAllAccounts());
    }

    @Test
    public void getAccountById_blueSky() throws Exception {
        when(repository.findById(1)).thenReturn(testAccount);
        assertEquals(testAccount, account.getAccountById(1));
    }

    @Test
    public void getAccountById_fails() {
        assertThrows(Exception.class, () -> {
            account.getAccountById(1);
        });
    }

    @Test
    public void getAccountByEmail_blueSky() throws Exception {
        when(repository.findByEmail("email@email")).thenReturn(testAccount);
        assertEquals(testAccount, account.getAccountByEmail("email@email"));
    }

    @Test
    public void getAccountByEmail_fails() {
        assertThrows(Exception.class, () -> {
            account.getAccountByEmail("email@email");
        });
    }

    @Test
    public void getAccountByUsername_blueSky() throws Exception {
        when(repository.findByUsername("username")).thenReturn(testAccount);
        assertEquals(testAccount, account.getAccountByUsername("username"));
    }

    @Test
    public void getAccountByUsername_fails() {
        assertThrows(Exception.class, () -> {
            account.getAccountByUsername("username");
        });
    }

    // setup test account profile and a list of said profiles to mock the repo with
    private static Date now = new Date();
    private static AccountProfile testAccountProfile = new AccountProfile("test username",
        "test hash", now, "test bio", "test email",
        "test/photopath/", "firstname", "lastname", "pronouns");
    private static ArrayList<AccountProfile> testProfiles = new ArrayList<>();

    // setup test role and a list of said roles to mock the repo with
    private static Role testRole = new Role(testAccountProfile, "1student");
    private static ArrayList<Role> testRoles = new ArrayList<>();

    /**
     * testing with no roles to search from
     */
    @Test
    void updateUsersSortedTestNull() {
        ArrayList<AccountProfile> users = new ArrayList<>();
        AccountProcessing.updateUsersSorted(users, new ArrayList<>(), repository);
        assertTrue(users.isEmpty());
    }

    @Test
    void updateUsersSortedTest() {
        // set up the profile repo findAll result with a valid account profile
        testProfiles = new ArrayList<>();
        testProfiles.add(testAccountProfile);
        testRole.setUserRoleId(1L);
        testRoles.add(testRole);
        testAccountProfile.addRoleTestingOnly(testRole);

        // mock the repo response
        when(repository.findById(1)).thenReturn(testAccountProfile);

        ArrayList<AccountProfile> users = new ArrayList<>();

        AccountProcessing.updateUsersSorted(users, testRoles, repository);
        assertFalse(users.isEmpty());
        assertEquals(testAccountProfile, users.get(0));
    }

    @Test
    void buildUserResponse_blueSky() {
        UserResponse expected = UserResponse.newBuilder()
            .setUsername("String username")
            .setFirstName("String firstName")
            .setLastName("String lastName")
            .setBio("String bio")
            .setPersonalPronouns("String pronouns")
            .setEmail("String email")
            .setCreated(Timestamp.newBuilder().setSeconds(now.getTime()/1000 + 1).build())
            .setProfileImagePath("DEFAULT")
            .addRoles(UserRole.TEACHER).build();
        testAccount.addRoleTestingOnly(new Role(testAccount, "2teacher"));
        UserResponse actual = account.buildUserResponse(testAccount);
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getBio(), actual.getBio());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getProfileImagePath(), actual.getProfileImagePath());
        assertEquals(expected.getNickname(), actual.getNickname());
    }
}