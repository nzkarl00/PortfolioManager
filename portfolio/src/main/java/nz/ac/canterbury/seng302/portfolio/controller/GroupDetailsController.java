package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Controller for the display project details page
 */
@Controller
public class GroupDetailsController {

    @Autowired
    private DeadlineRepository deadlineRepo;
    @Autowired
    private MilestoneRepository milestoneRepo;
    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private DateSocketService dateSocketService;
    @Autowired
    private SprintRepository repository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private AccountClientService accountClientService;
    @Autowired
    private NavController navController;
    @Autowired
    private GroupsClientService groupsService;


    String errorShow = "display:none;";
    String errorCode = "";
    String successCalendarShow = "display:none;";
    String successCalendarCode = "";
    String errorCalendarShow = "display:none;";
    String errorCalendarCode = "";

    Logger logger = LoggerFactory.getLogger(GroupDetailsController.class);

    /**
     * Returns the html page based on the user's role
     *
     * @param principal the auth token
     * @param model     The model to be used by the application for web integration
     * @return The html page to direct to
     * @throws Exception
     */
    @GetMapping("/group")
    public String details(@AuthenticationPrincipal AuthState principal, @RequestParam(value = "id") Integer groupId, Model model) throws Exception {
        logger.info(String.format("Fetching details for group=<%s>", groupId));

        Integer id = AuthStateInformer.getId(principal);

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);
        User currentUser = new User(userReply);
        navController.updateModelForNav(principal, model, userReply, id);

        GroupDetailsResponse response = groupsService.getGroup(groupId);
        Group currentGroup = new Group(response);

        String permissionToEdit = "display:none;";
        String role = AuthStateInformer.getRole(principal);

        /* Return the name of the Thymeleaf template */
        // detects the role of the current user and returns appropriate page
        if ((response.getGroupId() == 2) || (response.getGroupId() == 1)) {
        } else if (role.equals("teacher") || role.equals("admin") || response.getMembersList().contains(userReply)) {
            permissionToEdit = "";
        }



        model.addAttribute("group", response);
        model.addAttribute("editPermission", permissionToEdit);

        return "groupDetails";
    }

    @GetMapping("/edit-group")
    public String editGroup(@AuthenticationPrincipal AuthState principal, @RequestParam(value = "id") Integer groupId, Model model) throws Exception {

        Integer id = AuthStateInformer.getId(principal);
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);
        GroupDetailsResponse response = groupsService.getGroup(groupId);
        String role = AuthStateInformer.getRole(principal);
        System.out.println();

        if (response.getGroupId() == 2) {
            if (role.equals("teacher") || role.equals("admin")) {
                return "redirect:editGroup?id=" + groupId;
            }
        } else if (role.equals("teacher") || role.equals("admin") || response.getMembersList().contains(userReply)) {
            return "redirect:editGroup?id=" + groupId;
        }
        return "redirect:group?id=" + groupId;
    }

}
