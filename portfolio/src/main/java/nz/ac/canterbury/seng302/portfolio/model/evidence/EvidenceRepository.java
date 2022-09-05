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
    Evidence findById(int id);
    List<Evidence> findAllByOrderByDateAsc();
    List<Evidence> findAllByAssociatedProjectOrderByDateAsc(Project parent_project);
    List<Evidence> findAllByParentUserIdOrderByDateAsc(Integer valueOf);
    List<Evidence> findAllByAssociatedProjectAndParentUserIdOrderByDateAsc(Project project, Integer userId);
    // This query selects all the evidence the includes the category denoted by the categoryInt constants Evidence.QUALITATIVE_SKILLS etc..
    // The value from the database is divided by the constant, which is a binary column 2^X to shift the bits right by X for comparison
    // If the shifted value MOD 2 is equal to 1 it means the role is present in that piece of evidence, so it will be selected
    @Query(value="select * from evidence where MOD(evidence.categories/:categoryInt, 2) = 1", nativeQuery = true)
    List<Evidence> getEvidenceByCategoryInt(Integer categoryInt);
}
