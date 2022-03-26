package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AccountServerServiceTests {

    @Autowired
    private static AccountServerService ass = new AccountServerService();

    private AccountServerService testService = Mockito.mock(AccountServerService.class);
    @Autowired
    static
    AccountProfileRepository repo = Mockito.mock(AccountProfileRepository.class);

    private static AccountService as = Mockito.mock(AccountService.class);

    private StreamObserver<UserRegisterResponse> testObserver = mock(StreamObserver.class);

    private static AccountProfile testAccountProfile = new AccountProfile();

    private UserRegisterRequest testRequest1= UserRegisterRequest.newBuilder().setUsername("testusername1")
            .setPassword("testpassword")
            .setFirstName("testfirst")
            .setLastName("testlast")
            .setPersonalPronouns("he/him")
            .setEmail("test@email1")
            .build();

    private UserRegisterRequest testRequest2= UserRegisterRequest.newBuilder().setUsername("testusername2")
            .setPassword("testpassword")
            .setFirstName("testfirst")
            .setLastName("testlast")
            .setPersonalPronouns("he/him")
            .setEmail("test@email2")
            .build();

    private UserRegisterRequest testRequest3= UserRegisterRequest.newBuilder().setUsername("testusername3")
            .setPassword("testpassword")
            .setFirstName("testfirst")
            .setLastName("testlast")
            .setPersonalPronouns("he/him")
            .setEmail("test@email3")
            .build();

    @BeforeEach
    void setup() {
        ass.repo = repo;
        ass.accountService = as;
    }

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
