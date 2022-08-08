package nz.ac.canterbury.seng302.portfolio.model.timeBoundItems;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends CrudRepository<Sprint, Integer> {
    List<Sprint> findBySprintName(String sprintName);
    Sprint findById(int id);
    List<Sprint> findByParentProjectId(int parentProjectId);

    List<Sprint> findByParentProjectIdOrderBySprintStartDate(int parentProjectId);

    List<Sprint> getSprintByParentProjectIdOrderBySprintStartDateAsc(Integer projectId);
}
