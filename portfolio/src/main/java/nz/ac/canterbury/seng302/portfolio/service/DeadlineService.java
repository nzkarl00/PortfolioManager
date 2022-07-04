package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.DeadlineRepository;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.DeadlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeadlineService {
    @Autowired
    private DeadlineRepository repository;

    /**
     * Get list of all deadlines
     */
    public List<Deadline> getAllDeadlines() {
        List<Deadline> list = (List<Deadline>) repository.findAll();
        return list;
    }

    /**
     * Get deadline by its id.
     */
    public Deadline getDeadlineById(Integer id) throws Exception {

        Optional<Deadline> deadline = repository.findById(id);
        if(deadline!=null) {
            return deadline.get();
        }
        else
        {
            throw new Exception("Deadline not found");
        }
    }
    
}
