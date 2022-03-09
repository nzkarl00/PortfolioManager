package nz.ac.canterbury.seng302.portfolio.store;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintRepository extends CrudRepository<Sprint, Long> {

    public List<Sprint> findByName(String name);
}

