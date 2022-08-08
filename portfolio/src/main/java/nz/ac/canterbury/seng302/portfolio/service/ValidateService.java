package nz.ac.canterbury.seng302.portfolio.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateService {

    /**
     * Basic text checker, checks there are at least 3 non-special characters from any language
     * @param text The text to check
     */
    public static void validateEnoughCharacters(String text) {
        int charCount = 0;
        //This pattern will match characters from any language, note it also excludes whitespace
        //https://stackoverflow.com/a/64293069
        Pattern charPattern = Pattern.compile("[\\p{L}]");
        //Traverse the string and check each character individually
        for(int i = 0; i < text.length(); i++) {
            String charString = String.valueOf(text.charAt(i));
            Matcher charMatcher = charPattern.matcher(charString);
            if(charMatcher.matches()) {
                charCount++;
            }
        }
        //If there are not enough non-special characters, throw an error
        if(charCount < 3) {
          throw new IllegalArgumentException("Text does not contain enough non-special characters");
        }
    }
}
