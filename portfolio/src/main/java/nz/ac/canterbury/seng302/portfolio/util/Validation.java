package nz.ac.canterbury.seng302.portfolio.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    public static final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?>\\.[a-zA-Z0-9_+&*-]+)*@(?>[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern emailPattern = Pattern.compile(emailRegex);

    /**
     * Validate if a supplied email is of valid format.
     * Uses OWASP regex for matching emails.
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        // Introduce max length for email, for security purposes.
        if (email.length() > 200) {
            return false;
        }
        
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }
}
