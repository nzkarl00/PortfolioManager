package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedGroupsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GroupController {
    @Autowired
    private GroupsClientService groupsService;

    @Autowired
    private NavController navController;

    @Autowired
    private AccountClientService accountClientService;

    @GetMapping("/groups")
    public String getGroups(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) throws Exception {
        Integer id = AuthStateInformer.getId(principal);

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);

        navController.updateModelForNav(principal, model, userReply, id);

        List<Group> groups = new ArrayList<>();

        PaginatedGroupsResponse response = groupsService.getGroups(10, 0, true);
        for (GroupDetailsResponse group: response.getGroupsList()) {
            groups.add(new Group(group));
        }

        model.addAttribute("groups", groups);

        String role = AuthStateInformer.getRole(principal);

        if (role.equals("teacher") || role.equals("admin")) {
            model.addAttribute("display", "");
        } else {
            model.addAttribute("display", "display:none;");
        }

        return "groups";
    }
}
