package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.UserTemplate;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.Group;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AddGroupController {
    @Autowired
    private GroupsClientService groupsService;

    @Autowired
    private NavController navController;

    @Autowired
    private AccountClientService accountClientService;

    Logger logger = LoggerFactory.getLogger(AddGroupController.class);

    /**
     * the get mapping of the create group page
     * @param principal the authstate
     * @param model the thing to add details to
     * @return String of what html page to go to
     */
    @GetMapping("/addGroup")
    public String getGroups(
            @AuthenticationPrincipal AuthState principal,
            Model model) {
        String role = AuthStateInformer.getRole(principal);

        if (!(role.equals("teacher") || role.equals("admin"))) {
            return "redirect:groups";
        }
        model.addAttribute("groupForm", new Group());

        Integer userId = AuthStateInformer.getId(principal);

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(userId);

        navController.updateModelForNav(principal, model, userReply, userId);

        return "addGroup";
    }

    /**
     * Attempts to register the user if all information is valid
     * @param principal the authstate
     * @param shortName the short name of the new group
     * @param model The model to be used by the application for web integration
     * @return redirects to the signup page
     */
    @PostMapping("/createGroup")
    public String createGroup(
        @AuthenticationPrincipal AuthState principal,
        @RequestParam(value="longName") String longName,
        @RequestParam(value="shortName") String shortName,
        @ModelAttribute("groupForm") Group group,
        BindingResult result,
        Model model
    )
    {
        String role = AuthStateInformer.getRole(principal);
        // if you are a teacher or an admin you can add a new group
        if (role.equals("teacher") || role.equals("admin")) {
            model.addAttribute("display", "");
            CreateGroupResponse createReply;
            createReply = groupsService.create(shortName, longName);
            if (createReply.getIsSuccess()) {
                return "redirect:groups";
            } else {
                result.addError(new ObjectError("globalError", createReply.getMessage()));
                Integer userId = AuthStateInformer.getId(principal);
                UserResponse userReply;
                userReply = accountClientService.getUserById(userId);
                navController.updateModelForNav(principal, model, userReply, userId);
                return "addGroup";
            }
        } else {
            return "redirect:groups";
        }
    }
}
