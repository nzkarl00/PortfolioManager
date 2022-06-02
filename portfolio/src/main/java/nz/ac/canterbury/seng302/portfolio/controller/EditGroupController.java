package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.GetGroupDetailsResponse;
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
public class EditGroupController {
    @Autowired
    private GroupsClientService groupsService;

    @Autowired
    private NavController navController;

    @Autowired
    private AccountClientService accountClientService;

    @GetMapping("/editGroup")
    public String getGroups(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam Integer id,
            Model model
    ) throws Exception {
        Integer userId = AuthStateInformer.getId(principal);

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(userId);

        navController.updateModelForNav(principal, model, userReply, userId);

        model.addAttribute("id", id);

        return "editGroup";
    }
}
