package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class SprintService {
    @Autowired
    private SprintRepository repository;

    /**
     * Get list of all sprints
     */
    public List<Sprint> getAllSprints() {
        List<Sprint> list = (List<Sprint>) repository.findAll();
        return list;
    }

    /**
     * Get sprint by its id.
     */
    public Sprint getSprintById(Integer id) throws Exception {

        Optional<Sprint> sprint = repository.findById(id);
        if(sprint!=null) {
            return sprint.get();
        }
        else
        {
            throw new Exception("Project not found");
        }
    }
    /**
     * Get sprint by parent id, where parent id is the project that is parent for the sprints.
     */
    public List<Sprint> getSprintByParentId(Integer id) throws Exception {

        List<Sprint> sprints = repository.findByParentProjectId(id);
        if(sprints!=null) {
            return sprints;
        }
        else
        {
            throw new Exception("Project not found");
        }
    }

    /**
     * Checks to see if the proposed dates for a new sprint do not lie within any other sprint dates. Does not check against PROJECT DATES
     * as this would require the introduction of another repository to the service, which would violate single responsibility.
     * @param start the start date of the proposed sprint
     * @param end the end date of the proposed sprint
     * @param projectId the project for which all sprints will be compared to
     * @return true if the dates do not lie within another sprint, false if they do.
     */
    public boolean areNewSprintDatesValid(Date start, Date end, Integer projectId) {
        List<Sprint> sprints = repository.findByParentProjectId(projectId);
        if (start.after(end)) {
            return false;
        }
        for (Sprint s : sprints) {
            if (start.after(s.getStartDate()) && start.before(s.getEndDate())) {
                return false;
            }
            if(end.after(s.getStartDate()) && end.before(s.getEndDate())) {
                return false;
            }
            if(end.getTime() == s.getStartDate().getTime() || start.getTime() == s.getEndDate().getTime() || end.getTime() == s.getEndDate().getTime() || start.getTime() == s.getStartDate().getTime()) {
                return false;
            }
        }
        return true;
    }
}
