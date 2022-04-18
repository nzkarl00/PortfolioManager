package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import java.util.Calendar;
import java.util.Date;

import static org.aspectj.bridge.Version.getTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DateParsingTests {

    @Test
    void displayDateCurrentMonth() {
        String expectedString = "28 April 2022";
        Calendar c = Calendar.getInstance();
        c.set(2022, 3, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build()));
    }

    @Test
    void displayDatePastMonth() {
        String expectedString = "28 March 2022 (1 Month)";
        Calendar c = Calendar.getInstance();
        c.set(2022, 2, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build()));
    }

    @Test
    void displayDateLongPastMonth() {
        String expectedString = "28 December 2021 (4 Months)";
        Calendar c = Calendar.getInstance();
        c.set(2021, 11, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build()));
    }

    @Test
    void displayDateFutureMonth() {
        String expectedString = "28 November 2022";
        Calendar c = Calendar.getInstance();
        c.set(2022, 10, 28, 0, 0);
        Long seconds = c.getTime().getTime();
        UserResponse.Builder response = UserResponse.newBuilder().setCreated(
            Timestamp.newBuilder().setSeconds(seconds/1000));
        assertEquals(expectedString, DateParser.displayDate(response.build()));
    }
}