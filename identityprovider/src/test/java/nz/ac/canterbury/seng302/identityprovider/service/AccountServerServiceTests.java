package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
     * Mocked account service so database checks can be replaced with fixed results
     */
    private static AccountService as = Mockito.mock(AccountService.class);

    /**
     * Mocked stream observer to parse response as a replacement for the portfolio
     */
    private StreamObserver<UserRegisterResponse> testObserver = mock(StreamObserver.class);

    private static AccountProfile testAccountProfile = new AccountProfile();


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
        assertEquals(true, response.getIsSuccess());
    }
}
