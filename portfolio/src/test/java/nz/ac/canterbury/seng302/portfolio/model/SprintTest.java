package nz.ac.canterbury.seng302.portfolio.model;


import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Sprint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import static nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Sprint.dateToString;
import static org.junit.jupiter.api.Assertions.*;

class SprintTest {
    private int expectedSprintId;
    private String expectedSprintName;
    private String expectedSprintLabel;
    private String expectedSprintDescription;

    @BeforeEach
    void initializeVariables() {
        this.expectedSprintId = 1;
        this.expectedSprintName = "Sprint 1";
        this.expectedSprintLabel = "Sprint Label 1";
        this.expectedSprintDescription = "Sprint Description";
    }

    @Test
    void sprintInLocalDateFormatTest() {

        // formatting for comparing the dates
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // create variables to be passed to the constructors
        LocalDate expectedStartDate = LocalDate.now();
        LocalDate expectedEndDate = expectedStartDate.plusWeeks(3);


        // instance being made
        Sprint actualSprint = new Sprint(this.expectedSprintId, this.expectedSprintName, this.expectedSprintLabel, this.expectedSprintDescription, expectedStartDate, expectedEndDate);

        // convert dates to localDate format for comparing
        String actualStartDate = simpleDateFormat.format(actualSprint.getStartDate());
        String actualEndDate = simpleDateFormat.format(actualSprint.getEndDate());

        // check to see if expected and actual are equal
        assertEquals(this.expectedSprintName, actualSprint.getName());
        assertEquals(this.expectedSprintLabel, actualSprint.getLabel());
        assertEquals(this.expectedSprintDescription, actualSprint.getDescription());
        assertEquals(expectedStartDate.toString(), actualStartDate);
        assertEquals(expectedEndDate.toString(), actualEndDate);

    }

    @Test
    void sprintInDateFormatTest() {

        // create variables to be passed to the constructors
        Calendar calendar = Calendar.getInstance();
        Date expectedStartDate = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_MONTH, 3);
        Date expectedEndDate = calendar.getTime();

        // instance being made
        Sprint actualSprint = new Sprint(this.expectedSprintId, this.expectedSprintName, this.expectedSprintLabel, this.expectedSprintDescription, expectedStartDate, expectedEndDate);

        // check to see if expected and actual are equal
        assertEquals(this.expectedSprintName, actualSprint.getName());
        assertEquals(this.expectedSprintLabel, actualSprint.getLabel());
        assertEquals(this.expectedSprintDescription, actualSprint.getDescription());
        assertEquals(expectedStartDate, actualSprint.getStartDate());
        assertEquals(expectedEndDate, actualSprint.getEndDate());

    }

    @Test
    void sprintInStringFormatTest() {

        // formatting for comparing the dates
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // create variables to be passed to the constructors
        String expectedStartDate = LocalDate.now().toString();
        String expectedEndDate = LocalDate.now().plusWeeks(3).toString();

        // instance being made
        Sprint actualSprint = new Sprint(this.expectedSprintId, this.expectedSprintName, this.expectedSprintLabel, this.expectedSprintDescription, expectedStartDate, expectedEndDate);

        // convert dates to localDate format for comparing
        String actualStartDate = simpleDateFormat.format(actualSprint.getStartDate());
        String actualEndDate = simpleDateFormat.format(actualSprint.getEndDate());

        // check to see if expected and actual are equal
        assertEquals(this.expectedSprintName, actualSprint.getName());
        assertEquals(this.expectedSprintLabel, actualSprint.getLabel());
        assertEquals(this.expectedSprintDescription, actualSprint.getDescription());
        assertEquals(expectedStartDate, actualStartDate);
        assertEquals(expectedEndDate, actualEndDate);

    }

    @Test
    void toStringTest() {

        // create variables to be passed to the constructors
        Calendar calendar = Calendar.getInstance();
        Date expectedStartDate = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_MONTH, 3);
        Date expectedEndDate = calendar.getTime();

        // instance being made for a new project
        Project actualSprint = new Project(this.expectedSprintName, this.expectedSprintDescription, expectedStartDate, expectedEndDate);

        String expectedFormat = String.format(
                "Project[id=%d, projectName='Sprint 1', projectStartDate='%s', projectEndDate='%s', projectDescription='Sprint Description']",
                actualSprint.getId(), actualSprint.getStartDate(), actualSprint.getEndDate());
        // check to see if expected and actual are equal
        assertEquals(expectedFormat, actualSprint.toString());
    }

    @Test
    void dateToStringTest() {

        // formatting for comparing the dates
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");

        // get current date and format it correctly as in the dateTorString method
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String expectedDate = simpleDateFormat.format(date);

        // call dateToString method in model/sprint

        String actualFormattedDate = dateToString(date);

        assertEquals(expectedDate, actualFormattedDate);



    }

}