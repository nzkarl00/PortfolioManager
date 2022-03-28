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

    /**
     * Get a user's details from the id of the user
     * @param id
     * @return the user's details
     */
    public UserResponse getUserById(int id) {
        GetUserByIdRequest.Builder request = GetUserByIdRequest.newBuilder().setId(id);
        return accountServiceStub.getUserAccountById(request.build());
    }

    /**
     * Passes an edit user request to the IDP
     * @param id
     * @param firstName
     * @param middleName
     * @param lastName
     * @param nickname
     * @param bio
     * @param pronouns
     * @param email
     * @return the response from the IDP
     */
    public EditUserResponse editUser(int id, String firstName, String middleName, String lastName, String nickname, String bio, String pronouns, String email) {
        EditUserRequest.Builder request = EditUserRequest.newBuilder();
        request.setUserId(id);
        // as all of these fields are optional we have if's for null or empty values
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
