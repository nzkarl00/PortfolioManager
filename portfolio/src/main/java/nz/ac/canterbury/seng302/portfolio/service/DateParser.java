package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Sprint;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for parsing and processing dates
 */
public class DateParser {

    public static String sprintIdFail;

    private static final Pattern datePattern = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$");

    /**
     * It takes a UserResponse and outputs a readable date string.
     * @param userReply The UserResponse with a date to parse
     * @return the readable string
     */
    public static String displayDate(UserResponse userReply, Date currentDate) {
        Long seconds = userReply.getCreated().getSeconds(); //Format the date
        Date date = new Date(seconds * 1000); // turn into millis
        SimpleDateFormat dateFormat = new SimpleDateFormat( "dd LLLL yyyy" ); //Correct string format
        String stringDate = " " + dateFormat.format( date );
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH); //Month registered variables
        int year = cal.get(Calendar.YEAR);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        int totalMonth = (currentMonth - month) + 12 * (currentYear - year);
        boolean monthsCross = month > currentMonth;
        int totalYear = monthsCross?(currentYear - year - 1) : currentYear - year;

        if (totalMonth > 0){
            stringDate += " (";

            // put the years in if they apply
            if (totalYear > 0){
                if (totalYear > 1){
                    stringDate += totalYear + " Years";
                } else {
                    stringDate += totalYear + " Year";
                }
            }
            totalMonth %= 12;
            // if ther are both months and year fields put an and in
            if (totalYear > 0 && totalMonth != 0) {
                stringDate += " and ";
            }

            // put the appropriate month field in
            if (totalMonth > 1) {
                stringDate += totalMonth + " Months";
            } else if (totalMonth == 1) {
                stringDate += totalMonth + " Month";
            }

            stringDate += ")";
        }
        return stringDate;
    }

    /**
     * Gets the date form of the given date string
     *
     * @param dateString the string to read as a date in format 01/Jan/2000
     * @return the given date, as a date object
     */
    public static Date stringToDate(String dateString) {
        Date date = null;
        try {
            return new SimpleDateFormat("dd/MMM/yyyy").parse(dateString);
        } catch (Exception e1) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            } catch (Exception e2) {
                System.err.println("Error parsing date: " + e1.getMessage() + e2.getMessage());
            }
        }
        return null;
    }

    /**
     * Gets a string date and time to be converted to LocalDateTime
     *
     * @param dateString the string to read as a date in format "yyyy-MM-dd"
     * @param timeString the string to read as a time in format "hh:mm"
     * @return the given string date and time as a LocalDateTime object
     */
    public static LocalDateTime stringToLocalDateTime(String dateString, String timeString) {
        //https://mkyong.com/java8/java-8-how-to-format-localdatetime/
        if (timeString == null || timeString.isBlank()) {
            timeString = "00:00";
        }
        String dateToBeConverted = dateString + " " + timeString;
        LocalDateTime formatDateTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try {
            formatDateTime = LocalDateTime.parse(dateToBeConverted, formatter);
        } catch (Exception e1) {
            try {
                formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm");
                formatDateTime = LocalDateTime.parse(dateToBeConverted, formatter);
            } catch (Exception e2) {
                System.err.println("Error parsing date: " + e2.getMessage() + " " + e1.getMessage());
            }
        }

        return formatDateTime;
    }

    /**
     * Gets the string form of the given date in
     *
     * @param date the date to convert
     * @return the given date, as a string in format 01/Jan/2000
     */
    public static String dateToString(Date date) {
        return new SimpleDateFormat("dd/MMM/yyyy").format(date);
    }

    /**
     * Gets the string form of the given date in
     *
     * @param date the date to convert
     * @return the given date, as a string in format 01/Jan/2000
     */
    public static String dateToStringHtml(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static Boolean sprintDateCheck(List<Sprint> sprints, Sprint sprint, Date checkStartDate, Date checkEndDate) {
        // check if dates are not in other sprints timelines

        for (Sprint temp: sprints) { // loop through all the sprints

            // check to see if the looped sprint ends between proposed sprint dates
            if (((temp.getEndDate().after(checkStartDate) && temp.getEndDate().before(checkEndDate))

            // check to see if the looped sprint starts between proposed sprint dates
            || (temp.getStartDate().after(checkStartDate) && temp.getStartDate().before(checkEndDate))

            // check to see if the looped sprint is between proposed sprint dates
            || (temp.getStartDate().after(checkStartDate) && temp.getEndDate().before(checkEndDate))

            // check to see if the start date or end date are equal with temp sprint
            || (checkStartDate.equals(DateParser.stringToDate(temp.getEndDateString())))

            // check to see if the looped sprint is enclosing the proposed sprint
            || (temp.getStartDate().before(checkStartDate) && temp.getEndDate().after(checkEndDate)))

            // make sure you're not comparing to the sprint you are changing
            && temp.getId() != sprint.getId()) {
                sprintIdFail = temp.getName();
                return false;
            }
        }
        return true;
    }

    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Convert a LocalDateTime object to an old Date object
     * @param dateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Validate if a user supplied date yyyy-MM-dd is of valid format.
     * True if the date is of valid format.
     * Note this uses a basic regex and does not check for things like leap year dates, etc.
     * @param date
     * @return
     */
    public static boolean isValidDateString(String date) {
        Matcher dateMatcher = datePattern.matcher(date);
        return dateMatcher.matches();
    }
}
