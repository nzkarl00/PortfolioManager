package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectItemService {
    @Autowired
    DeadlineRepository deadlineRepository;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    MilestoneRepository milestoneRepository;

    public ProjectTimeBoundItem getProjectItemByIdAndType(String type, Integer id) throws Exception {
        Optional<? extends ProjectTimeBoundItem> item = null;
        switch (type) {
            case "Deadline":
                item = deadlineRepository.findById(id);
                break;
            case "Milestone":
                item = milestoneRepository.findById(id);
                break;
            case "Event":
                item = eventRepository.findById(id);
                break;
        }
        if(item!=null) {
            return item.get();
        }
        else
        {
            throw new Exception("Project item not found");
        }
    }

    /**
     * Get deadline by its id.
     */
    public Deadline getDeadlineById(Integer id) throws Exception {

        Optional<Deadline> deadline = deadlineRepository.findById(id);
        if(deadline!=null) {
            return deadline.get();
        }
        else
        {
            throw new Exception("Deadline not found");
        }
    }

    /**
     * Get event by its id.
     */
    public Event getEventById(Integer id) throws Exception {

        Optional<Event> event = eventRepository.findById(id);
        if(event!=null) {
            return event.get();
        }
        else
        {
            throw new Exception("Event not found");
        }
    }

    /**
     * Get milestone by its id.
     */
    public Milestone getMilestoneById(Integer id) throws Exception {

        Optional<Milestone> milestone = milestoneRepository.findById(id);
        if(milestone!=null) {
            return milestone.get();
        }
        else
        {
            throw new Exception("Milestone not found");
        }
    }

    /**
     * Saves deadline edits to the repository
     */
    public void saveDeadlineEdit(Deadline deadline) {
        deadlineRepository.save(deadline);
    }

    /**
     * Saves event edits to the repository
     */
    public void saveEventEdit(Event event) {
        eventRepository.save(event);
    }


    /**
     * Saves milestone edits to the repository
     */
    public void saveMilestoneEdit(Milestone milestone) {
        milestoneRepository.save(milestone);
    }
}
