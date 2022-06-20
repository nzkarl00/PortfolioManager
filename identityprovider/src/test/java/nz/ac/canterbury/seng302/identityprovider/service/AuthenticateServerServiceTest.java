package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.authentication.JwtTokenUtil;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.Role;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticateServerServiceTest {

    /**
     * The authenticate server service we are testing in this class
     */
    @Autowired
    private static AuthenticateServerService ass = new AuthenticateServerService();

    /**
     * Mocked account service so database checks can be replaced with fixed results
     */
    @Autowired
    private static Account as = Mockito.mock(Account.class);

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        ass.accountService = as;
    }

    // setting up and closing the mocked static authStateInformer
    static MockedStatic<Hasher> utilities;
    static MockedStatic<JwtTokenUtil> tokenUtil;

    @BeforeAll
    public static void open() {
        utilities = Mockito.mockStatic(Hasher.class);
        tokenUtil = Mockito.mockStatic(JwtTokenUtil.class);
    }

    @AfterAll
    public static void close() {
        utilities.close();
        tokenUtil.close();
    }

    private static AccountProfile testAccountProfile = new AccountProfile("test username",
        "test hash", new Date(), "test bio", "test email",
        "test/photopath/", "firstname", "lastname", "pronouns");

    // setup test role and a list of said roles to mock the repo with
    private static Role testRole = new Role(testAccountProfile, "1student");

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<AuthenticateResponse> authenticateResponseObserver = mock(StreamObserver.class);

    /**
     * test the Authenticate server service correctly logs in the user with the
     * given information
     * @throws Exception from the .authenticate() call
     */
    @Test
    void authenticate_validLoginInfo() throws Exception {
        testAccountProfile.addRoleTestingOnly(testRole);

        AuthenticateRequest request = AuthenticateRequest.newBuilder()
            .setUsername("test username")
            .setPassword("testPassword").build();
        when(as.getAccountByUsername("test username")).thenReturn(testAccountProfile);

        utilities.when(() -> Hasher.verify("testPassword", "test hash")).thenReturn(true);

        ass.authenticate(request, authenticateResponseObserver);

        verify(authenticateResponseObserver, times(1)).onCompleted();
        ArgumentCaptor<AuthenticateResponse> captor = ArgumentCaptor.forClass(AuthenticateResponse.class);
        verify(authenticateResponseObserver, times(1)).onNext(captor.capture());
        AuthenticateResponse response = captor.getValue();
        assertTrue(response.getSuccess());
        assertFalse(response.getToken().isEmpty());
    }

    /**
     * tests the authenticate service that cannot find a user and therefore
     * cannot log the user in and return the appropriate message
     * @throws Exception
     */
    @Test
    void authenticate_invalidUsername() throws Exception {
        testAccountProfile.addRoleTestingOnly(testRole);

        AuthenticateRequest request = AuthenticateRequest.newBuilder()
            .setUsername("not correct username")
            .setPassword("testPassword").build();
        when(as.getAccountByUsername("test username")).thenReturn(testAccountProfile);

        utilities.when(() -> Hasher.verify("testPassword", "test hash")).thenReturn(true);

        ass.authenticate(request, authenticateResponseObserver);

        verify(authenticateResponseObserver, times(1)).onCompleted();
        ArgumentCaptor<AuthenticateResponse> captor = ArgumentCaptor.forClass(AuthenticateResponse.class);
        verify(authenticateResponseObserver, times(1)).onNext(captor.capture());
        AuthenticateResponse response = captor.getValue();
        assertFalse(response.getSuccess());
        assertEquals("Log in attempt failed: username invalid", response.getMessage());
    }

    /**
     * tests the authenticate service that doesn't match the user hash and therefore
     * cannot log the user in and return the appropriate message
     * @throws Exception
     */
    @Test
    void authenticate_invalidPassword() throws Exception {
        testAccountProfile.addRoleTestingOnly(testRole);

        AuthenticateRequest request = AuthenticateRequest.newBuilder()
            .setUsername("test username")
            .setPassword("testPassword").build();
        when(as.getAccountByUsername("test username")).thenReturn(testAccountProfile);

        utilities.when(() -> Hasher.verify("testPassword", "test hash")).thenReturn(false);

        ass.authenticate(request, authenticateResponseObserver);

        verify(authenticateResponseObserver, times(1)).onCompleted();
        ArgumentCaptor<AuthenticateResponse> captor = ArgumentCaptor.forClass(AuthenticateResponse.class);
        verify(authenticateResponseObserver, times(1)).onNext(captor.capture());
        AuthenticateResponse response = captor.getValue();
        assertFalse(response.getSuccess());
        assertEquals("Log in attempt failed: password incorrect", response.getMessage());
    }
}