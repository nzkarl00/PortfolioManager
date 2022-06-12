package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The repo interface to auto generate the sql calling and updating for the roles table
 */
@Repository
public interface DeadlineRepository extends CrudRepository<Deadline, Integer> {
    Deadline findById(int id);
    Deadline findByParentProject(int id);
    List<Deadline> findAllByParentProject(Project project);
}
