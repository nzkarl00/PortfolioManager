package nz.ac.canterbury.seng302.portfolio.model.evidence;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillTagRepository  extends CrudRepository<SkillTag, Integer> {
    SkillTag findById(int id);
    List<SkillTag> findAll();
    SkillTag findByTitle(String title);

    Optional<SkillTag> findByTitleAndParentProject(String title, Project project);

    SkillTag findByTitleIgnoreCase(String validSkillString);

    @Query("select st from SkillTag st " +
            "inner join EvidenceTag et on et.parentSkillTag = st " +
//            "inner join evidence e on et.parentEvidence = e" +
            "where et.parentEvidence = ?1")
    List<SkillTag> findByParentEvidence(Evidence parentEvidence);
}
