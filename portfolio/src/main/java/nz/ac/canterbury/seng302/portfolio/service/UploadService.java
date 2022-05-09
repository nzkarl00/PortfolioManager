package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.ProfilePhotoUploadMetadata;
import nz.ac.canterbury.seng302.shared.identityprovider.UploadUserProfilePhotoRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class UploadService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceStub photoStub;

    public ProfilePhotoUploadMetadata createPhotoMetaData(int id, String filepath) {
        ProfilePhotoUploadMetadata metaData = ProfilePhotoUploadMetadata.newBuilder()
                .setUserId(id)
                .setFileType(filepath.split(".")[-1])
                .build();
        return metaData;
    }

    public void uploadPhoto(int id, MultipartFile file) throws IOException {
        ProfilePhotoUploadMetadata data = createPhotoMetaData(id, file.getOriginalFilename());
        ByteString bytes = photoToBytes(file);
        uploadUserProfilePhoto(data, bytes);
    }

    public ByteString photoToBytes(MultipartFile file) throws IOException {
        return ByteString.copyFrom(file.getBytes());
    }

    public void uploadUserProfilePhoto(ProfilePhotoUploadMetadata metaData, ByteString fileContent) {
        StreamObserver<FileUploadStatusResponse> responseObserver = new StreamObserver<FileUploadStatusResponse>() {
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
        };
        StreamObserver<UploadUserProfilePhotoRequest> requestObserver = photoStub.uploadUserProfilePhoto(responseObserver);
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
