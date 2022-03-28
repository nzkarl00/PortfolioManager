package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateParser {

    /**
     * It takes a UserResponse and outputs a readable date string.
     * @param userReply The UserResponse with a date to parse
     * @return the readable string
     */
    public static String displayDate(UserResponse userReply) {
        Long seconds = userReply.getCreated().getSeconds(); //Format the date
        Date date = new Date(seconds * 1000); // turn into millis
        SimpleDateFormat dateFormat = new SimpleDateFormat( "dd LLLL yyyy" ); //Correct string format
        String stringDate = dateFormat.format( date );
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH); //Month registered variables
        int year = cal.get(Calendar.YEAR);
        Calendar currentCalendar = Calendar.getInstance();
        cal.setTime(new Date());
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        int totalMonth = (currentMonth - month) + 12 * (currentYear - year);

        if (totalMonth > 0){
            if (totalMonth > 1){
                stringDate += " (" + totalMonth + " Months)";
            } else {
                stringDate += " (" + totalMonth + " Month)";
            }
        }
        return stringDate;
    }
}
