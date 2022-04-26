package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.junit.jupiter.api.Test;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.mockito.Mock;

import java.util.Calendar;
import java.util.Date;

import static org.aspectj.bridge.Version.getTime;
import static org.junit.jupiter.api.Assertions.*;

class DateParsingTests {

    @Test
    void displayDateCurrentMonth() {
        String expectedString = " 28 April 2022";
        Calendar c = Calendar.getInstance();
        c.set(2022, 3, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build()));
    }

    @Test
    void displayDatePastMonth() {
        String expectedString = " 28 March 2022 (1 Month)";
        Calendar c = Calendar.getInstance();
        c.set(2022, 2, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build()));
    }

    @Test
    void displayDateLongPastMonth() {
        String expectedString = " 28 December 2021 (4 Months)";
        Calendar c = Calendar.getInstance();
        c.set(2021, 11, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build()));
    }

    @Test
    void displayDateFutureMonth() {
        String expectedString = " 28 November 2022";
        Calendar c = Calendar.getInstance();
        c.set(2022, 10, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build()));
    }

    @Test
    void displayDateFutureYear() {
        String expectedString = " 28 December 2020 (1 Year and 4 Months)";
        Calendar c = Calendar.getInstance();
        c.set(2020, 11, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build()));
    }

    @Test
    void displayDateFutureYears() {
        String expectedString = " 28 December 2019 (2 Years and 4 Months)";
        Calendar c = Calendar.getInstance();
        c.set(2019, 11, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build()));
    }

    @Test
    void stringToDateDayFirst() {
        Calendar c = Calendar.getInstance();
        c.set(2022, 10, 28, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        Long seconds = c.getTime().getTime();
        Date expectedDate = new Date(seconds);
        Date actualDate = DateParser.stringToDate("28/Nov/2022");
        System.out.println(expectedDate); // for debugging as the .getTime() give gross numbers
        System.out.println(actualDate);
        assertEquals(expectedDate.getTime(), actualDate.getTime());
    }

    @Test
    void stringToDateYearFirst() {
        Calendar c = Calendar.getInstance();
        c.set(2022, 10, 28, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        Long seconds = c.getTime().getTime();
        Date expectedDate = new Date(seconds);
        Date actualDate = DateParser.stringToDate("2022-11-28");
        System.out.println(expectedDate); // for debugging as the .getTime() give gross numbers
        System.out.println(actualDate);
        assertEquals(expectedDate.getTime(), actualDate.getTime());
    }
    @Test
    void stringToDateFail() {
        Date actualDate = DateParser.stringToDate("2022/11-28");
        assertNull(actualDate);
    }

    @Test
    void dateToStringTest() {
        String expected = "28/Nov/2022";
        Calendar c = Calendar.getInstance();
        c.set(2022, 10, 28, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        Long seconds = c.getTime().getTime();
        Date date = new Date(seconds);
        String actual = DateParser.dateToString(date);
        assertEquals(expected, actual);
    }

    @Test
    void dateToStringHtmlTest() {
        String expected = "2022-11-28";
        Calendar c = Calendar.getInstance();
        c.set(2022, 10, 28, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        Long seconds = c.getTime().getTime();
        Date date = new Date(seconds);
        String actual = DateParser.dateToStringHtml(date);
        assertEquals(expected, actual);
    }

    @Mock
    ProjectRepository projectRepository;

    @Mock
    SprintRepository sprintRepository;

    @Test
    void sprintDateCheckBlueSky() {
        assertTrue(true);
    }
}