package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * The repo interface to auto generate the sql calling and updating for the Group table
 */
@Repository
public interface GroupRepository extends CrudRepository<Groups, Long> {

    List<Groups> findAllByShortName(String shortName);
    List<Groups> findAllByLongName(String longName);
}
