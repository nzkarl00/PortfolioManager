package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.model.websocket.EditStatusUpdate;
import nz.ac.canterbury.seng302.portfolio.model.websocket.EventUpdate;
import nz.ac.canterbury.seng302.portfolio.model.websocket.FetchUpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
    public void sendEventCalendarChange(Project project) {
        this.template.convertAndSend("/topic/calendar/" + project.getId()
                , new EventUpdate(FetchUpdateType.EVENT, 1));
    }
    /**
     * Send an update deadline message through websockets to all the users on the same project details page
     */
    public void sendDeadlineCalendarChange(Project project) {
        this.template.convertAndSend("/topic/calendar/" + project.getId()
                , new EventUpdate(FetchUpdateType.DEADLINE, 1));
    }
    /**
     * Send an update milestone message through websockets to all the users on the same project details page
     */
    public void sendMilestoneCalendarChange(Project project) {
        this.template.convertAndSend("/topic/calendar/" + project.getId()
                , new EventUpdate(FetchUpdateType.MILESTONE, 1));
    }

    /**
     * Sends the editing status of a project as a string
     */
    public void sendEditStatus(Project project, String message) {
        this.template.convertAndSend("/topic/calendar/" + project.getId(), new EditStatusUpdate(FetchUpdateType.EDIT, message));
    }
}
