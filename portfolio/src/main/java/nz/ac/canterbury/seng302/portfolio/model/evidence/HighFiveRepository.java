package nz.ac.canterbury.seng302.portfolio.model.evidence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HighFiveRepository extends CrudRepository<HighFive, Integer> {
    // Return a single HighFive as a HighFive has unique parentEvidence and userId pair
    HighFive findByParentEvidenceAndParentUserId(Evidence parentEvidence, int userId);

}
