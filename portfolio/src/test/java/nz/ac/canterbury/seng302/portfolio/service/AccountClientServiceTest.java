package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountClientServiceTest {
    // Note https://github.com/mockito/mockito/wiki/What's-new-in-Mockito-2#mock-the-unmockable-opt-in-mocking-of-final-classesmethods
    // Much of the implementation of mocking final methods came from here

    /**
     * The account server service we are testing in this class
     */
    @Autowired
    private static AccountClientService accountClientService = new AccountClientService();

    /**
     * The stub to contact for and mock back grpc responses
     */
    @Autowired
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub accountServiceStub = mock(UserAccountServiceGrpc.UserAccountServiceBlockingStub.class);

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        accountClientService.accountServiceStub = accountServiceStub;
    }

    /**
     * note this mainly tests the ability to make the request, and send it
     */
    @Test
    void registerTest() {
        UserRegisterResponse expected = UserRegisterResponse.newBuilder().setIsSuccess(true).setMessage("this is the successful one").setNewUserId(1).build();
        UserRegisterRequest expectedInput = UserRegisterRequest.newBuilder()
            .setUsername("username")
            .setPassword("password")
            .setFirstName("lachlan")
            .setLastName("alsop")
            .setPersonalPronouns("He/Him")
            .setEmail("lra63@uclive.ac.nz")
            .build();
        when(accountServiceStub.register(expectedInput)).thenReturn(expected);
        UserRegisterResponse actual = accountClientService.register("username", "password", "lachlan", "alsop", "He/Him", "lra63@uclive.ac.nz");
        assertEquals(expected, actual);
    }

    @Test
    void registerFail() {
        UserRegisterResponse expected = UserRegisterResponse.newBuilder().setIsSuccess(true).setMessage("this is the successful one").setNewUserId(1).build();
        UserRegisterRequest expectedInput = UserRegisterRequest.newBuilder()
            .setUsername("username")
            .setPassword("password")
            .setFirstName("lachlan")
            .setLastName("alsop")
            .setPersonalPronouns("He/Him")
            .setEmail("lra63@uclive.ac.nz")
            .build();
        when(accountServiceStub.register(expectedInput)).thenReturn(expected);
        UserRegisterResponse actual = accountClientService.register("username", "password", "Lachlan", "Alsop", "He/Him", "lra63@uclive.ac.nz");
        assertNotEquals(expected, actual); // names are capitalised, meaning it should not return any output
    }

    /**
     * note this mainly tests the ability to make the request, and send it
     */
    @Test
    void getUserByIdTest() {
        UserResponse expected = UserResponse.newBuilder().setFirstName("lachlan").build();
        GetUserByIdRequest expectedInput = GetUserByIdRequest.newBuilder().setId(1).build();
        when(accountServiceStub.getUserAccountById(expectedInput)).thenReturn(expected);
        UserResponse actual = accountClientService.getUserById(1);
        assertEquals(expected, actual);
    }

    @Test
    void getUserByIdFail() {
        UserResponse expected = UserResponse.newBuilder().setFirstName("lachlan").build();
        GetUserByIdRequest expectedInput = GetUserByIdRequest.newBuilder().setId(2).build();
        when(accountServiceStub.getUserAccountById(expectedInput)).thenReturn(expected);
        UserResponse actual = accountClientService.getUserById(1);
        assertNotEquals(expected, actual); // id is 2, not 1
    }

    /**
     * note this mainly tests the ability to make the request, and send it
     */
    @Test
    void editUserTest() {
        EditUserResponse expected = EditUserResponse.newBuilder().setIsSuccess(true).build();
        EditUserRequest expectedInput = EditUserRequest.newBuilder()
            .setUserId(1)
            .setMiddleName("robert colin")
            .setFirstName("lachlan")
            .setLastName("alsop")
            .setNickname("lachie")
            .setPersonalPronouns("He/Him")
            .setEmail("lra63@uclive.ac.nz")
            .setBio("i think therefor i am ;)")
            .build();
        when(accountServiceStub.editUser(expectedInput)).thenReturn(expected);
        EditUserResponse actual = accountClientService.editUser(1, "lachlan", "robert colin", "alsop", "lachie", "i think therefor i am ;)", "He/Him", "lra63@uclive.ac.nz");
        assertEquals(expected, actual);
    }

    @Test
    void editUserFail() {
        EditUserResponse expected = EditUserResponse.newBuilder().setIsSuccess(true).build();
        EditUserRequest expectedInput = EditUserRequest.newBuilder()
            .setUserId(1)
            .setMiddleName("robert colin")
            .setFirstName("lachlan")
            .setLastName("alsop")
            .setNickname("lachie")
            .setPersonalPronouns("He/Him")
            .setEmail("lra63@uclive.ac.nz")
            .setBio("i think therefor i am ;)")
            .build();
        when(accountServiceStub.editUser(expectedInput)).thenReturn(expected);
        EditUserResponse actual = accountClientService.editUser(2, "lachlan", "robert colin", "alsop", "lachie", "i think therefor i am ;)", "He/Him", "lra63@uclive.ac.nz");
        assertNotEquals(expected, actual);
    }

    /**
     * note this mainly tests the ability to make the request, and send it
     */
    @Test
    void getPaginatedUsersTest() {
        UserResponse response = UserResponse.newBuilder().setFirstName("lachlan").build();
        PaginatedUsersResponse expected = PaginatedUsersResponse.newBuilder().addUsers(response).build();
        GetPaginatedUsersRequest expectedInput = GetPaginatedUsersRequest.newBuilder()
            .setLimit(50)
            .setOffset(0)
            .setOrderBy("name_desc")
            .build();
        when(accountServiceStub.getPaginatedUsers(expectedInput)).thenReturn(expected);
        PaginatedUsersResponse actual = accountClientService.getPaginatedUsers(50, 0, "name", 1);
        assertEquals(expected, actual);
    }

    @Test
    void getPaginatedUsersFail() {
        UserResponse response = UserResponse.newBuilder().setFirstName("lachlan").build();
        PaginatedUsersResponse expected = PaginatedUsersResponse.newBuilder().addUsers(response).build();
        GetPaginatedUsersRequest expectedInput = GetPaginatedUsersRequest.newBuilder()
            .setLimit(50)
            .setOffset(0)
            .setOrderBy("name_desc")
            .build();
        when(accountServiceStub.getPaginatedUsers(expectedInput)).thenReturn(expected);
        PaginatedUsersResponse actual = accountClientService.getPaginatedUsers(50, 1, "name", 1);
        assertNotEquals(expected, actual);
    }

    /**
     * note this mainly tests the ability to make the request, and send it
     */
    @Test
    void editPasswordTest() {
        ChangePasswordResponse expected = ChangePasswordResponse.newBuilder().build();
        ChangePasswordRequest expectedInput = ChangePasswordRequest.newBuilder()
            .setNewPassword("oaisdvhiern___SECURE!!!___sahufi")
            .setCurrentPassword("password")
            .setUserId(1)
            .build();
        when(accountServiceStub.changeUserPassword(expectedInput)).thenReturn(expected);
        ChangePasswordResponse actual = accountClientService.editPassword(1, "password", "oaisdvhiern___SECURE!!!___sahufi");
        assertEquals(expected, actual);
    }

    @Test
    void editPasswordFail() {
        ChangePasswordResponse expected = ChangePasswordResponse.newBuilder().build();
        ChangePasswordRequest expectedInput = ChangePasswordRequest.newBuilder()
            .setNewPassword("oaisdvhiern___SECURE!!!___sahufi")
            .setCurrentPassword("password")
            .setUserId(1)
            .build();
        when(accountServiceStub.changeUserPassword(expectedInput)).thenReturn(expected);
        ChangePasswordResponse actual = accountClientService.editPassword(2, "password", "oaisdvhiern___SECURE!!!___sahufi");
        assertNotEquals(expected, actual);
    }
}