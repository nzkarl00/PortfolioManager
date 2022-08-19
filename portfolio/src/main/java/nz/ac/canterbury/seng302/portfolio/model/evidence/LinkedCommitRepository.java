package nz.ac.canterbury.seng302.portfolio.model.evidence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LinkedCommitRepository extends CrudRepository<LinkedCommit, Integer> {
    LinkedCommit findById(int id);
    List<LinkedCommit> findByParentEvidence(int parentEvidenceId);
}
