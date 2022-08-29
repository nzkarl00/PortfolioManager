package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * The repo interface to auto generate the sql calling and updating for the GroupMembership table
 */
@Repository
public interface GroupMembershipRepository extends CrudRepository<GroupMembership, Long> {
    List<GroupMembership> findAllByRegisteredGroups(Groups group);
    List<GroupMembership> findAllByRegisteredGroupUser(AccountProfile profile);
    List<GroupMembership> findAllByRegisteredGroupsAndRegisteredGroupUser(Groups groups, AccountProfile profile);
    GroupMembership findByRegisteredGroupsAndRegisteredGroupUser(Groups group, AccountProfile profile);

    @Modifying
    @Query("delete from GroupMembership m where m.registeredGroups = ?1 and m.registeredGroupUser = ?2")
    void deleteByRegisteredGroupsAndRegisteredGroupUser(Groups group, AccountProfile profile);

    @Modifying
    @Query("delete from GroupMembership m where m.groupMembershipId = ?1")
    void deleteByGroupMembershipId(Long membershipIdToRemove);

    List<GroupMembership> findAllByRegisteredGroupUser(AccountProfile profile, Pageable pageable);
}
