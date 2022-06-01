package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GroupsClientServiceTest {

    /**
     * The group server service we are testing in this class
     */
    @Autowired
    private static GroupsClientService groupClientService = new GroupsClientService();

    /**
     * The stub to contact for and mock back grpc responses
     */
    @Autowired
    private GroupsServiceGrpc.GroupsServiceBlockingStub groupServiceStub = mock(GroupsServiceGrpc.GroupsServiceBlockingStub.class);

    /**
     * Setup to replace the autowired instances of these with the mocks
     */
    @BeforeEach
    void setup() {
        groupClientService.groupServiceStub = groupServiceStub;
    }

    /**
     * Test is for creating a group
     */
    @Test
    void createGroup() {
        CreateGroupResponse expected = CreateGroupResponse.newBuilder()
                .setIsSuccess(true)
                .setMessage("group has been successfully been made")
                .setNewGroupId(1)
                .build();

        CreateGroupRequest expectedInput = CreateGroupRequest.newBuilder()
                .setLongName("The Owl Group")
                .setShortName("TOG")
                .build();

        when(groupServiceStub.createGroup(expectedInput)).thenReturn(expected);
        CreateGroupResponse actual = groupClientService.create("TOG", "The Owl Group");
        assertEquals(expected, actual);

    }

    /**
     * Test is for deleting a group
     */
    @Test
    void deleteGroup() {
        DeleteGroupResponse expected = DeleteGroupResponse.newBuilder()
                .setIsSuccess(true)
                .setMessage("group has been successfully deleted")
                .build();


        DeleteGroupRequest expectedInput = DeleteGroupRequest.newBuilder()
                .setGroupId(1)
                .build();

        when(groupServiceStub.deleteGroup(expectedInput)).thenReturn(expected);
        DeleteGroupResponse actual = groupClientService.delete(1);
        assertEquals(expected, actual);

    }

}
