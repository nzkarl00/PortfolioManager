package nz.ac.canterbury.seng302.portfolio.model.evidence;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A storage interface for Evidence entities
 */
@Repository
public interface EvidenceRepository extends CrudRepository<Evidence, Integer> {
    Event findById(int id);
    List<Evidence> findAllByOrderByDateDesc();
    List<Evidence> findAllByAssociatedProjectOrderByDateDesc(Project parent_project);
    List<Evidence> findAllByParentUserIdOrderByDateDesc(Integer valueOf);
}
