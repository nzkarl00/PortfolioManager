package nz.ac.canterbury.seng302.portfolio.model.evidence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceTagRepository  extends CrudRepository<EvidenceTag, Integer> {
    EvidenceTag findById(int id);
    List<EvidenceTag> findAll();

    List<EvidenceTag> findAllByParentSkillTagId(Integer integer);
    List<EvidenceTag> findAllByParentEvidenceId(Integer evidence);
}