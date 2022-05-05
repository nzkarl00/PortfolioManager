package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void projectInStringFormatTest() {

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

        // formatting for comparing the dates
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

}