package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.repository.CrudRepository;

public interface AccountProfileRepository extends CrudRepository<AccountProfile, Long> {

    AccountProfile findById(Long id);
    AccountProfile findByUsername(String username);
    AccountProfile findByEmail(String email);
}