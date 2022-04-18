package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {
    @Test
    public void initFromUserRole() {
        Role role = new Role(UserRole.STUDENT);
        assertEquals("student", role.toString());

        role = new Role(UserRole.TEACHER);
        assertEquals("teacher", role.toString());

        role = new Role(UserRole.COURSE_ADMINISTRATOR);
        assertEquals("course_administrator", role.toString());
    }
}