package nz.ac.canterbury.seng302.portfolio.model.evidence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebLinkRepository extends CrudRepository<WebLink, Integer> {
    WebLink findById(int id);
    List<WebLink> findByParentEvidence(int parentEvidenceId);

    @Modifying
    @Query("delete from Weblink w where w.parentEvidence = ?1")
    void deleteAllByEvidence(Evidence evidence);
}
