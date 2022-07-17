package nz.ac.canterbury.seng302.portfolio.common;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public final class CommonProjectItems {
    public static MultiValueMap<String, String> validParamsDeadline = getValid();
    public static MultiValueMap<String, String> validParamsEvent = getValidEvent();

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

    private static MultiValueMap<String, String> getValidEvent() {
        MultiValueMap<String, String> ret = new LinkedMultiValueMap<String, String>();
        ret.add("projectId", String.valueOf(0));
        ret.add("dateId", String.valueOf(0));
        ret.add("eventName", "Event 1");
        ret.add("eventType", "Event");
        ret.add("eventStartDate", "2022-05-04T16:20");
        ret.add("eventEndDate", "2022-06-04T16:20");
        ret.add("eventDescription", "This is an event for project 1");
        return ret;
    }

    public static MultiValueMap<String, String> deadlineBeforeProjectParams = getBefore();
    public static MultiValueMap<String, String> eventBeforeProjectParams = getBeforeForEvent();

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

    private static MultiValueMap<String, String> getBeforeForEvent() {
        MultiValueMap<String, String> ret = new LinkedMultiValueMap<String, String>();
        ret.add("projectId", String.valueOf(0));
        ret.add("dateId", String.valueOf(0));
        ret.add("eventName", "Event 1");
        ret.add("eventType", "Event");
        ret.add("eventStartDate", "2022-04-01T00:00");
        ret.add("eventEndDate", "2022-05-10T00:00");
        ret.add("eventDescription", "This is an event for project 1");
        return ret;
    }

    public static MultiValueMap<String, String> deadlineAfterProjectParams = getAfter();
    public static MultiValueMap<String, String> eventAfterProjectParams = getAfterForEvent();

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

    private static MultiValueMap<String, String> getAfterForEvent() {
        MultiValueMap<String, String> ret = new LinkedMultiValueMap<String, String>();
        ret.add("projectId", String.valueOf(0));
        ret.add("dateId", String.valueOf(0));
        ret.add("eventName", "Event 1");
        ret.add("eventType", "Event");
        ret.add("eventStartDate", "2023-07-01T00:01");
        ret.add("eventEndDate", "2023-07-02T00:01");
        ret.add("eventDescription", "This is an event for project 1");
        return ret;
    }

    public static MultiValueMap<String, String> eventStartDateAfterEndDate = getStartDateAfterEndDateForEvent();

    private static MultiValueMap<String, String> getStartDateAfterEndDateForEvent() {
        MultiValueMap<String, String> ret = new LinkedMultiValueMap<String, String>();
        ret.add("projectId", String.valueOf(0));
        ret.add("dateId", String.valueOf(0));
        ret.add("eventName", "Event 1");
        ret.add("eventType", "Event");
        ret.add("eventStartDate", "2022-05-20T00:01");
        ret.add("eventEndDate", "2022-05-12T00:01");
        ret.add("eventDescription", "This is an event for project 1");
        return ret;
    }

}
