package nz.ac.canterbury.seng302.portfolio.model.evidence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HighFiveRepository extends CrudRepository<HighFive, Integer> {
    HighFive findByParentEvidenceAndParentUserId(Evidence parentEvidence, int userId);

    @Modifying
    @Query("delete from HighFive h where h.id = ?1")
    void delete(int id);
}
