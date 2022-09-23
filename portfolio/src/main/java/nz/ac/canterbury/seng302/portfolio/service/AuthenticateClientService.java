package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticationServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateClientService {

    @GrpcClient("identity-provider-grpc-server")
    private AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authenticationStub;

    Logger logger = LoggerFactory.getLogger(AuthenticateClientService.class);

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public AuthenticateResponse authenticate(final String username, final String password)  {
        logger.info("[Authenticate] attempting to authenticate user with IDP, username: " + username.replaceAll("[\n\r\t]", "_"));
        AuthenticateRequest authRequest = AuthenticateRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        AuthenticateResponse authRes = authenticationStub.authenticate(authRequest); // creates the token for the cookie here
        logger.info("[Authenticate] received authentication response from IDP, username: " + username.replaceAll("[\n\r\t]", "_"));
        return authRes;
    }

    public AuthState checkAuthState() throws StatusRuntimeException {
        return authenticationStub.checkAuthState(Empty.newBuilder().build());
    }
}
