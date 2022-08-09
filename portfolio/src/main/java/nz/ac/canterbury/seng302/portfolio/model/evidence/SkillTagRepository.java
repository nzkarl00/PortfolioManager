package nz.ac.canterbury.seng302.portfolio.model.evidence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillTagRepository  extends CrudRepository<SkillTag, Integer> {
    SkillTag findById(int id);
    List<SkillTag> findAll();
    SkillTag findByTitle(String title);

    SkillTag findByTitleIgnoreCase(String validSkillString);
}