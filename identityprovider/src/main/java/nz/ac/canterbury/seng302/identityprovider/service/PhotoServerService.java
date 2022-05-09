package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.ProfilePhotoUploadMetadata;
import nz.ac.canterbury.seng302.shared.identityprovider.UploadUserProfilePhotoRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class PhotoServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @Autowired
    AccountProfileRepository repo;

    @GrpcClient("portfolio-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceStub photoStub;

    public StreamObserver<UploadUserProfilePhotoRequest> uploadUserProfilePhoto(StreamObserver<FileUploadStatusResponse> responseObserver) {
        StreamObserver<UploadUserProfilePhotoRequest> requestObserver = new StreamObserver<UploadUserProfilePhotoRequest>() {
            @Override
            public void onNext(UploadUserProfilePhotoRequest uploadRequest) {
                System.out.println(uploadRequest.getFileContent());
                FileUploadStatusResponse.Builder response = FileUploadStatusResponse.newBuilder();
                response.setMessage("looking good");
                responseObserver.onNext(response.build());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Upload failed, ERROR: " + Status.fromThrowable(t));
            }

            @Override
            public void onCompleted() {
                System.out.println("Upload complete");
                FileUploadStatusResponse.Builder response = FileUploadStatusResponse.newBuilder();
                response.setMessage("looking good");
                responseObserver.onNext(response.build());
            }
        };
        return requestObserver;
    }

}
