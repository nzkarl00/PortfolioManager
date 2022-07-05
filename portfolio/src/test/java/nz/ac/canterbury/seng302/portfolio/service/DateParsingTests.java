package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DateParsingTests {

    static Date testDate;

    @BeforeAll
    static void setup() {
        Calendar c = Calendar.getInstance();
        c.set(2022, 5, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        testDate = new Date(seconds);
    }

    @Test
    void displayDateCurrentMonth() {
        String expectedString = " 28 June 2022";
        Calendar c = Calendar.getInstance();
        c.set(2022, 5, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build(), testDate));
    }

    @Test
    void displayDatePastMonth() {
        String expectedString = " 28 May 2022 (1 Month)";
        Calendar c = Calendar.getInstance();
        c.set(2022, 4, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build(), testDate));
    }

    @Test
    void displayDateLongPastMonth() {
        String expectedString = " 28 January 2022 (5 Months)";
        Calendar c = Calendar.getInstance();
        c.set(2022, 0, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build(), testDate));
    }

    @Test
    void displayDateFutureMonth() {
        String expectedString = " 28 December 2022";
        Calendar c = Calendar.getInstance();
        c.set(2022, 11, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build(), testDate));
    }

    @Test
    void displayDateCrossingMonth() {
        String expectedString = " 28 December 2021 (6 Months)";
        Calendar c = Calendar.getInstance();
        c.set(2021, 11, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
                Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build(), testDate));
    }

    @Test
    void displayDateFutureYear() {
        String expectedString = " 28 January 2021 (1 Year and 5 Months)";
        Calendar c = Calendar.getInstance();
        c.set(2021, 0, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
                Timestamp.newBuilder().setSeconds(seconds / 1000));
        assertEquals(expectedString, DateParser.displayDate(response.build(), testDate));
    }

    @Test
    void displayDateFutureYears() {
        String expectedString = " 28 January 2020 (2 Years and 5 Months)";
        Calendar c = Calendar.getInstance();
        c.set(2020, 0, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build(), testDate));
    }

    @Test
    void stringToDateDayFirst() {
        Calendar c = Calendar.getInstance();
        c.set(2022, 10, 28, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        Long seconds = c.getTime().getTime();
        Date expectedDate = new Date(seconds);
        Date actualDate = DateParser.stringToDate("28/Nov/2022");
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

    Sprint sprint1 = new Sprint(1, "sprint1", "label", "description", new Date(),
        new Date(new Date().getTime() + 60 * 60 * 24 * 31 * 1)); // now to 1 month in the future
    Sprint sprint2 = new Sprint(1, "sprint2", "label", "description",
        new Date(new Date().getTime() + 60 * 60 * 24 * 31 * 1),
        new Date(new Date().getTime() + 60 * 60 * 24 * 31 * 2)); // 1 month to 2 months in the future
    Sprint sprint3 = new Sprint(1, "sprint3", "label", "description",
        new Date(new Date().getTime() + 60 * 60 * 24 * 31 * 2),
        new Date(new Date().getTime() + 60 * 60 * 24 * 31 * 3)); // 2 month to 3 months in the future
    Sprint sprint4 = new Sprint(1, "sprint4", "label", "description",
        new Date(new Date().getTime() + 60 * 60 * 24 * 31 * 3),
        new Date(new Date().getTime() + 60 * 60 * 24 * 31 * 4)); // 3 month to 4 months in the future
    Sprint sprint5 = new Sprint(1, "sprint5", "label", "description",
        new Date(new Date().getTime() + 60 * 60 * 24 * 31 * 4),
        new Date(new Date().getTime() + 60 * 60 * 24 * 31 * 5)); // 4 month to 5 months in the future
    ArrayList<Sprint> sprints = new ArrayList<>();
    {
        sprint1.setId(1);
        sprint2.setId(2);
        sprint3.setId(3);
        sprint4.setId(4);
        sprint5.setId(5);
        sprints.add(sprint1);
        sprints.add(sprint2);
        sprints.add(sprint3);
        sprints.add(sprint4);
        sprints.add(sprint5);
    }

    @Test
    void sprintDateCheckBlueSky() {
        // testing with the existing working dates
        assertTrue(DateParser.sprintDateCheck(sprints, sprint1, sprint1.getStartDate(), sprint1.getEndDate()));
    }

    @Test
    void sprintDateCheckEndFail() {
        // Testing this part of the code
        // (temp.getStartDate().after(checkStartDate) && temp.getStartDate().before(checkEndDate))
        assertFalse(DateParser.sprintDateCheck(sprints, sprint1, sprint1.getStartDate(),
            new Date(sprint1.getEndDate().getTime() + 1000 * 60 * 60 * 24 * 5))); // 5 days in the future
    }

    @Test
    void sprintDateCheckStartFail() {
        // Testing this part of the code
        //(temp.getEndDate().after(checkStartDate) && temp.getEndDate().before(checkEndDate))
        assertFalse(DateParser.sprintDateCheck(sprints, sprint2, new Date(sprint2.getStartDate().getTime() - 1000 * 60 * 60 * 24 * 5), // 5 days in the past
            sprint2.getEndDate()));
    }

    @Test
    void sprintDateCheckBetweenFail() {
        // Testing this part of the code
        // (temp.getStartDate().after(checkStartDate) && temp.getEndDate().before(checkEndDate))
        assertFalse(DateParser.sprintDateCheck(sprints, sprint1,
            new Date(sprint2.getStartDate().getTime() - 1000 * 60 * 60 * 24 * 5), // 5 days in the past
            new Date(sprint2.getEndDate().getTime() + 1000 * 60 * 60 * 24 * 5))); // 5 days in the future
    }

    @Test
    void sprintDateCheckInsideFail() {
        // Testing this part of the code
        // (temp.getStartDate().before(checkStartDate) && temp.getEndDate().after(checkEndDate))
        assertFalse(DateParser.sprintDateCheck(sprints, sprint1,
            new Date(sprint2.getStartDate().getTime() + 1000 * 60 * 60 * 24 * 5), // 5 days in the future
            new Date(sprint2.getEndDate().getTime() - 1000 * 60 * 60 * 24 * 5))); // 5 days in the past
    }



    @Test
    void convertStringToLocalDateTime() {
        String expected = "2022-06-23T02:00";
        LocalDateTime actual = DateParser.stringToLocalDateTime("2022-06-23", "02:00");
        assertEquals(expected, actual.toString());
    }

    @Test
    void convertStringToLocalDateTimeWithDifferentFormat() {
        String expected = "2022-06-23T02:00";
        LocalDateTime actual = DateParser.stringToLocalDateTime("23/Jun/2022", "02:00");
        assertEquals(expected, actual.toString());
    }

    @Test
    void convertStringToLocalDateTimeWithNoTime() {
        String expected = "2022-06-23T00:00";
        LocalDateTime actual = DateParser.stringToLocalDateTime("2022-06-23", null);
        assertEquals(expected, actual.toString());
    }

    @Test
    void convertStringToLocalDateTimeWithWrongParametersPassed() {;
        LocalDateTime actual = DateParser.stringToLocalDateTime("23/Jun/2022J", "02:00");
        assertNull(actual);
    }

}