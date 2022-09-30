package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.userGroups.User;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

public class UserTemplate {
    public String username;
    public String password;
    public String passwordConfirm;
    public String firstname;
    public String lastname;
    public String pronouns;
    public String email;

    public UserTemplate() {};

}
