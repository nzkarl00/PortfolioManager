package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * A repository to store group repo models within the database.
 */
@Repository
public interface GroupRepoRepository extends CrudRepository<GroupRepo, Integer> {
    GroupRepo findById(int id);
    Optional<GroupRepo> findByParentGroupId(int id);
}
