package nz.ac.canterbury.seng302.portfolio.model.evidence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvidenceTagRepository  extends CrudRepository<EvidenceTag, Integer> {
    EvidenceTag findById(int id);
    List<EvidenceTag> findAll();

    List<EvidenceTag> findAllByParentSkillTagId(Integer integer);
    List<EvidenceTag> findAllByParentEvidenceId(Integer evidence);
    EvidenceTag findByParentEvidenceIdAndParentSkillTagId(Integer evidence, Integer skillTagID);
    Optional<EvidenceTag> findByParentEvidenceAndParentSkillTag(Evidence evidence, SkillTag skillTag);

    @Modifying
    @Query("delete from EvidenceTag e where e.id = ?1")
    void deleteById(Integer id);
}
