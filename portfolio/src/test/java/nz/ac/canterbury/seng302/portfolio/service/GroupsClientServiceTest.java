package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.mock;

public class GroupsClientServiceTest {


    /**
     * The group server service we are testing in this class
     */
    @Autowired
    private static GroupsClientService groupClientService = new GroupsClientService();

//    /**
//     * The stub to contact for and mock back grpc responses
//     */
//    @Autowired
//    private GroupsServiceServiceGrpc. UserAccountServiceGrpc.UserAccountServiceBlockingStub accountServiceStub = mock(UserAccountServiceGrpc.UserAccountServiceBlockingStub.class);
//
//    /**
//     * Setup to replace the autowired instances of these with the mocks
//     */
//    @BeforeEach
//    void setup() {
//        accountClientService.accountServiceStub = accountServiceStub;
//    }
//

}
