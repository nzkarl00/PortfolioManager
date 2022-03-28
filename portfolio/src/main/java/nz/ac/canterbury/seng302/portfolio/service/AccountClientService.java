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

    public EditUserResponse editUser(int id, String firstName, String middleName, String lastName, String nickname, String bio, String pronouns, String email) {
        EditUserRequest.Builder request = EditUserRequest.newBuilder();
        request.setUserId(id);
        if (!firstName.isEmpty()) { request.setFirstName(firstName); }
        if (!middleName.isEmpty()) { request.setMiddleName(middleName); }
        if (!lastName.isEmpty()) { request.setLastName(lastName); }
        if (!nickname.isEmpty()) { request.setNickname(nickname); }
        if (!bio.isEmpty()) { request.setBio(bio); }
        if (!pronouns.isEmpty()) { request.setPersonalPronouns(pronouns); }
        if (!email.isEmpty()) { request.setEmail(email); }
        return accountServiceStub.editUser(request.build());
    }
}
