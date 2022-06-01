package nz.ac.canterbury.seng302.identityprovider.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GroupTest {

    @Test
    void create_teacher_group_test() {

        String expectedLongGroupName = "Teacher Group";
        String expectedShortGroupName = "Teacher Group";

        Groups actual = new Groups(expectedLongGroupName, expectedShortGroupName);

        assertEquals(expectedLongGroupName, actual.getGroupLongName());
        assertEquals(expectedShortGroupName, actual.getGroupShortName());

    }
}
