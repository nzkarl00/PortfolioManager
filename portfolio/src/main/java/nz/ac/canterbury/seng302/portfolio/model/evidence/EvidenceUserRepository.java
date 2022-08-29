package nz.ac.canterbury.seng302.portfolio.model.evidence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenceUserRepository extends CrudRepository<EvidenceUser, Integer> {
}
