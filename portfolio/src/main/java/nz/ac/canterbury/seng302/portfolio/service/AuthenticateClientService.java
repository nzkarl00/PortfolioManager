package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticationServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateClientService {

    @GrpcClient("identity-provider-grpc-server")
    private AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authenticationStub;

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub accountServiceStub;

    public AuthenticateResponse authenticate(final String username, final String password)  {
        AuthenticateRequest authRequest = AuthenticateRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        return authenticationStub.authenticate(authRequest);
    }

    public AuthState checkAuthState() throws StatusRuntimeException {
        return authenticationStub.checkAuthState(Empty.newBuilder().build());
    }

    public UserRegisterResponse register(String username, String password, String firstName, String lastName, String personalPronouns, String email) {
        UserRegisterRequest registerRequest = UserRegisterRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPersonalPronouns(personalPronouns)
                .setEmail(email)
                .build();
        return accountServiceStub.register(registerRequest);
    }
}
