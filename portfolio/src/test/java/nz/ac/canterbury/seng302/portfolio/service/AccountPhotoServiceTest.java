package nz.ac.canterbury.seng302.portfolio.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.shared.identityprovider.ProfilePhotoUploadMetadata;
import nz.ac.canterbury.seng302.shared.identityprovider.UploadUserProfilePhotoRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountPhotoServiceTest {

    /**
     * The account server service we are testing in this class
     */
    private static final AccountPhotoService accountPhotoService = new AccountPhotoService();

    /**
     * The stub to contact for and mock back grpc responses
     */
    @Autowired
    private UserAccountServiceGrpc.UserAccountServiceStub
        accountServiceStub = mock(UserAccountServiceGrpc.UserAccountServiceStub.class);

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        accountPhotoService.accountStub = accountServiceStub;
    }

    @Test
    void createPhotoMetaData_blueSky() {
        ProfilePhotoUploadMetadata expected = ProfilePhotoUploadMetadata.newBuilder()
            .setUserId(1)
            .setFileType("jpeg")
            .build();
        ProfilePhotoUploadMetadata actual = accountPhotoService.createPhotoMetaData(1,"jpeg");
        assertEquals(expected, actual);
    }

    StreamObserver<UploadUserProfilePhotoRequest> streamObserver = mock(StreamObserver.class);

    @Test
    void uploadPhoto_BlueSky() throws IOException {
        // get the test image
        String dir = System.getProperty("user.dir");
        Path userDir = Paths.get(dir);
        Path resources = userDir.resolve("src/test/resources/test.images/adams-world-1.jpg");

        //turn the test image into the filetype we recieve from the controller
        // https://stackoverflow.com/questions/16648549/converting-file-to-multipartfile
        String name = "adams-world-1.jpg";
        String originalFileName = "adams-world-1.jpg";
        String contentType = "image/jpeg";
        byte[] content = null;

        try {
            content = Files.readAllBytes(resources);
        } catch (final IOException e) {
            fail(e.getMessage());
        }
        MultipartFile result = new MockMultipartFile(name,
            originalFileName, contentType, content);

        when(accountServiceStub.uploadUserProfilePhoto(any(StreamObserver.class))).thenReturn(streamObserver);
        accountPhotoService.uploadPhoto(1, result);
        verify(streamObserver, times(1)).onCompleted();
        ArgumentCaptor<UploadUserProfilePhotoRequest> captor = ArgumentCaptor.forClass(UploadUserProfilePhotoRequest.class);
        // check we get 98 requests to upload from our stream observer
        // note this is constant as our test file length is constant
        verify(streamObserver, times(98)).onNext(captor.capture());
        List<UploadUserProfilePhotoRequest> request = captor.getAllValues();
        // check the first uploaded request contains the correct meta-data
        assertEquals("jpeg", request.get(0).getMetaData().getFileType());
        assertEquals(1, request.get(0).getMetaData().getUserId());
    }
}