package nz.ac.canterbury.seng302.identityprovider.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GroupTest {

    @Test
    void create_teacher_group_test() {

        String expectedGroupName = "Teacher Group";

        Groups actual = new Groups(expectedGroupName);

        assertEquals(expectedGroupName, actual.getGroupName());

    }
}
