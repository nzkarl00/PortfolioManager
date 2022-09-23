package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.userGroups.Group;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.User;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the display project details page
 */
@Controller
public class GroupDetailsController {

    @Autowired
    private AccountClientService accountClientService;
    @Autowired
    private NavController navController;
    @Autowired
    private GroupsClientService groupsService;

    Logger logger = LoggerFactory.getLogger(GroupDetailsController.class);

    /**
     * Returns the html page based on the user's role
     *
     * @param principal the auth token
     * @param model     The model to be used by the application for web integration
     * @return The html page to direct to
     */
    @GetMapping("/group")
    public String details(@AuthenticationPrincipal AuthState principal, @RequestParam(value = "id") Integer groupId, Model model) {
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
    public String editGroup(@AuthenticationPrincipal AuthState principal, @RequestParam(value = "id") Integer groupId, Model model) {

        Integer id = AuthStateInformer.getId(principal);
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);
        GroupDetailsResponse response = groupsService.getGroup(groupId);
        String role = AuthStateInformer.getRole(principal);

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
