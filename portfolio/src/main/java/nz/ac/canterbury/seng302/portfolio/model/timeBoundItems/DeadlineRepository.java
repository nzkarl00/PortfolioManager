package nz.ac.canterbury.seng302.portfolio.model.timeBoundItems;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Deadline;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The repo interface to auto generate the sql calling and updating for the roles table
 */
@Repository
public interface DeadlineRepository extends CrudRepository<Deadline, Integer> {
    Deadline findById(int id);
    Deadline findByParentProject(int id);
    /*
    Orders all items by start date, beginning with earlier dates.
    */
    List<Deadline> findAllByParentProjectOrderByStartDateAsc(Project project);
    /*
    Orders all items within the day range by start date, beginning with earlier dates.
     */

    List<Deadline> findAllByParentProjectAndStartDateBetweenOrderByStartDateAsc(Project project, LocalDateTime startDate, LocalDateTime endDate);
}
