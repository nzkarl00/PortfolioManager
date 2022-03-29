package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.Role;
import nz.ac.canterbury.seng302.identityprovider.model.RolesRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.Date;

@GrpcService
public class AccountServerService extends UserAccountServiceImplBase{

    @Autowired
    Account accountService;

    @Autowired
    AccountProfileRepository repo;

    @Autowired
    RolesRepository roleRepo;

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
        AccountProfile newAccount = repo.save(new AccountProfile(request.getUsername(), hashedPassword, new Date(), "", request.getEmail(), null, request.getFirstName(), request.getLastName(), request.getPersonalPronouns()));
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

    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        UserResponse.Builder reply = UserResponse.newBuilder();
        AccountProfile profile = repo.findById(request.getId());
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
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

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
                .setMessage("we edited somme shit idk");
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
