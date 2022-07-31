package nz.ac.canterbury.seng302.portfolio.model.evidence;

import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceTagRepository  extends CrudRepository<EvidenceTag, Integer> {
    EvidenceTag findById(int id);
}