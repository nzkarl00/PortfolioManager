package nz.ac.canterbury.seng302.portfolio.model;

public class Validate {

    public static void validateText(String text) {
        //Traverse the string
        int character_count = 0;
        for(int i = 0, i < text.length(); i++) {
            //Check
            if()
                character_count++;
        }
        //Check there are at least 3 non-special characters
        if(character_count < 3) {
            throw new IllegalArgumentException("Text does not contain enough characters");
        }

    }
}
