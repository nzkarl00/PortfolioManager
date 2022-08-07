package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.*;
import nz.ac.canterbury.seng302.portfolio.model.websocket.EditStatusUpdate;
import nz.ac.canterbury.seng302.portfolio.model.websocket.EventUpdate;
import nz.ac.canterbury.seng302.portfolio.model.websocket.FetchUpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DateSocketService {
    @Autowired
    private SprintRepository repository;
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Send an update sprint message through websockets to all the users on the same project details page
     */
    public void sendSprintCalendarChange(int id) {
        this.template.convertAndSend("/topic/calendar/" + id, new EventUpdate(FetchUpdateType.SPRINT));
    }

    /**
     * Send an update event message through websockets to all the users on the same project details page
     */
    public void sendEventCalendarChange(Project project, Event event) {
        List<Sprint> sprints = repository.findByParentProjectId(project.getId());
        // loop through sprints
        for (Sprint sprint: sprints) {
            LocalDateTime startDate = DateParser.convertToLocalDateTime(sprint.getStartDate());
            LocalDateTime endDate = DateParser.convertToLocalDateTime(sprint.getEndDate());
            // if deadline is within sprint
            if (event.getEndDate().isAfter(startDate) && event.getStartDate().isBefore(endDate)) {
                /// send a deadline update
                this.template.convertAndSend("/topic/calendar/" + project.getId()
                        , new EventUpdate(FetchUpdateType.EVENT, sprint.getId()));
            }
        }
    }

    /**
     * Send an update deadline message through websockets to all the users on the same project details page
     */
    public void sendDeadlineCalendarChange(Project project, Deadline deadline) {
        List<Sprint> sprints = repository.findByParentProjectId(project.getId());
        // loop through sprints
        for (Sprint sprint: sprints) {
            LocalDateTime startDate = DateParser.convertToLocalDateTime(sprint.getStartDate());
            LocalDateTime endDate = DateParser.convertToLocalDateTime(sprint.getEndDate());
            // if deadline is within sprint
            if (deadline.getEndDate().isAfter(startDate) && deadline.getStartDate().isBefore(endDate)) {
                /// send a deadline update
                this.template.convertAndSend("/topic/calendar/" + project.getId()
                        , new EventUpdate(FetchUpdateType.DEADLINE, sprint.getId()));
            }
        }
    }

    /**
     * Send an update milestone message through websockets to all the users on the same project details page
     */
    public void sendMilestoneCalendarChange(Project project, Milestone milestone) {
        List<Sprint> sprints = repository.findByParentProjectId(project.getId());
        // loop through sprints
        for (Sprint sprint: sprints) {
            LocalDateTime startDate = DateParser.convertToLocalDateTime(sprint.getStartDate());
            LocalDateTime endDate = DateParser.convertToLocalDateTime(sprint.getEndDate());
            // if deadline is within sprint
            if (milestone.getEndDate().isAfter(startDate) && milestone.getStartDate().isBefore(endDate)) {
                /// send a deadline update
                this.template.convertAndSend("/topic/calendar/" + project.getId()
                        , new EventUpdate(FetchUpdateType.MILESTONE, sprint.getId()));
            }
        }
    }

    /**
     * Send an update event message through websockets to all the users on the same project details page
     */
    public void sendEventCalendarChange(Project project) {
        List<Sprint> sprints = repository.findByParentProjectId(project.getId());

        for (Sprint sprint: sprints) {
            this.template.convertAndSend("/topic/calendar/" + project.getId()
                    , new EventUpdate(FetchUpdateType.EVENT, sprint.getId()));
        }
    }
    /**
     * Send an update deadline message through websockets to all the users on the same project details page
     */
    public void sendDeadlineCalendarChange(Project project) {
        List<Sprint> sprints = repository.findByParentProjectId(project.getId());

        for (Sprint sprint: sprints) {
            this.template.convertAndSend("/topic/calendar/" + project.getId()
                    , new EventUpdate(FetchUpdateType.DEADLINE, sprint.getId()));
        }
    }
    /**
     * Send an update milestone message through websockets to all the users on the same project details page
     */
    public void sendMilestoneCalendarChange(Project project) {
        List<Sprint> sprints = repository.findByParentProjectId(project.getId());

        for (Sprint sprint: sprints) {
            this.template.convertAndSend("/topic/calendar/" + project.getId()
                    , new EventUpdate(FetchUpdateType.MILESTONE, sprint.getId()));
        }
    }

    /**
     * Sends the editing status of a project as a string
     */
    public void sendEditStatus(Project project, String message) {
        this.template.convertAndSend("/topic/calendar/" + project.getId(), new EditStatusUpdate(FetchUpdateType.EDIT, message));
    }
}
