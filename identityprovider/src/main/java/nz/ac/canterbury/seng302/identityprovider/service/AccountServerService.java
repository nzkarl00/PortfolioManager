package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.identityprovider.util.FileSystemUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
    FileSystemUtils fsUtils;

    @Autowired
    GroupsServerService groupsServerService;

    // Repositories required
    @Autowired
    AccountProfileRepository repo;
    @Autowired
    RolesRepository roleRepo;
    @Autowired
    GroupMembershipRepository groupMembershipRepo;
    @Autowired
    GroupRepository groupRepo;

    /**
     * if there are no users in the db, build a set of 5001 default users
     */
    @PostConstruct
    private void buildDefaultUsers() {
        // if no users exist
        if (repo.findById(1) == null) {

            //https://www.baeldung.com/java-random-string
            int leftLimit = 48; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();
            StringBuilder buffer = new StringBuilder(targetStringLength);
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int)
                        (random.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
            }
            String generatedString = buffer.toString();
            String hashedPassword = Hasher.hashPassword(generatedString);

            // add a default admin password to the file directory
            // if a malicious user gets into this server
            // an admin privileged user shouldn't aid them in their attacks

            // System.outs here are fine in my books currently as this will only be run once
            // feel free to disagree though
            try {
                File admin = new File(System.getProperty("user.dir") + "/defaultAdminPassword.txt");
                // make sure new file can be made
                if (admin.exists()) {
                    admin.delete();
                }
                if (admin.createNewFile()) {
                    //
                    System.out.println("default admin file created: " + admin.getName());
                    System.out.println(generatedString);
                    FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + "/defaultAdminPassword.txt");
                    myWriter.write(generatedString);
                    myWriter.close();
                    createNewUsers(hashedPassword);
                } else {
                    System.out.println("default admin file already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred in creating the default admin file.");
                e.printStackTrace();
            }
        }
    }

    private void createNewUsers(String hashedPassword) {
        AccountProfile newAdmin = repo.save(
                new AccountProfile(
                        "admin", hashedPassword, new Date(), "", "admin@defaultAdmin",
                        null, "admin", "admin", "He/Him"));
        roleRepo.save(new Role(newAdmin, "3admin"));
        Groups groupToAddTo = groupRepo.findByGroupId(1);
        GroupMembership groupMemberToAdd = new GroupMembership(newAdmin, groupToAddTo);
        groupMembershipRepo.save(groupMemberToAdd);


        try {
            // open the names to build users from
            File firstNames = new File(System.getProperty("user.dir") + "/src/main/resources/buildUsers/firstNames.txt");
            Scanner firstNamesReader = new Scanner(firstNames);

            File lastNames = new File(System.getProperty("user.dir") + "/src/main/resources/buildUsers/lastNames.txt");
            Scanner lastNamesReader = new Scanner(firstNames);

            // loop through the names and build the users
            while (firstNamesReader.hasNextLine()) {
                String firstName = firstNamesReader.nextLine();
                while (lastNamesReader.hasNextLine()) {
                    String lastName = lastNamesReader.nextLine();
                    AccountProfile newAccount = repo.save(
                            new AccountProfile(
                                    firstName + lastName, hashedPassword, new Date(), "", firstName + "." + lastName + "@default",
                                    null, firstName, lastName, "He/Him"));
                    roleRepo.save(new Role(newAccount, "1student"));
                    Groups userGroup = groupRepo.findByGroupId(2);
                    GroupMembership userToAdd = new GroupMembership(newAccount, userGroup);
                    groupMembershipRepo.save(userToAdd);
                }
            }
            firstNamesReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File could not be found");
            e.printStackTrace();
        }
    }


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
            roleRepo.save(new Role(newAccount, "1student"));
            List<Groups> noMembers = groupRepo.findAllByGroupShortName("MWAG");
            groupMembershipRepo.save(new GroupMembership(noMembers.get(0), newAccount));
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
            UserResponse reply = accountService.buildUserResponse(profile);
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
                .setMessage("Your account has been edited");
            responseObserver.onNext(reply.build());
        }
        responseObserver.onCompleted();
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
            reply.addUsers(accountService.buildUserResponse(usersSorted.get(i)));
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
            AccountProfile profile = repo.findById(request.getUserId());
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

    /**
     * Gets the role the user wish to modify (added or removed) to be returned as a string.
     * @param roleToModify the UserRole from the grpc request, that was requested to be modified (added or removed)
     */
    public String getRoleToModify(UserRole roleToModify) {

        return switch (roleToModify) {
            case TEACHER -> groupsServerService.TEACHER_ROLE;
            case COURSE_ADMINISTRATOR -> groupsServerService.ADMIN_ROLE;
            default -> groupsServerService.STUDENT_ROLE;
        };
    }


    /**
     * Remove the role from a user with details specified by the request.
     * The action to remove a role from a user will be reflected in the DB, for both Role, Group, and GroupMembership repos.
     * @param request the grpc request containing the change details
     * @param responseObserver the observer to send the response to
     */
    @Transactional
    @Override
    public void removeRoleFromUser(ModifyRoleOfUserRequest request, StreamObserver<UserRoleChangeResponse> responseObserver) {
        AccountProfile user = repo.findById(request.getUserId());
        UserRoleChangeResponse.Builder reply = UserRoleChangeResponse.newBuilder();

        String roleToRemove = getRoleToModify(request.getRole()); // The role to remove from the user as given from the request.

        Long roleIdToRemove = null;

        List<Role> rolesOfUser = roleRepo.findAllByRegisteredUser(user); // List of roles held by that user
        for (Role role: rolesOfUser) {

            // Out of the roles held by that user, update the repos with the requested role to be removed.
            if (role.getRole().equals(roleToRemove)) {
                roleIdToRemove = role.getUserRoleId();
                roleRepo.deleteById(roleIdToRemove);

                // if the role removal is a teacher also remove them from the teacher group
                if (roleToRemove.equals(groupsServerService.TEACHER_ROLE) || role.equals(groupsServerService.ADMIN_ROLE)) {
                    int isTeacher = 0;
                    for ( Role currentRole: user.getRoles()) {
                        System.out.println(currentRole.getPlainRole());
                        if ((currentRole.getPlainRole().equals("admin")) || (currentRole.getPlainRole().equals("teacher"))) {
                            isTeacher += 1;
                        }
                    }
                    if (isTeacher < 2) {
                        Groups teacherGroup = groupRepo.findAllByGroupShortName(groupsServerService.TEACHER_GROUP_NAME_SHORT).get(0);
                        groupMembershipRepo.deleteByRegisteredGroupsAndRegisteredGroupUser(teacherGroup, user);
                    }

                    // if there are no groups left for the user add them to Members Without A Group (MWAG)
                    if (groupMembershipRepo.findAllByRegisteredGroupUser(user).isEmpty()) {
                        Groups noGroup = groupRepo.findAllByGroupShortName(groupsServerService.MWAG_GROUP_NAME_SHORT).get(0);
                        groupMembershipRepo.save(new GroupMembership(user, noGroup));
                    }
                }
            }
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    /**
     * Add the role from a user with details specified by the request.
     * The action to add a role from a user will be reflected in the DB, for both Role, Group, and GroupMembership repos.
     * @param request the grpc request containing the change details
     * @param responseObserver the observer to send the response to
     */
    @Transactional
    @Override
    public void addRoleToUser(ModifyRoleOfUserRequest request, StreamObserver<UserRoleChangeResponse> responseObserver) {
        AccountProfile user = repo.findById(request.getUserId());
        UserRoleChangeResponse.Builder reply = UserRoleChangeResponse.newBuilder();

        String role = getRoleToModify(request.getRole()); // The role to add to the user as given from the request.
        Role roleForRepo = new Role(user, role);
        roleRepo.save(roleForRepo);

        // if the role to add is a teacher, add them to the teacher group and remove from the members without a group.
        if (role.equals(groupsServerService.TEACHER_ROLE) || role.equals(groupsServerService.ADMIN_ROLE)) {
            Groups teacherGroup = groupRepo.findAllByGroupShortName(groupsServerService.TEACHER_GROUP_NAME_SHORT).get(0);
            groupMembershipRepo.deleteByRegisteredGroupsAndRegisteredGroupUser(teacherGroup, user);
            groupMembershipRepo.save(new GroupMembership(user, teacherGroup));

            Groups noMembers = groupRepo.findAllByGroupShortName(groupsServerService.MWAG_GROUP_NAME_SHORT).get(0);
            groupMembershipRepo.deleteByRegisteredGroupsAndRegisteredGroupUser(noMembers, user);
        }

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
