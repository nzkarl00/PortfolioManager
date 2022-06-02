package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.Role;
import nz.ac.canterbury.seng302.identityprovider.model.RolesRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.hibernate.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.util.FileSystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * The GRPC server side service class
 * contains many of the protobuf implementations to allow communication between the idp and portfolio servers
 */
@GrpcService
public class AccountServerService extends UserAccountServiceImplBase{

    @Autowired
    Account accountService;

    @Autowired
    AccountProfileRepository repo;

    @Autowired
    RolesRepository roleRepo;

    @Autowired
    private FileSystemUtils fsUtils;


    /**
     * the handling and registering of a new user through a UserRegisterRequest
     * @param request the request with user details
     * @param responseObserver the place to send the response back to
     */
    @Override
    @Transactional
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();
        if (usernameExists(request.getUsername())) {
            reply.setIsSuccess(false).setMessage("Registration failed, username already exists");
        } else if (emailExists(request.getEmail())) {
            reply.setIsSuccess(false).setMessage("Registration failed, email already exists");
        } else {
            // Hash the password
            String hashedPassword = Hasher.hashPassword(request.getPassword());
            AccountProfile newAccount = repo.save(
                    new AccountProfile(
                            request.getUsername(), hashedPassword, new Date(), "", request.getEmail(),
                            null, request.getFirstName(), request.getLastName(), request.getPersonalPronouns()));
            roleRepo.save(new Role(newAccount, "1student")); // TODO change this from the default
            reply.setMessage("Created account " + request.getUsername()).setIsSuccess(true);
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     *
     * @param username to be checked
     * @return true if the username exists in the system, false if it does not
     */
    public boolean usernameExists(String username) {
        try {
            accountService.getAccountByUsername(username);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /**
     *
     * @param email to be checked
     * @return true if the email exists in the system, false if it does not
     */
    public boolean emailExists(String email) {
        try {
            accountService.getAccountByEmail(email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Send back the user details associated with the user id
     * @param request the request containing the userid
     * @param responseObserver where to send the response back to
     */
    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        AccountProfile profile = repo.findById(request.getId());
        if (!(profile == null)) {
            UserResponse reply = buildUserResponse(profile);
            responseObserver.onNext(reply);
        }
        responseObserver.onCompleted();
    }

    /**
     * handle the recieveing and editing of a user based on a EditUserRequest protobuf
     * @param request the EditUserRequest
     * @param responseObserver the place to send the message back
     */
    @Override
    public void editUser(EditUserRequest request, StreamObserver<EditUserResponse> responseObserver) {
        EditUserResponse.Builder reply = EditUserResponse.newBuilder();
        AccountProfile profile = repo.findById(request.getUserId());
        if (!(profile == null)) {
            if (!request.getEmail().isEmpty()) { profile.setEmail(request.getEmail()); }
            if (!request.getBio().isEmpty()) { profile.setBio(request.getBio()); }
            if (!request.getLastName().isEmpty()) { profile.setLastName(request.getLastName()); }
            if (!request.getFirstName().isEmpty()) { profile.setFirstName(request.getFirstName()); }
            if (!request.getMiddleName().isEmpty()) { profile.setMiddleName(request.getMiddleName()); }
            if (!request.getNickname().isEmpty()) { profile.setNickname(request.getNickname()); }
            if (!request.getPersonalPronouns().isEmpty()) { profile.setPronouns(request.getPersonalPronouns()); }
            repo.save(profile);
            reply.setIsSuccess(true)
                .setMessage("We edited somme s***t, idk lol");
            responseObserver.onNext(reply.build());
        }
        responseObserver.onCompleted();
    }

    /**
     * A builder for a UserResponse from a repo profile
     * @param profile the profile to build the protobuf from
     * @return the final protobuf to represent the profile given
     */
    private UserResponse buildUserResponse(AccountProfile profile) {
        UserResponse.Builder reply = UserResponse.newBuilder();
        reply
            .setUsername(profile.getUsername())
            .setFirstName(profile.getFirstName())
            .setMiddleName(profile.getMiddleName())
            .setLastName(profile.getLastName())
            .setNickname(profile.getNickname())
            .setBio(profile.getBio())
            .setId(profile.getId())
            .setPersonalPronouns(profile.getPronouns())
            .setEmail(profile.getEmail())
            .setCreated(Timestamp.newBuilder().setSeconds(profile.getRegisterDate().getTime()/1000).build())
            .setProfileImagePath(profile.getPhotoPath());

        for (Role role : profile.getRoles()) {
            if (role.getRole().equals("1student")) { reply.addRoles(UserRole.STUDENT); } // Note the {number}{role} structure is due to sorting to allow for the highest priority roles to be shown
            if (role.getRole().equals("2teacher")) { reply.addRoles(UserRole.TEACHER); }
            if (role.getRole().equals("3admin")) { reply.addRoles(UserRole.COURSE_ADMINISTRATOR); }
        }

        return reply.build();
    }

    /**
     * Updates the usersSorted list with the correct users in the order given by the sorted roles query
     * @param usersAll the list to reference
     * @param usersSorted the list to update
     * @param ascDesc the list to if we're sorting by lowest or highest role
     */
    public void updateUsersSorted(List<AccountProfile> usersAll, List<AccountProfile> usersSorted, Boolean ascDesc) {
        ArrayList<AccountProfile> userAdmins = new ArrayList<>();
        ArrayList<AccountProfile> userTeachers = new ArrayList<>();
        ArrayList<AccountProfile> userStudents = new ArrayList<>();

        for (AccountProfile profile: usersAll) {
            Role currentRole = null;
            List<Role> usersRoles = roleRepo.findAllByRegisteredUser(profile);

            for (Role role: usersRoles) {
                if (currentRole == null) {
                    currentRole = role;
                } else {
                    if (currentRole.getRole().equals("3admin")) {
                    } else if ((currentRole.getRole().equals("2teacher")) && (role.getRole().equals("3admin"))) {
                            currentRole = role;
                    } else {
                        currentRole = role;
                    }
                }
            }

            if (currentRole.getRole().equals("3admin")) {
                userAdmins.add(profile);
            }
            if (currentRole.getRole().equals("2teacher")) {
                userTeachers.add(profile);
            }
            if (currentRole.getRole().equals("1student")) {
                userStudents.add(profile);
            }

        }

        if (ascDesc) {
            for (AccountProfile profile: userAdmins) {
                usersSorted.add(profile);
            }
            for (AccountProfile profile: userTeachers) {
                usersSorted.add(profile);
            }
            for (AccountProfile profile: userStudents) {
                usersSorted.add(profile);
            }
        } else {
            for (AccountProfile profile: userStudents) {
                usersSorted.add(profile);
            }
            for (AccountProfile profile: userTeachers) {
                usersSorted.add(profile);
            }
            for (AccountProfile profile: userAdmins) {
                usersSorted.add(profile);
            }
        }

    }

    /**
     * Send back the all the user details
     * @param request the GetPaginatedUsersRequest
     * @param responseObserver the place to send the message back
     */
    @Override
    public void getPaginatedUsers(GetPaginatedUsersRequest request, StreamObserver<PaginatedUsersResponse> responseObserver) {
        int limit = request.getLimit() + request.getOffset();

        PaginatedUsersResponse.Builder reply = PaginatedUsersResponse.newBuilder();
        List<AccountProfile> usersSorted = new ArrayList<>();

        Boolean isSorted = false;

        if (request.getOrderBy().equals("roles_asc")) {
            updateUsersSorted(sortUsers(request), usersSorted, true);

        } else if (request.getOrderBy().equals("roles_desc")) {
            updateUsersSorted(sortUsers(request), usersSorted, false);

        } else {
            usersSorted = sortUsers(request);
        }

        int i = request.getOffset();
        while (i < limit && i < usersSorted.size()) {
            reply.addUsers(buildUserResponse(usersSorted.get(i)));
            i++;
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * returns the correct sorting of users based on the GRPC request
     * @param request the GRPC request
     * @return the list of account profiles sorted as to the grpc request
     */
    public List<AccountProfile> sortUsers(GetPaginatedUsersRequest request) {
        switch (request.getOrderBy()) {
            case "first_name_asc":
                return repo.findAllByOrderByFirstNameAsc();
            case "first_name_desc":
                return repo.findAllByOrderByFirstNameDesc();
            case "last_name_asc":
                return repo.findAllByOrderByLastNameAsc();
            case "last_name_desc":
                return repo.findAllByOrderByLastNameDesc();
            case "nickname_asc":
                return repo.findAllByOrderByNicknameAsc();
            case "nickname_desc":
                return repo.findAllByOrderByNicknameDesc();
            case "username_asc":
                return repo.findAllByOrderByUsernameAsc();
            case "username_desc":
                return repo.findAllByOrderByUsernameDesc();
            default:
                return repo.findAll();
        }
    }

    /**
     * Change the user's password specified by the request if the details are appropriate
     * @param request the grpc request containing the change details
     * @param observer the observer to send the response to
     */
    @Override
    public void changeUserPassword(ChangePasswordRequest request, StreamObserver<ChangePasswordResponse> observer) {
        ChangePasswordResponse.Builder response = ChangePasswordResponse.newBuilder();
        try {
            AccountProfile profile = accountService.getAccountById(request.getUserId());
            if (Hasher.verify(request.getCurrentPassword(), profile.getPasswordHash())) {
                profile.setPasswordHash(Hasher.hashPassword(request.getNewPassword()));
                repo.save(profile);
                response.setIsSuccess(true)
                    .setMessage("Password changed");
            } else {
                response.setMessage("Password change failed: current password incorrect")
                    .setIsSuccess(false);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage())
                .setIsSuccess(false);
        }
        observer.onNext(response.build());
        observer.onCompleted();
    }
    @Override
    public void removeRoleFromUser(ModifyRoleOfUserRequest request, StreamObserver<UserRoleChangeResponse> responseObserver) {
        AccountProfile user = repo.findById(request.getUserId());
        UserRoleChangeResponse.Builder reply = UserRoleChangeResponse.newBuilder();

        System.out.println(request);
        String roleString;
        switch (request.getRole()) {
            case TEACHER:
                roleString = "2teacher";
                break;
            case COURSE_ADMINISTRATOR:
                roleString = "3admin";
                break;
            default:
                roleString = "1student";
                break;

        }

        Long roleId = null;

        List<Role> roles = roleRepo.findAllByRegisteredUser(user);
        for (Role role: roles) {
            if (role.getRole().equals(roleString)) {
                roleId = role.getUserRoleId();
                roleRepo.deleteById(roleId);
            }
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();

    }

    @Override
    public void addRoleToUser(ModifyRoleOfUserRequest request, StreamObserver<UserRoleChangeResponse> responseObserver) {
        AccountProfile user = repo.findById(request.getUserId());
        UserRoleChangeResponse.Builder reply = UserRoleChangeResponse.newBuilder();
        String role;
        switch (request.getRole()) {
            case TEACHER:
                role = "2teacher";
                break;
            case COURSE_ADMINISTRATOR:
                role = "3admin";
                break;
            default:
                role = "1student";
                break;
        }

        Role roleForRepo = new Role(user, role);
        roleRepo.save(roleForRepo);

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<UploadUserProfilePhotoRequest> uploadUserProfilePhoto(StreamObserver<FileUploadStatusResponse> responseObserver) {
        return new StreamObserver<UploadUserProfilePhotoRequest>() {
            private Integer userId;
            private String fileType;
            private Path imagePath;

            @Override
            public void onNext(UploadUserProfilePhotoRequest uploadRequest) {
                // if there is meta-data update the variables, if not update the file
                if (!uploadRequest.getMetaData().getFileType().isEmpty()) {
                    // set up map variables based on metadata
                    userId = uploadRequest.getMetaData().getUserId();
                    fileType = uploadRequest.getMetaData().getFileType();
                    imagePath = fsUtils.userProfilePhotoAbsolutePath(userId, fileType);

                    try {
                        // Delete the file if it already exists.
                        Files.deleteIfExists(imagePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        // if the file doesn't exist, make it, and it's path
                        if (Files.notExists(imagePath)) {
                            Files.createDirectories(imagePath.getParent());
                            Files.createFile(imagePath);
                        }
                        // write the bytes fed from the portfolio to the file location
                        Files.write(imagePath, uploadRequest.getFileContent().toByteArray(), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                FileUploadStatusResponse.Builder response = FileUploadStatusResponse.newBuilder();
                response.setMessage("Uploading")
                    .setStatus(FileUploadStatus.PENDING);
                responseObserver.onNext(response.build());
            }

            @Override
            public void onError(Throwable t) {
                FileUploadStatusResponse.Builder response = FileUploadStatusResponse.newBuilder();
                response.setMessage("Upload failed, ERROR: " + Status.fromThrowable(t))
                    .setStatus(FileUploadStatus.FAILED);
                responseObserver.onNext(response.build());
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {

                // Save the new file path to user repo
                AccountProfile profile = repo.findById(userId);
                if (!(profile == null)) {
                    profile.setPhotoPath(fsUtils.userProfilePhotoRelativePath(userId, fileType).toString());
                    repo.save(profile);
                }

                FileUploadStatusResponse.Builder response = FileUploadStatusResponse.newBuilder();
                response.setMessage("Upload complete")
                    .setStatus(FileUploadStatus.SUCCESS);
                responseObserver.onNext(response.build());
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * responsible for deleting the user profile photo if the user requests it
     * @param request the id of the user photo to delete
     * @param responseObserver where to send the message about the deletion
     */
    @Override
    public void deleteUserProfilePhoto(DeleteUserProfilePhotoRequest request, StreamObserver<DeleteUserProfilePhotoResponse> responseObserver) {
        AccountProfile profile = repo.findById(request.getUserId());
        DeleteUserProfilePhotoResponse.Builder response = DeleteUserProfilePhotoResponse.newBuilder();
        try {
            Files.deleteIfExists(fsUtils.resolveRelativeProfilePhotoPath(Paths.get(profile.getPhotoPath())));
            profile.setPhotoPath(null);
            repo.save(profile);
            response.setIsSuccess(true);
            response.setMessage("File deleted");
        } catch (IOException e) {
            e.printStackTrace();
            response.setIsSuccess(false);
            response.setMessage(e.getMessage());
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
