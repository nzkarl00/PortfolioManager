package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GroupClientService;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedGroupsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

@Controller
public class GroupController {
    @Autowired
    private GroupsClientService groupsService;

    @Autowired
    private GroupClientService groupClientService;

    @Autowired
    private NavController navController;

    @Autowired
    private AccountClientService accountClientService;

    private int MAX_NUMBER_OF_GROUPS = 10;
    private List<Integer> clipboard = new ArrayList<>();
    private HashMap<Integer, List<Integer>> clipboard2 = new HashMap<>();

    /**
     * gets a page of groups with all the users in the groups shown in a table
     * @param principal the authstate
     * @param page the page number to get of the groups
     * @param model the thing to display details
     * @return String to direct correct html page
     */
    @GetMapping("/groups")
    public String getGroups(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam("page") Optional<Integer> page,
            Model model
    ) {
        Integer id = AuthStateInformer.getId(principal);

        // Pagination setup
        Integer currentPage = page.orElse(Integer.valueOf(0));
        if (currentPage < 0) currentPage = 0;

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);

        navController.updateModelForNav(principal, model, userReply, id);

        List<Group> groups = new ArrayList<>();

        // get the groups, if there are no groups and you aren't on the first page, go back to the previous page
        PaginatedGroupsResponse response = groupsService.getGroups(MAX_NUMBER_OF_GROUPS, currentPage, true);
        if (currentPage != 0 && response.getGroupsList().isEmpty()) {
            response = groupsService.getGroups(MAX_NUMBER_OF_GROUPS, currentPage-1, true);
            currentPage--;

        }

        for (GroupDetailsResponse group: response.getGroupsList()) {
            groups.add(new Group(group));
        }

        model.addAttribute("groups", groups);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("clipboard", clipboard);
        model.addAttribute("clipboard2", clipboard2);

        String role = AuthStateInformer.getRole(principal);

        // if you are a teacher or an admin you can add a new group
        if (role.equals("teacher") || role.equals("admin")) {
            model.addAttribute("display", "");
        } else {
            model.addAttribute("display", "display:none;");
        }

        return "groups";
    }

    @PostMapping("/ctrlv")
    public String paste(
        @AuthenticationPrincipal AuthState principal,
        @RequestBody() List<Integer> ids,
        @RequestParam("groupId") Integer groupId,
        Model model
    ) {
        groupClientService.addUserToGroup(groupId, (ArrayList<Integer>) ids);
        clipboard = ids;
        return "redirect:groups";
    }

    @PostMapping("/ctrlx")
    public String cut(
            @AuthenticationPrincipal AuthState principal,
            @RequestBody() HashMap<Integer, List<Integer>> ids,
            Model model
    ) {
        for (Map.Entry<Integer, List<Integer>> entry : ids.entrySet()) {
            if (entry.getKey() >= 0) {
                groupClientService.removeUserFromGroup(entry.getKey(), (ArrayList<Integer>) entry.getValue());
            }
        }
        clipboard = ids.get(-1);
        clipboard2 = ids;
        return "redirect:groups";
    }
}
