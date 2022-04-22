package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * The repo interface to auto generate the sql calling and updating for the accountProfile table
 */
@Repository
public interface AccountProfileRepository extends CrudRepository<AccountProfile, Long> {

    AccountProfile findById(int id);
    AccountProfile findByUsername(String username);
    AccountProfile findByEmail(String email);

    List<AccountProfile> findAll();

    // used for sorting paginated user requests
    List<AccountProfile> findAllByOrderByUsernameAsc();
    List<AccountProfile> findAllByOrderByFirstNameAsc();
    List<AccountProfile> findAllByOrderByFirstNameDesc();
    List<AccountProfile> findAllByOrderByLastNameAsc();
    List<AccountProfile> findAllByOrderByLastNameDesc();
    List<AccountProfile> findAllByOrderByNicknameAsc();
    List<AccountProfile> findAllByOrderByNicknameDesc();
    List<AccountProfile> findAllByOrderByUsernameDesc();
}
