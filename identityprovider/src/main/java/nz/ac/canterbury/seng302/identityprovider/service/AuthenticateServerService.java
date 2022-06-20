package nz.ac.canterbury.seng302.identityprovider.service;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import nz.ac.canterbury.seng302.identityprovider.authentication.AuthenticationServerInterceptor;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.authentication.JwtTokenUtil;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticationServiceGrpc.AuthenticationServiceImplBase;


@GrpcService
public class AuthenticateServerService extends AuthenticationServiceImplBase{

    @Autowired
    Account accountService;
    private JwtTokenUtil jwtTokenService = JwtTokenUtil.getInstance();


    /**
     * Attempts to authenticate a user with a given username and password.
     */
    @Override
    public void authenticate(AuthenticateRequest request, StreamObserver<AuthenticateResponse> responseObserver) {
        AuthenticateResponse.Builder reply = AuthenticateResponse.newBuilder();

        // Get the corresponding user from store
        try {
            AccountProfile profile = accountService.getAccountByUsername(request.getUsername());

            assert request.getUsername().equals(profile.getUsername());

            if (Hasher.verify(request.getPassword(), profile.getPasswordHash())) {
                // token length is 248
                //String token = jwtTokenService.generateTokenForUser(profile.getUsername(), profile.getId(), profile.getUsername(), ROLE_OF_USER);
                String token = jwtTokenService.generateTokenForUser(profile);
                reply
                    .setEmail(profile.getEmail())
                    // TODO: Fetch name
                    .setFirstName("TODO: FETCH NAME")
                    .setLastName("TODO: FETCH NAME")
                    .setMessage("Logged in successfully!")
                    .setSuccess(true)
                    .setToken(token)
                    .setUserId(profile.getId())
                    .setUsername(profile.getUsername());
            } else {
                reply
                    .setMessage("Log in attempt failed: password incorrect")
                    .setSuccess(false)
                    .setToken("");
            }
        } catch (Exception e) {
            reply
                .setMessage("Log in attempt failed: username invalid")
                .setSuccess(false)
                .setToken("");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * The AuthenticationInterceptor already handles validating the authState for us, so here we just need to
     * retrieve that from the current context and return it in the gRPC body
     */
    @Override
    public void checkAuthState(Empty request, StreamObserver<AuthState> responseObserver) {
        responseObserver.onNext(AuthenticationServerInterceptor.AUTH_STATE.get());
        responseObserver.onCompleted();
    }
}


