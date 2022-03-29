package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends CrudRepository<Role, Long> {
    Role findByRegisteredUser(int id);
}
