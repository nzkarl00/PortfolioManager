package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * The GRPC client side service class
 * contains many of the protobuf implementations to allow communication between the idp and portfolio servers
 */
public class AccountClientService
        extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @GrpcClient("identity-provider-grpc-server")
    UserAccountServiceGrpc.UserAccountServiceBlockingStub accountServiceStub;

    Logger logger = LoggerFactory.getLogger(AccountClientService.class);

    /**
     * makes a UserRegisterRequest to receive a UserRegisterResponse
     *
     * @param username         the user's username
     * @param password         the user's plaintext password
     * @param firstName        the user's firstname
     * @param lastName         the user's lastname
     * @param personalPronouns the user's preferred pronouns
     * @param email            the user's email
     * @return the UserRegisterResponse from the request with the params
     */
    public UserRegisterResponse register(String username, String password,
                                         String firstName, String lastName,
                                         String personalPronouns,
                                         String email) {
        logger.info("[REGISTER] Attempting to register user with IDP, username: " + username.replaceAll("[\n\r\t]", "_"));
        UserRegisterRequest registerRequest = UserRegisterRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPersonalPronouns(personalPronouns)
                .setEmail(email)
                .build();
        UserRegisterResponse res = accountServiceStub.register(registerRequest);
        logger.info("[REGISTER] Received register response for user from IDP, username: " + username.replaceAll("[\n\r\t]", "_"));
        return res;
    }

    /**
     * Get a user's details from the id of the user
     *
     * @param id
     * @return the user's details
     */
    public UserResponse getUserById(int id) {
        GetUserByIdRequest.Builder request =
                GetUserByIdRequest.newBuilder().setId(id);
        return accountServiceStub.getUserAccountById(request.build());
    }

    /**
     * Passes an edit user request to the IDP
     *
     * @param id         the user's account id
     * @param firstName  the user's first name
     * @param middleName the user's middle name
     * @param lastName   the user's last name
     * @param nickname   the user's nickname
     * @param bio        the user's self description
     * @param pronouns   the user's preferred pronouns
     * @param email      the user's email
     * @return the response from the IDP
     */
    public EditUserResponse editUser(int id, String firstName,
                                     String middleName, String lastName,
                                     String nickname, String bio,
                                     String pronouns, String email) {
        EditUserRequest.Builder request = EditUserRequest.newBuilder();
        request.setUserId(id);
        // as all of these fields are optional we have if's for null or empty values
        if (!firstName.isEmpty()) {
            request.setFirstName(firstName);
        }
        if (!middleName.isEmpty()) {
            request.setMiddleName(middleName);
        }
        if (!lastName.isEmpty()) {
            request.setLastName(lastName);
        }
        if (!nickname.isEmpty()) {
            request.setNickname(nickname);
        }
        if (!bio.isEmpty()) {
            request.setBio(bio);
        }
        if (!pronouns.isEmpty()) {
            request.setPersonalPronouns(pronouns);
        }
        if (!email.isEmpty()) {
            request.setEmail(email);
        }
        return accountServiceStub.editUser(request.build());
    }

    /**
     * returns a paginated users response from the request details
     *
     * @param limit     the max amount of users to get
     * @param offset    the starting point to get users from
     * @param orderBy   the sorting mode
     * @param orderMode 1 for descending 0 for ascending
     * @return the list of UserResponses in the form of a paginatedUsersResponse
     */
    public PaginatedUsersResponse getPaginatedUsers(int limit, int offset,
                                                    String orderBy,
                                                    int orderMode) {
        GetPaginatedUsersRequest.Builder request =
                GetPaginatedUsersRequest.newBuilder();
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

    /**
     * @return A hashmap of every user in the system, from their ID to their username
     */
    public HashMap<String, Integer> getUsernameMap() {
        HashMap<String, Integer> usernameMap = new HashMap<String, Integer>();
        //Keeps track of how many users have been retrieved from the database
        int retrievedMembers = 0;
        //Keeps track of how many users request to the database
        int expectedMembers = 0;
        //Stop the loop when no more members can be retrieved
        PaginatedUsersResponse response = getPaginatedUsers(10, 0, "username", 1);
        for(int page = 0; page < response.getResultSetSize(); page++) {
            for (UserResponse userResponse : response.getUsersList()) {
                usernameMap.put(userResponse.getUsername(), userResponse.getId());
            }
            response = getPaginatedUsers(10, page * 10, "username", 1);
        }
        return usernameMap;
    }

    /**
     * returns the Change password response based on the given details
     *
     * @param id              thhe id of the user to change
     * @param currentPassword the current user password
     * @param newPassword     the new user password
     * @return the grpc response ChangePasswordResponse
     */
    public ChangePasswordResponse editPassword(int id, String currentPassword,
                                               String newPassword) {
        ChangePasswordRequest.Builder request =
                ChangePasswordRequest.newBuilder();
        request.setUserId(id)
                .setCurrentPassword(currentPassword)
                .setNewPassword(newPassword);
        return accountServiceStub.changeUserPassword(request.build());
    }

    public UserRoleChangeResponse deleteRole(String role, Integer userId) {
        ModifyRoleOfUserRequest.Builder request =
                ModifyRoleOfUserRequest.newBuilder();
        request.setUserId(userId);
        UserRole roleSending;
        if (role.equals("student")) {
            roleSending = UserRole.STUDENT;
        } else if (role.equals("teacher")) {
            roleSending = UserRole.TEACHER;
        } else if (role.equals("course_administrator")) {
            roleSending = UserRole.COURSE_ADMINISTRATOR;
        } else {
            roleSending = UserRole.STUDENT;
        }

        request.setRole(roleSending);

        return accountServiceStub.removeRoleFromUser(request.build());
    }

    public UserRoleChangeResponse addRole(String role, Integer userId) {
        ModifyRoleOfUserRequest.Builder request =
                ModifyRoleOfUserRequest.newBuilder();
        request.setUserId(userId);
        UserRole roleSending;
        if (role.equals("student")) {
            roleSending = UserRole.STUDENT;
        } else if (role.equals("teacher")) {
            roleSending = UserRole.TEACHER;
        } else if (role.equals("course_administrator")) {
            roleSending = UserRole.COURSE_ADMINISTRATOR;
        } else {
            roleSending = UserRole.STUDENT;
        }

        request.setRole(roleSending);
        return accountServiceStub.addRoleToUser(request.build());
    }


    public DeleteUserProfilePhotoResponse deleteUserProfilePhoto(int id) {
        DeleteUserProfilePhotoRequest.Builder request =
                DeleteUserProfilePhotoRequest.newBuilder()
                        .setUserId(id);
        return accountServiceStub.deleteUserProfilePhoto(request.build());
    }
}
