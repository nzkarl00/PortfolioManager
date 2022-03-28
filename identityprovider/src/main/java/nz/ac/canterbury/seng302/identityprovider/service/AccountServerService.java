package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
import java.util.Optional;

@GrpcService
public class AccountServerService extends UserAccountServiceImplBase{

    @Autowired
    Account accountService;

    @Autowired
    AccountProfileRepository repo;

    /**
     * the handling and registering of a new user through a UserRegisterRequest
     * @param request the request with user details
     * @param responseObserver the place to send the response back to
     */
    @Override
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
        repo.save(new AccountProfile(request.getUsername(), hashedPassword, new Date(), "", request.getEmail(), null));
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
        UserResponse.Builder reply = UserResponse.newBuilder();
        AccountProfile profile = repo.findById(request.getId());
        reply
                .setUsername(profile.getUsername())
                .setFirstName("Not Yet Implemented to db")
                .setMiddleName("not yet implemented to db")
                .setLastName("not yet implemetned to db")
                .setNickname("not yet implemented to db")
                .setBio(profile.getBio())
                .setPersonalPronouns("not yet implemented to db")
                .setEmail(profile.getEmail())
                .setCreated(Timestamp.newBuilder().setSeconds(profile.getRegisterDate().getTime()/1000).build())
                .setProfileImagePath(profile.getPhotoPath())
                .addRoles(UserRole.STUDENT)
                .addRoles(UserRole.COURSE_ADMINISTRATOR)
                .addRoles(UserRole.TEACHER); // TODO in db
        responseObserver.onNext(reply.build());
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
        if (!request.getEmail().isEmpty()) { profile.setEmail(request.getEmail()); } //TODO please add the correct fields once db is up and running
        if (!request.getBio().isEmpty()) { profile.setBio(request.getBio()); }
        repo.save(profile);
        reply.setIsSuccess(true)
                .setMessage("we edited somme shit idk");
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
