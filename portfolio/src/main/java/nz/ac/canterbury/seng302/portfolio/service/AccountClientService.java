package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

/**
 * The GRPC client side service class
 * contains many of the protobuf implementations to allow communication between the idp and portfolio servers
 */
public class AccountClientService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub accountServiceStub;

    /**
     * makes a UserRegisterRequest to receive a UserRegisterResponse
     * @param username the user's username
     * @param password the user's plaintext password
     * @param firstName the user's firstname
     * @param lastName the user's lastname
     * @param personalPronouns the user's preferred pronouns
     * @param email the user's email
     * @return the UserRegisterResponse from the request with the params
     */
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
     * @param id the user's account id
     * @param firstName the user's first name
     * @param middleName the user's middle name
     * @param lastName the user's last name
     * @param nickname the user's nickname
     * @param bio the user's self description
     * @param pronouns the user's preferred pronouns
     * @param email the user's email
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

    /**
     * returns a paginated users response from the request details
     * @param limit the max amount of users to get
     * @param offset the starting point to get users from
     * @param orderBy the sorting mode
     * @param orderMode 1 for descending 0 for ascending
     * @return the list of UserResponses in the form of a paginatedUsersResponse
     */
    public PaginatedUsersResponse getPaginatedUsers(int limit, int offset, String orderBy, int orderMode) {
        GetPaginatedUsersRequest.Builder request = GetPaginatedUsersRequest.newBuilder();
        String order = orderBy;
        if (orderMode == 1) {
            order += "_desc";
        } else {
            order += "_asc";
        }
        request.setLimit(limit)
                .setOffset(offset)
                .setOrderBy(order);
        return accountServiceStub.getPaginatedUsers(request.build());
    }
}
