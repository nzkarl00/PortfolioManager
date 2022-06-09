package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AddGroupController {
    @Autowired
    private GroupsClientService groupsService;

    @Autowired
    private NavController navController;

    @Autowired
    private AccountClientService accountClientService;

    String errorShow = "display:none;";
    String successShow = "display:none;";
    String successCode = "successCode";

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

        model.addAttribute("errorShow", errorShow);
        model.addAttribute("successShow", successShow);
        model.addAttribute("successCode", successCode);

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
        Model model
    )
    {
        String role = AuthStateInformer.getRole(principal);
        // if you are a teacher or an admin you can add a new group
        if (role.equals("teacher") || role.equals("admin")) {
            model.addAttribute("display", "");
            CreateGroupResponse createReply;
            createReply = groupsService.create(shortName, longName);
            successCode = createReply.getMessage();
            if (createReply.getIsSuccess()) {
                errorShow = "display:none;";
                successShow = "";
            } else {
                errorShow = "";
                successShow = "display:none;";
            }

            return "redirect:addGroup";
        } else {
            return "redirect:groups";
        }
    }
}
