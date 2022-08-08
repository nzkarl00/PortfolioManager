package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.SprintRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SprintServiceTest {

    @Mock
    SprintRepository repository;

    @InjectMocks
    SprintService sprintService = new SprintService();

    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    static Sprint testSprint1;
    static Sprint testSprint2;
    static List<Sprint> testSprintList;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this); // This is required for Mockito annotations to work
    }


    @BeforeAll
    static void setup() throws ParseException {
        testSprint1 = new Sprint(0, "testSprint1", "testSprint1", "testDescription", dateFormat.parse("01-05-2022"), dateFormat.parse("01-06-2022"));
        testSprint2 = new Sprint(0, "testSprint2", "testSprint2", "testDescription", dateFormat.parse("02-07-2022"), dateFormat.parse("01-08-2022"));
        testSprintList = new ArrayList<>();
        testSprintList.add(testSprint1);
        testSprintList.add(testSprint2);
    }

    @Test
    void areNewSprintDatesValidWithValidStartAndEndDates() throws ParseException {
        Date startDate = dateFormat.parse("02-06-2022");
        Date endDate = dateFormat.parse("01-07-2022");
        Mockito.when(repository.findByParentProjectId(0)).thenReturn(testSprintList);
        boolean result = sprintService.areNewSprintDatesValid(startDate, endDate, 0);
        assertEquals(true, result);
    }

    @Test
    void areNewSprintDatesValidWithOverlappingStartDate() throws ParseException {
        Date startDate = dateFormat.parse("01-06-2022");
        Date endDate = dateFormat.parse("01-07-2022");
        Mockito.when(repository.findByParentProjectId(0)).thenReturn(testSprintList);
        boolean result = sprintService.areNewSprintDatesValid(startDate, endDate, 0);
        assertEquals(false, result);
    }

    @Test
    void areNewSprintDatesValidWithOverlappingEndDate() throws ParseException {
        Date startDate = dateFormat.parse("02-06-2022");
        Date endDate = dateFormat.parse("02-07-2022");
        Mockito.when(repository.findByParentProjectId(0)).thenReturn(testSprintList);
        boolean result = sprintService.areNewSprintDatesValid(startDate, endDate, 0);
        assertEquals(false, result);
    }

    @Test
    void areNewSprintDatesValidWithOverlappingStartAndEndDate() throws ParseException {
        Date startDate = dateFormat.parse("01-06-2022");
        Date endDate = dateFormat.parse("02-07-2022");
        Mockito.when(repository.findByParentProjectId(0)).thenReturn(testSprintList);
        boolean result = sprintService.areNewSprintDatesValid(startDate, endDate, 0);
        assertEquals(false, result);
    }

    @Test
    void areNewSprintDatesValidWithEndBeforeStart() throws ParseException {
        Date startDate = dateFormat.parse("01-07-2022");
        Date endDate = dateFormat.parse("01-06-2022");
        Mockito.when(repository.findByParentProjectId(0)).thenReturn(testSprintList);
        boolean result = sprintService.areNewSprintDatesValid(startDate, endDate, 0);
        assertEquals(false, result);
    }

}
