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
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class AccountPhotoService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceStub accountStub;

    public ProfilePhotoUploadMetadata createPhotoMetaData(int id, String fileType) {
        return ProfilePhotoUploadMetadata.newBuilder()
                .setUserId(id)
                .setFileType("jpeg")
                .build();
    }

    public void uploadPhoto(int id, MultipartFile file) throws IOException {
        ProfilePhotoUploadMetadata data = createPhotoMetaData(id, file.getContentType());
        uploadUserProfilePhoto(data, file);
    }

    public void uploadUserProfilePhoto(ProfilePhotoUploadMetadata metadata, MultipartFile file) throws IOException {
        StreamObserver<UploadUserProfilePhotoRequest> streamObserver = this.accountStub.uploadUserProfilePhoto(new PhotoUploadObserver());

        streamObserver.onNext(UploadUserProfilePhotoRequest.newBuilder()
                .setMetaData(metadata).build());

        InputStream inputStream = file.getInputStream();
        byte[] bytes = new byte[4096];
        int size;
        while ((size = inputStream.read(bytes)) > 0) {
            UploadUserProfilePhotoRequest uploadRequest = UploadUserProfilePhotoRequest.newBuilder()
                    .setFileContent(ByteString.copyFrom(bytes, 0, size))
                    .build();
            streamObserver.onNext(uploadRequest);
        }
        inputStream.close();
        streamObserver.onCompleted();
    }


    public class PhotoUploadObserver implements StreamObserver<FileUploadStatusResponse> {
        @Override
        public void onNext(FileUploadStatusResponse uploadStatusResponse){
            System.out.println("Uploading photo section "+uploadStatusResponse.getMessage()+" "+uploadStatusResponse.getSerializedSize());
        }

        @Override
        public void onError(Throwable t){
            System.out.println("Upload failed, ERROR: "+Status.fromThrowable(t));
        }

        @Override
        public void onCompleted(){
            System.out.println("Upload complete");
        }
    }
}
