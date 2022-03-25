package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountProfileRepository extends CrudRepository<AccountProfile, Integer> {
    AccountProfile findById(int id);
    AccountProfile findByUsername(String username);
    AccountProfile findByEmail(String email);
}
