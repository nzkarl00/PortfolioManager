package nz.ac.canterbury.seng302.portfolio.model.userGroups;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A repository to store group repo models within the database.
 */
@Repository
public interface GroupRepoRepository extends CrudRepository<GroupRepo, Integer> {
    GroupRepo findById(int id);
    Optional<GroupRepo> findByParentGroupId(int id);
}
