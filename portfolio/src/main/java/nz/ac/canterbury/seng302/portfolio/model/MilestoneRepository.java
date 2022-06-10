package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The repo interface to auto generate the sql calling and updating for the roles table
 */
@Repository
public interface MilestoneRepository extends CrudRepository<Milestone, Integer> {
    Milestone findById(int id);
    Milestone findByParentProject(int id);
    List<Milestone> findAllByParentProject(Project project);
}
