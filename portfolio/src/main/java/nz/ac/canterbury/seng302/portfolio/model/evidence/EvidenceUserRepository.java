package nz.ac.canterbury.seng302.portfolio.model.evidence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenceUserRepository extends CrudRepository<EvidenceUser, Integer> {

    @Modifying
    @Query("delete from EvidenceUser e where e.parentEvidence = ?1")
    void deleteAllByEvidence(Evidence evidence);
}
