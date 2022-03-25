package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class AccountService {
    @Autowired
    private AccountProfileRepository repository;

    /**
     * Get list of all account profiles
     */
    public List<AccountProfile> getAllAccounts() {
        List<AccountProfile> list = (List<AccountProfile>) repository.findAll();
        return list;
    }

    /**
     * Get account profile by id
     */
    public AccountProfile getAccountById(Integer id) throws Exception {

        AccountProfile profile = repository.findById(id);
        if(profile!=null) {
            return profile;
        }
        else
        {
            throw new Exception("Account profile not found");
        }
    }

    /**
     * Get account by username
     */
    public AccountProfile getAccountByUsername(String username) throws Exception {

        AccountProfile profile = repository.findByUsername(username);
        if(profile!=null) {
            return profile;
        }
        else
        {
            throw new Exception("Account profile not found");
        }
    }
}
