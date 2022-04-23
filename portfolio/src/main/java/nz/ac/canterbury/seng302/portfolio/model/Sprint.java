package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.persistence.Column;

/**
 * The entity representation for the Sprint and sprint table
 */
@Entity // this is an entity, assumed to be in a table called Sprint
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int parentProjectId;
    private String sprintName;
    private String sprintLabel;
    @Column(name = "sprint_description", length=4096)
    private String sprintDescription;
    private Date sprintStartDate;
    private Date sprintEndDate;

    protected Sprint() {}

    public Sprint(int parentProjectId, String sprintName, String sprintLabel, String sprintDescription, Date sprintStartDate, Date sprintEndDate) {
        this.parentProjectId = parentProjectId;
        this.sprintName = sprintName;
        this.sprintLabel = sprintLabel;
        this.sprintDescription = sprintDescription;
        this.sprintStartDate = sprintStartDate;
        this.sprintEndDate = sprintEndDate;
    }

    public Sprint(int parentProjectId, String sprintName, String sprintLabel, String sprintDescription, LocalDate sprintStartDate, LocalDate sprintEndDate) {
        this.parentProjectId = parentProjectId;
        this.sprintName = sprintName;
        this.sprintLabel = sprintLabel;
        this.sprintDescription = sprintDescription;
        ZoneId defaultZoneId = ZoneId.systemDefault();
        this.sprintStartDate = Date.from(sprintStartDate.atStartOfDay(defaultZoneId).toInstant());
        this.sprintEndDate = Date.from(sprintEndDate.atStartOfDay(defaultZoneId).toInstant());
    }


    public Sprint(int parentProjectId, String sprintName, String sprintLabel, String sprintDescription, String projectStartDate, String projectEndDate) {
        this.parentProjectId = parentProjectId;
        this.sprintName = sprintName;
        this.sprintLabel = sprintLabel;
        this.sprintDescription = sprintDescription;
        this.sprintStartDate = Project.stringToDate(projectStartDate);
        this.sprintEndDate = Project.stringToDate(projectEndDate);
    }

    @Override
    public String toString() {
        return String.format(
                "Sprint[id=%d, parentProjectId='%d', sprintName='%s', sprintLabel='%s', sprintStartDate='%s', sprintEndDate='%s', sprintDescription='%s']",
                id, parentProjectId, sprintName, sprintLabel, sprintStartDate, sprintEndDate, sprintDescription);
    }


    public int getId(){
        return  id;
    }
    public int getParentProjectId() {
        return parentProjectId;
    }
    public String getName() {
        return sprintName;
    }
    public String getLabel() {
        return sprintLabel;
    }
    public void setLabel(String label) {
        this.sprintLabel = label;
    }
    public String getDescription(){
        return sprintDescription;
    }

    public void setName(String newName) {
        this.sprintName = newName;
    }
    public void setDescription(String newDescription){
        this.sprintDescription = newDescription;
    }

    /**
     * Gets the string form of the given date in
     *
     * @param date the date to convert
     * @return the given date, as a string in format 01/Jan/2000
     */
    static String dateToString(Date date) {
        return new SimpleDateFormat("dd/MMM/yyyy").format(date);
    }

    // these two setters set the dates with strings
    public void setStartDateStringSprint(String date) {
        this.sprintStartDate = Project.stringToDate(date);
    }

    public void setEndDateStringSprint(String date) {
        this.sprintEndDate = Project.stringToDate(date);
    }

    // the two setters in here set the date with Dates
    public Date getStartDate() {
        return sprintStartDate;
    }

    public String getStartDateString() {
        return Project.dateToString(this.sprintStartDate);
    }

    public void setStartDate(Date newStartDate) {
        this.sprintStartDate = newStartDate;
    }

    public Date getEndDate() {
        return sprintEndDate;
    }

    public String getEndDateString() {
        return Project.dateToString(this.sprintEndDate);
    }

    public void setEndDate(Date newEndDate) {
        this.sprintEndDate = newEndDate;
    }
}
