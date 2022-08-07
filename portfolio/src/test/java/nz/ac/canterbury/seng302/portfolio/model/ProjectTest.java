package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectTest {

    @Test
    void projectInLocalDateFormatTest() {

        // formatting for comparing the dates
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // create variables to be passed to the constructors
        String expectedName = "Project 2022";
        String expectedDescription = "this project is about a new sprint";
        LocalDate expectedStartDate = LocalDate.now();
        LocalDate expectedEndDate = expectedStartDate.plusMonths(8);

        // instance being made
        Project actualProject = new Project(expectedName, expectedDescription, expectedStartDate, expectedEndDate);

        // convert dates to localDate format for comparing
        String actualStartDate = simpleDateFormat.format(actualProject.getStartDate());
        String actualEndDate = simpleDateFormat.format(actualProject.getEndDate());

        // check to see if expected and actual are equal
        assertEquals(expectedName, actualProject.getName());
        assertEquals(expectedDescription, actualProject.getDescription());
        assertEquals(expectedStartDate.toString(), actualStartDate);
        assertEquals(expectedEndDate.toString(), actualEndDate);

    }

    @Test
    void projectInDateFormatTest() {

        // create variables to be passed to the constructors
        String expectedName = "Project 2022";
        String expectedDescription = "this project is about a new sprint";
        Calendar calendar = Calendar.getInstance();
        Date expectedStartDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 8);
        Date expectedEndDate = calendar.getTime();

        // instance being made
        Project actualProject = new Project(expectedName, expectedDescription, expectedStartDate, expectedEndDate);

        // check to see if expected and actual are equal
        assertEquals(expectedName, actualProject.getName());
        assertEquals(expectedDescription, actualProject.getDescription());
        assertEquals(expectedStartDate, actualProject.getStartDate());
        assertEquals(expectedEndDate, actualProject.getEndDate());

    }

    @Test
    void projectInStringFormatTest() {

        // formatting for comparing the dates
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // create variables to be passed to the constructors
        String expectedName = "Project 2022";
        String expectedDescription = "this project is about a new sprint";
        String expectedStartDate = LocalDate.now().toString();
        String expectedEndDate = LocalDate.now().plusMonths(8).toString();


        // instance being made
        Project actualProject = new Project(expectedName, expectedDescription, expectedStartDate, expectedEndDate);

        // convert dates to localDate format for comparing
        String actualStartDate = simpleDateFormat.format(actualProject.getStartDate());
        String actualEndDate = simpleDateFormat.format(actualProject.getEndDate());

        // check to see if expected and actual are equal
        assertEquals(expectedName, actualProject.getName());
        assertEquals(expectedDescription, actualProject.getDescription());
        assertEquals(expectedStartDate, actualStartDate);
        assertEquals(expectedEndDate, actualEndDate);

    }

    @Test
    void toStringTest() {

        // create variables to be passed to the constructors
        Calendar calendar = Calendar.getInstance();
        Date expectedStartDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 8);
        Date expectedEndDate = calendar.getTime();

        // instance being made for a new project
        Project actualProject = new Project("Project 2022", "test description", expectedStartDate, expectedEndDate);

        String expectedFormat = String.format(
                "Project[id=%d, projectName='Project 2022', projectStartDate='%s', projectEndDate='%s', projectDescription='test description']",
                actualProject.getId(), actualProject.getStartDate(), actualProject.getEndDate());
        // check to see if expected and actual are equal
        assertEquals(expectedFormat, actualProject.toString());
    }

}