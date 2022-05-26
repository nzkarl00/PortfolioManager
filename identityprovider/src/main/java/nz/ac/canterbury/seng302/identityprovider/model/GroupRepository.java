package nz.ac.canterbury.seng302.identityprovider.model;

import org.apache.catalina.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * The repo interface to auto generate the sql calling and updating for the Group table
 */
@Repository
public interface GroupRepository extends CrudRepository<Groups, Long> {
    Groups findById(int id);
}
