package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * The repo interface to auto generate the sql calling and updating for the GroupMembership table
 */
@Repository
public interface GroupMembershipRepository extends CrudRepository<GroupMembership, Long> {
    List<GroupMembership> findAllByRegisteredGroups(Groups group);
    void deleteGroupMembershipByRegisteredGroupUser(AccountProfile profile);
}
