package nz.ac.canterbury.seng302.identityprovider.model;

import org.springframework.data.repository.CrudRepository;

public interface AccountProfileRepository extends CrudRepository<AccountProfile, Integer> {

    AccountProfile findByaccountname(String accountname);
    AccountProfile findByemail(String email);
}