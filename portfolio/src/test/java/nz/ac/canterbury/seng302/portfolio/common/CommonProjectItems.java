package nz.ac.canterbury.seng302.portfolio.common;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public final class CommonProjectItems {
    public static MultiValueMap<String, String> validParamsDeadline = getValid();

    private static MultiValueMap<String, String> getValid() {
        MultiValueMap<String, String> ret = new LinkedMultiValueMap<String, String>();
        ret.add("projectId", String.valueOf(0));
        ret.add("dateId", String.valueOf(0));
        ret.add("eventName", "Deadline 1");
        ret.add("eventType", "Deadline");
        ret.add("eventStartDate", "2022-05-04");
        ret.add("eventEndDate", "16:20");
        ret.add("eventDescription", "This is a deadline for project 1");
        return ret;
    }

    public static MultiValueMap<String, String> deadlineBeforeProjectParams = getBefore();

    private static MultiValueMap<String, String> getBefore() {
        MultiValueMap<String, String> ret = new LinkedMultiValueMap<String, String>();
        ret.add("projectId", String.valueOf(0));
        ret.add("dateId", String.valueOf(0));
        ret.add("eventName", "Deadline 1");
        ret.add("eventType", "Deadline");
        ret.add("eventStartDate", "2022-04-01");
        ret.add("eventEndDate", "16:20");
        ret.add("eventDescription", "This is a deadline for project 1");
        return ret;
    }

    public static MultiValueMap<String, String> deadlineAfterProjectParams = getAfter();

    private static MultiValueMap<String, String> getAfter() {
        MultiValueMap<String, String> ret = new LinkedMultiValueMap<String, String>();
        ret.add("projectId", String.valueOf(0));
        ret.add("dateId", String.valueOf(0));
        ret.add("eventName", "Deadline 1");
        ret.add("eventType", "Deadline");
        ret.add("eventStartDate", "2022-07-01");
        ret.add("eventEndDate", "16:20");
        ret.add("eventDescription", "This is a deadline for project 1");
        return ret;
    }
}
