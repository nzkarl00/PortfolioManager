package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.userGroups.User;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {
    @Test
    public void buildFromUserResponse() {
        UserResponse.Builder reply = UserResponse.newBuilder();
        reply // build response to make user from
            .setUsername("lil Timmy")
            .setFirstName("Timothy")
            .setMiddleName("Eunice")
            .setLastName("Smith")
            .setNickname("Timmy")
            .setBio("I am lil Timmy and I'm here to say your mum is... a really nice person, she makes good cookies too")
            .setPersonalPronouns("He/Him")
            .addRoles(UserRole.STUDENT)
            .setEmail("Timothy.Smith@gmail.com");
        User user = new User(reply.build()); // build user and validate data
        assertEquals("Timothy", user.firstName);
        assertEquals("Smith", user.lastName);
        assertEquals("lil Timmy", user.username);
        assertEquals("Timmy", user.nickname);
        assertEquals("student", user.roles());
    }

    //Note no roles() testing as that functionality will be removed soon
    // it is just a placeholder for now and will be updated with U20
}