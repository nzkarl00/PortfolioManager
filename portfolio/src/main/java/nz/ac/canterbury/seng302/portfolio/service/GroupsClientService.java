package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

/**
 * The GRPC client side service class
 * contains many of the protobuf implementations to allow communication between the idp and portfolio servers
 */
public class GroupsClientService extends GroupsServiceGrpc.GroupsServiceImplBase {

    @GrpcClient("identity-provider-grpc-server")
    GroupsServiceGrpc.GroupsServiceBlockingStub groupServiceStub;


    /**
     * makes a CreateGroupRequest to receive a CreateGroupResponse
     *
     * @param shortName the short name of a given group
     * @param longName  the long name of a given group
     * @return the CreateGroupResponse from the request with the params
     */
    public CreateGroupResponse create(String shortName, String longName) {
        CreateGroupRequest request = CreateGroupRequest.newBuilder()
            .setLongName(longName)
            .setShortName(shortName).build();

        return groupServiceStub.createGroup(request);
    }

    /**
     * makes a DeleteGroupRequest to receive a DeleteGroupResponse
     *
     * @param groupId the id of a group that needs to be deleted
     * @return the DeleteGroupResponse from the request with the params
     */
    public DeleteGroupResponse delete(int groupId) {
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder()
            .setGroupId(groupId).build();

        return groupServiceStub.deleteGroup(request);
    }

    /**
     *
     * @param limit
     * @param offset
     * @param asc
     * @return
     */
    public PaginatedGroupsResponse getGroups(int limit, int offset, boolean asc) {
        GetPaginatedGroupsRequest request = GetPaginatedGroupsRequest.newBuilder()
            .setLimit(limit)
            .setOffset(offset)
            .setIsAscendingOrder(asc).build();

        return groupServiceStub.getPaginatedGroups(request);
    }

    /**
     * Returns a response to a request to modify a group
     * @param id the ID of the group to be named
     * @param nameL the new Long name of the group
     * @param nameS the new Short name of the group
     * @return a ModifyGroupDetailsResponse
     */
    public ModifyGroupDetailsResponse modifyGroup(int id, String nameL, String nameS) {
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
            .setGroupId(id)
            .setLongName(nameL)
            .setShortName(nameS).build();

        return groupServiceStub.modifyGroupDetails(request);
    }

    /**
     * Returns information on a specific group
     * @param id the ID of the group to be named
     * @return the group id, short and long names, and a list of members
     */
    public GroupDetailsResponse getGroup(int id) {
        GetGroupDetailsRequest request = GetGroupDetailsRequest.newBuilder()
            .setGroupId(id).build();

        return groupServiceStub.getGroupDetails(request);
    }
}
