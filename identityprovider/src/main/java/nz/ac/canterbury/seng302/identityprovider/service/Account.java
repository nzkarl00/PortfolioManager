package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.Role;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class Account {
    @Autowired
    private AccountProfileRepository repository;

    /**
     * Get list of all account profiles
     */
    public List<AccountProfile> getAllAccounts() {
        return repository.findAll();
    }

    /**
     * Get account by ID
     * @param id the int of ID to search for
     * @return the account profile searched for
     * @throws Exception tells the user the account profile isn't found
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
     * Get the accountprofile by email
     * @param email the email to get the profile with
     * @return the account profile associated with the email
     * @throws Exception tells the user the account profile isn't found
     */
    public AccountProfile getAccountByEmail(String email) throws Exception {
        AccountProfile profile = repository.findByEmail(email);
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
     * @param username the username to get the profile by
     * @return the Account profile from the email
     * @throws Exception tells the user the account profile isn't found
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

    /**
     * A builder for a UserResponse from a repo profile
     * @param profile the profile to build the protobuf from
     * @return the final protobuf to represent the profile given
     */
    public UserResponse buildUserResponse(AccountProfile profile) {
        UserResponse.Builder reply = UserResponse.newBuilder();
        reply
            .setUsername(profile.getUsername())
            .setFirstName(profile.getFirstName())
            .setMiddleName(profile.getMiddleName())
            .setLastName(profile.getLastName())
            .setNickname(profile.getNickname())
            .setBio(profile.getBio())
            .setId(profile.getId())
            .setPersonalPronouns(profile.getPronouns())
            .setEmail(profile.getEmail())
            .setCreated(
                Timestamp.newBuilder().setSeconds(profile.getRegisterDate().getTime()/1000).build())
            .setProfileImagePath(profile.getPhotoPath());

        for (Role role : profile.getRoles()) {
            if (role.getRole().equals("1student")) { reply.addRoles(UserRole.STUDENT); } // Note the {number}{role} structure is due to sorting to allow for the highest priority roles to be shown
            if (role.getRole().equals("2teacher")) { reply.addRoles(UserRole.TEACHER); }
            if (role.getRole().equals("3admin")) { reply.addRoles(UserRole.COURSE_ADMINISTRATOR); }
        }

        return reply.build();
    }
}
