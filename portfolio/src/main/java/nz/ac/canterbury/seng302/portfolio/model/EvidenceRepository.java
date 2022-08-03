package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A storage interface for Evidence entities
 */
@Repository
public interface EvidenceRepository extends CrudRepository<Evidence, Integer> {
    Event findById(int id);
    List<Evidence> findByParentUserId(int parentUserId);
    List<Evidence> findAll();

    // NOTE: In future, create a custom getter to fetch a list of evidence based on parent user id and
    // the associated project id.
}
