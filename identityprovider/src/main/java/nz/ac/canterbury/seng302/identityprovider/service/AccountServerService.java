package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.hibernate.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;

@GrpcService
public class AccountServerService extends UserAccountServiceImplBase{

    @Autowired
    AccountProfileRepository repo;

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();
        // TODO: Handle saving of name.
        repo.save(new AccountProfile(request.getUsername(), request.getPassword(), new Date(), "", request.getEmail(), null));
        reply.setMessage("Created account " + request.getUsername());
        System.out.println("TESTING DATA " + request.getUsername());
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

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
                .setCreated(Timestamp.getDefaultInstance()) // TODO
                .setProfileImagePath(profile.getPhotoPath())
                .addRoles(UserRole.STUDENT)
                .addRoles(UserRole.COURSE_ADMINISTRATOR)
                .addRoles(UserRole.TEACHER); // TODO in db
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
