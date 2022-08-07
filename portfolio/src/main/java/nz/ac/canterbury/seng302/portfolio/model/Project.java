package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Deadline;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * The entity representation for the Project and project table
 */
@Entity // this is an entity, assumed to be in a table called Project
public class Project {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "project_name")
    private String projectName;
    @Column(name = "project_description", length=4096)
    private String projectDescription;
    @Column(name = "project_start_date")
    private Date projectStartDate;
    @Column(name = "project_end_date")
    private Date projectEndDate;
    @OneToMany(mappedBy = "parentProject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected List<Deadline> deadlines;

    public Project() {}

    // These three constructors are just used to parse different date types Date, LocalDate, and String
    public Project(String projectName, String projectDescription, Date projectStartDate, Date projectEndDate) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectStartDate = projectStartDate;
        this.projectEndDate = projectEndDate;
    }

    public Project(String projectName, String projectDescription, LocalDate projectStartDate, LocalDate projectEndDate) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        // Convert local date to regular date
        ZoneId defaultZoneId = ZoneId.systemDefault();
        this.projectStartDate = Date.from(projectStartDate.atStartOfDay(defaultZoneId).toInstant());
        this.projectEndDate = Date.from(projectEndDate.atStartOfDay(defaultZoneId).toInstant());
    }

    public Project(String projectName, String projectDescription, String projectStartDate, String projectEndDate) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectStartDate = DateParser.stringToDate(projectStartDate);
        this.projectEndDate = DateParser.stringToDate(projectEndDate);
    }

    @Override
    public String toString() {
        return String.format(
                "Project[id=%d, projectName='%s', projectStartDate='%s', projectEndDate='%s', projectDescription='%s']",
                id, projectName, projectStartDate, projectEndDate, projectDescription);
    }

    /* Getters/Setters */

    public int getId(){
        return  id;
    }

    public void setId(int newId) {
        this.id = newId;
    }

    public String getName() {
        return projectName;
    }

    public void setName(String newName) {
        this.projectName = newName;
    }

    public String getDescription(){
        return projectDescription;
    }

    public void setDescription(String newDescription) {
        this.projectDescription = newDescription;
    }

    /* Dates have string get/set methods to interact with view */

    public Date getStartDate() {
        return projectStartDate;
    }

    public String getStartDateString() {
        return DateParser.dateToString(this.projectStartDate);
    }

    public String getStartDateStringHtml() {
        return DateParser.dateToStringHtml(this.projectStartDate);
    }

    public void setStartDate(Date newStartDate) {
        this.projectStartDate = newStartDate;
    }

    public void setStartDateString(String date) {
        this.projectStartDate = DateParser.stringToDate(date);
    }

    public Date getEndDate() {
        return projectEndDate;
    }

    public String getEndDateString() {
        return DateParser.dateToString(this.projectEndDate);
    }

    public String getEndDateStringHtml() {
        return DateParser.dateToStringHtml(this.projectEndDate);
    }

    public LocalDate getLocalStartDate() {
        return LocalDate.ofInstant(projectStartDate.toInstant(), ZoneId.systemDefault());
    }

    public LocalDate getLocalEndDate() {
        return projectEndDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }

    public void setEndDate(Date newEndDate) {
        this.projectEndDate = newEndDate;
    }

    public void setEndDateString(String date) {
        this.projectEndDate = DateParser.stringToDate(date);
    }
}
