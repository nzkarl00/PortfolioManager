package nz.ac.canterbury.seng302.portfolio.common;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceTag;
import nz.ac.canterbury.seng302.portfolio.model.evidence.SkillTag;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.*;

public final class CommonProjectItems {

    public static Project getValidProject() {
        Date april1 = DateParser.stringToDate("2022-05-01");
        Date may1 = DateParser.stringToDate("2022-10-01");
        Project testProject = new Project("testName", "testDescription", april1, may1);
        return testProject;
    }

    public static Evidence getValidEvidence() {
        Project testProject = getValidProject();
        LocalDate may4 = LocalDate.parse("2022-05-04");
        Evidence evidence = new Evidence(0, testProject, "Evidence One", "This evidence is the first to be submitted", may4, 0);

        return evidence;
    }

    public static final List<Integer> testDeleteSkillIDs = List.of(1);
    private static final List<String> testSkillsToAdd = List.of("Skill1", "Skill2");
    private static final HashMap<Integer, String> testSkillsToChange = new HashMap<Integer, String>() {{put(1, "Skill");}};

    public static EvidenceService.ParsedEditSkills getParsedEditSkillsAddSkill() {
        return new EvidenceService.ParsedEditSkills(testSkillsToAdd, Collections.emptyList(), new HashMap<>());
    }

    public static EvidenceService.ParsedEditSkills getParsedEditSkillsRemoveSkill() {
        return new EvidenceService.ParsedEditSkills(Collections.emptyList(), testDeleteSkillIDs, new HashMap<>());
    }

    public static SkillTag getNoSkillsSkillTag() {
        return new SkillTag(getValidProject(), "No_skills");
    }

    public static SkillTag getSkillTagA() {
        return new SkillTag(getValidProject(), "A");
    }

    public static SkillTag getSkillTagB() {
        return new SkillTag(getValidProject(), "B");
    }

    public static SkillTag getSkillTagC() {
        return new SkillTag(getValidProject(), "C");
    }

    public static SkillTag getSkillTagAlpha() {
        return new SkillTag(getValidProject(), "Alpha");
    }

    public static SkillTag getSkillTagAlphaLowercase() {
        return new SkillTag(getValidProject(), "alpha");
    }

    public static EvidenceTag getNoSkillsEvidenceTag(Evidence e) {
        SkillTag skillTag = getNoSkillsSkillTag();
        EvidenceTag evidenceTag = new EvidenceTag(skillTag, e);
        skillTag.setEvidenceTags(List.of(evidenceTag));
        return evidenceTag;
    }

    public static EvidenceTag getEvidenceTagA(Evidence e) {
        SkillTag skillTag = getSkillTagA();
        EvidenceTag evidenceTag = new EvidenceTag(skillTag, e);
        skillTag.setEvidenceTags(List.of(evidenceTag));
        return evidenceTag;
    }

    public static EvidenceTag getEvidenceTagANotUnique(Evidence e) {
        SkillTag skillTag = getSkillTagA();
        EvidenceTag evidenceTag = new EvidenceTag(skillTag, e);
        skillTag.setEvidenceTags(List.of(evidenceTag, new EvidenceTag()));
        return evidenceTag;
    }

    public static EvidenceTag getEvidenceTagB(Evidence e) {
        SkillTag skillTag = getSkillTagB();
        EvidenceTag evidenceTag = new EvidenceTag(skillTag, e);
        skillTag.setEvidenceTags(List.of(evidenceTag));
        return evidenceTag;
    }

    public static EvidenceTag getEvidenceTagC(Evidence e) {
        SkillTag skillTag = getSkillTagC();
        EvidenceTag evidenceTag = new EvidenceTag(skillTag, e);
        skillTag.setEvidenceTags(List.of(evidenceTag));
        return evidenceTag;
    }

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
