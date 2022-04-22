package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The repo interface to auto generate the sql calling and updating for the roles table
 */
@Repository
public interface RolesRepository extends CrudRepository<Role, Long> {
    Role findByRegisteredUser(int id);

    // for roles sorting in user table
    List<Role> findAllByOrderByRoleAsc();
    List<Role> findAllByOrderByRoleDesc();
}
