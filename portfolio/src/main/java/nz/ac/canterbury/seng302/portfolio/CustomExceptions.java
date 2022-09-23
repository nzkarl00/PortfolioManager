package nz.ac.canterbury.seng302.portfolio;

public class CustomExceptions {
    public static class ProjectItemTypeException extends Exception {
        public ProjectItemTypeException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class ProjectItemNotFoundException extends Exception {
        public ProjectItemNotFoundException(String errorMessage) { super(errorMessage); }
    }


}
