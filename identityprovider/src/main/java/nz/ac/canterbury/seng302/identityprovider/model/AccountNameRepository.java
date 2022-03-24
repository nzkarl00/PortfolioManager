package nz.ac.canterbury.seng302.identityprovider.model;

import java.util.*;

import org.springframework.data.repository.CrudRepository;

public interface AccountNameRepository extends CrudRepository<AccountName, Long> {
    AccountName findNameById(Long id);
    ArrayList<AccountName> findByFirstName(String firstName);
    ArrayList<AccountName> findByLastName(String lastName);
}
