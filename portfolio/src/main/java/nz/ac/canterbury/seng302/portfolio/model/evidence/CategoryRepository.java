package nz.ac.canterbury.seng302.portfolio.model.evidence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {
    Category findById(int id);
    List<Category> findAllByParentEvidence(int parentEvidenceId);
    List<Category> findAll();
}
