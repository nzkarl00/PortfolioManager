package nz.ac.canterbury.seng302.identityprovider.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleTest {

    @Test
    void roleTest() {

        // create variables for the constructor
        AccountProfile account = new AccountProfile();
        String expectedRole = "teacher";

        // make actual instance for testing correct variables are being passed in
        Role actualRole = new Role(account, expectedRole);

        assertEquals(expectedRole, actualRole.getRole());
    }
}
