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
    Event findByParentUserId(int parentUserId);
//    Event findByParentUserId(int id);
}
