package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * The GRPC client side service class
 * contains many of the protobuf implementations to allow communication between the idp and portfolio servers
 */
public class AccountClientService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub accountServiceStub;
    private UserAccountServiceGrpc.UserAccountServiceStub photoStub;

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

    /**
     * returns the Change password response based on the given details
     * @param id thhe id of the user to change
     * @param currentPassword the current user password
     * @param newPassword the new user password
     * @return the grpc response ChangePasswordResponse
     */
    public ChangePasswordResponse editPassword(int id, String currentPassword, String newPassword) {
        ChangePasswordRequest.Builder request = ChangePasswordRequest.newBuilder();
        request.setUserId(id)
            .setCurrentPassword(currentPassword)
            .setNewPassword(newPassword);
        return accountServiceStub.changeUserPassword(request.build());
    }

    public ProfilePhotoUploadMetadata createPhotoMetaData(int id, String filepath) {
        ProfilePhotoUploadMetadata metaData = ProfilePhotoUploadMetadata.newBuilder()
        .setUserId(id)
        .setFileType(filepath.split(".")[-1])
        .build();
        return metaData;
    }

    public ByteString photoToBytes(String filepath) throws IOException {
        File image = new File(filepath);
        return ByteString.copyFrom(Files.readAllBytes(image.toPath()));
    }

    public StreamObserver<UploadUserProfilePhotoRequest> uploadUserProfilePhoto(ProfilePhotoUploadMetadata metaData, ByteString fileContent) {
        StreamObserver<UploadUserProfilePhotoRequest> requestObserver = photoStub.uploadUserProfilePhoto(new StreamObserver<FileUploadStatusResponse>() {
            @Override
            public void onNext(FileUploadStatusResponse uploadStatusResponse) {
                System.out.println("Uploading photo section " + uploadStatusResponse.getMessage() + " " + uploadStatusResponse.getSerializedSize());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Upload failed, ERROR: " + Status.fromThrowable(t));
            }

            @Override
            public void onCompleted() {
                System.out.println("Upload complete");
            }
        });
        List<UploadUserProfilePhotoRequest> requests = new ArrayList<UploadUserProfilePhotoRequest>();
        int i;
        for (i=0; i < fileContent.size() / 512; i+= 512) {
            requests.add(UploadUserProfilePhotoRequest.newBuilder().setFileContent(fileContent.substring(i, i+512)).setMetaData(metaData).build());
        }
        for (UploadUserProfilePhotoRequest req : requests) {
            requestObserver.onNext(req);
        }
        requestObserver.onCompleted();
    }
}
