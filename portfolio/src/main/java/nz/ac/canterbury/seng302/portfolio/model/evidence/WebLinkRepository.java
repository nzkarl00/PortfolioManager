package nz.ac.canterbury.seng302.portfolio.model.evidence;

import nz.ac.canterbury.seng302.portfolio.model.evidence.WebLink;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebLinkRepository extends CrudRepository<WebLink, Integer> {
    WebLink findById(int id);
    List<WebLink> findByParentEvidence(int parentEvidenceId);
}
