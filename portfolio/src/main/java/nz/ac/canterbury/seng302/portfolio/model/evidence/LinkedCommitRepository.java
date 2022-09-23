package nz.ac.canterbury.seng302.portfolio.model.evidence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkedCommitRepository extends CrudRepository<LinkedCommit, Integer> {
    LinkedCommit findById(int id);
    List<LinkedCommit> findByParentEvidence(int parentEvidenceId);


    @Modifying
    @Query("delete from LinkedCommit w where w.parentEvidence = ?1 and w.hash = ?2")
    void deleteByParentEvidenceAndHash(Evidence parentEvidence, String hash);}
