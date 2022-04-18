package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Repository
public interface AccountProfileRepository extends CrudRepository<AccountProfile, Long> {

    AccountProfile findById(int id);
    AccountProfile findByUsername(String username);
    AccountProfile findByEmail(String email);
    List<AccountProfile> findAll();
}
