package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

public class AccountClientService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub accountServiceStub;

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

    public UserResponse getUserById(int id) {
        GetUserByIdRequest.Builder request = GetUserByIdRequest.newBuilder().setId(id);
        return accountServiceStub.getUserAccountById(request.build());
    }
}
