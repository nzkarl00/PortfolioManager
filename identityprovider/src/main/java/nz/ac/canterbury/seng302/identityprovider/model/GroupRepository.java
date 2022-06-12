package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * The repo interface to auto generate the sql calling and updating for the Group table
 */
@Repository
public interface GroupRepository extends CrudRepository<Groups, Long> {
    Groups findByGroupId(int id);

    List<Groups> findAllByGroupShortName(String shortName);
    List<Groups> findAllByGroupLongName(String longName);
    List<Groups> findAllByGroupId(int groupId);

    List<Groups> findAll(Pageable request);
}
