package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.Role;
import nz.ac.canterbury.seng302.identityprovider.model.RolesRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.hibernate.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@GrpcService
public class AccountServerService extends UserAccountServiceImplBase{

    @Autowired
    Account accountService;

    @Autowired
    AccountProfileRepository repo;

    @Autowired
    RolesRepository roleRepo;

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
            // TODO: Handle saving of name.
            // Hash the password
            String hashedPassword = Hasher.hashPassword(request.getPassword());
            AccountProfile newAccount = repo.save(
                    new AccountProfile(
                            request.getUsername(), hashedPassword, new Date(), "", request.getEmail(),
                            null, request.getFirstName(), request.getLastName(), request.getPersonalPronouns()));
            roleRepo.save(new Role(newAccount, "student")); // TODO change this from the default
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
        UserResponse reply = buildUserResponse(profile);
        responseObserver.onNext(reply);
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
            .setPersonalPronouns(profile.getPronouns())
            .setEmail(profile.getEmail())
            .setCreated(Timestamp.newBuilder().setSeconds(profile.getRegisterDate().getTime()/1000).build())
            .setProfileImagePath(profile.getPhotoPath());

        for (Role role : profile.getRoles()) {
            if (role.getRole().equals("student")) { reply.addRoles(UserRole.STUDENT); }
            if (role.getRole().equals("teacher")) { reply.addRoles(UserRole.TEACHER); }
            if (role.getRole().equals("admin")) { reply.addRoles(UserRole.COURSE_ADMINISTRATOR); }
        }
        return reply.build();
    }

    /**
     * Send back the all the user details
     * @param request the GetPaginatedUsersRequest
     * @param responseObserver the place to send the message back
     */
    @Override
    public void getPaginatedUsers(GetPaginatedUsersRequest request, StreamObserver<PaginatedUsersResponse> responseObserver) {
        int limit = request.getLimit() + request.getOffset();

        List<AccountProfile> users;
        PaginatedUsersResponse.Builder reply = PaginatedUsersResponse.newBuilder();
        switch ( request.getOrderBy() ) {
            case ("name_desc"):
                users = repo.findAll(PageRequest.of(0, 1), Sort.by(Sort.Direction.DESC, "username"));
                break;
            default:
                users = repo.findAll(PageRequest.of(0, 1), Sort.by(Sort.Direction.ASC, "username"));
        }

        int i = request.getOffset();
        while (i < limit && i < users.size()) {
            reply.addUsers(buildUserResponse(users.get(i)));
            i++;
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
