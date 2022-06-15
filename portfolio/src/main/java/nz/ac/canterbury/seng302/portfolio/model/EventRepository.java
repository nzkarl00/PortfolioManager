package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The repo interface to auto generate the sql calling and updating for the roles table
 */
@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {
    Event findById(int id);
    Event findByParentProject(int id);
    List<Event> findAllByParentProject(Project project);
}