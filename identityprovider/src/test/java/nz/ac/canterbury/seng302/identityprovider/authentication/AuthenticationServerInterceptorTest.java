package nz.ac.canterbury.seng302.identityprovider.authentication;

import io.cucumber.messages.types.Meta;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.Role;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServerInterceptorTest {

    ServerCall<Object, Object> call = new ServerCall<>() {
        @Override
        public void request(int numMessages) {

        }

        @Override
        public void sendHeaders(Metadata headers) {

        }

        @Override
        public void sendMessage(Object message) {

        }

        @Override
        public void close(Status status, Metadata trailers) {

        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public MethodDescriptor<Object, Object> getMethodDescriptor() {
            return null;
        }
    };

    Metadata metadata = new Metadata();

    ServerCallHandler<Object, Object> next = (call, headers) -> null;

    private static AccountProfile testAccountProfile = new AccountProfile("test username",
        "test hash", new Date(), "test bio", "test email",
        "test/photopath/", "firstname", "lastname", "pronouns");

    private static Role testRole = new Role(testAccountProfile, "1student");

    /**
     * a glorious exercise in mocking... but not much mocking
     */
    @Test
    void interceptCall_blueSky() {
        testAccountProfile.addRoleTestingOnly(testRole);
        JwtTokenUtil jwtTokenUtil = JwtTokenUtil.getInstance();
        String token = jwtTokenUtil.generateTokenForUser(testAccountProfile);
        metadata.put(Metadata.Key.of("X-Authorization", Metadata.ASCII_STRING_MARSHALLER), token);

        AuthenticationServerInterceptor interceptor = new AuthenticationServerInterceptor();
        interceptor.interceptCall(call, metadata, next);
    }

}