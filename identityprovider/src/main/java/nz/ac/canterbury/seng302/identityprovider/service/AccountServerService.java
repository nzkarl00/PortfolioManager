package nz.ac.canterbury.seng302.identityprovider.service;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.hibernate.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class AccountServerService extends UserAccountServiceImplBase{

    @Autowired
    AccountProfileRepository repo;

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();
        repo.save(new AccountProfile(request.getUsername(), request.getPassword(), request.getFirstName(), request.getPersonalPronouns(), request.getEmail()));
        reply.setMessage("Created account " + request.getUsername());
        System.out.println("TESTING DATA " + request.getUsername());
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}