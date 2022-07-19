package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyGroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    String errorShow = "display:none;";
    String successShow = "display:none;";
    String successCode = "successCode";

    /**
     * open up the editing with the data from the request
     * @param principal the authstate
     * @param id the id of the group to edit
     * @param model the model to add info to display
     * @return String to direct the user
     */
    @GetMapping("/editGroup")
    public String getGroups(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam Integer id,
            Model model) {

        Integer userid = AuthStateInformer.getId(principal);
        UserResponse userReply;
        userReply = accountClientService.getUserById(userid);
        GroupDetailsResponse groupresponse = groupsService.getGroup(id);
        String role = AuthStateInformer.getRole(principal);

        if ((groupresponse.getGroupId() == 2) || (groupresponse.getGroupId() == 1)) {
            return "redirect:groups";
        } else if (role.equals("teacher") || role.equals("admin") || groupresponse.getMembersList().contains(userReply)) {
            // Nothing
        } else {
            return "redirect:groups";
        }

        model.addAttribute("errorShow", errorShow);
        model.addAttribute("successShow", successShow);
        model.addAttribute("successCode", successCode);
        model.addAttribute("groupId", id);

        Integer userId = AuthStateInformer.getId(principal);


        navController.updateModelForNav(principal, model, userReply, userId);

        model.addAttribute("id", id);

        GroupDetailsResponse response = groupsService.getGroup(id);

        model.addAttribute("longName", response.getLongName());
        model.addAttribute("shortName", response.getShortName());

        return "editGroup";
    }

    @GetMapping("/back")
    public String returnBack(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam Integer id,
            Model model) {

        return "redirect:group?id=" + id;

    }

    /**
     *
     * @param principal the authstate
     * @param id the id of the group to edit
     * @param longName the long name to change to
     * @param shortName the short name to change to
     * @param model the model to add info to display
     * @return String to direct the user
     */
    @PostMapping("/modifyGroup")
    public String editGroup(
        @AuthenticationPrincipal AuthState principal,
        @RequestParam Integer id,
        @RequestParam String longName,
        @RequestParam String shortName,
        Model model ) {

        Integer userid = AuthStateInformer.getId(principal);
        UserResponse userReply;
        userReply = accountClientService.getUserById(userid);
        GroupDetailsResponse groupresponse = groupsService.getGroup(id);
        String role = AuthStateInformer.getRole(principal);

        if ((groupresponse.getGroupId() == 2) || (groupresponse.getGroupId() == 1)) {
                return "redirect:groups";
        } else if (role.equals("teacher") || role.equals("admin") || groupresponse.getMembersList().contains(userReply)) {
            // Nothing
        } else {
            return "redirect:groups";
        }

        ModifyGroupDetailsResponse response = groupsService.modifyGroup(id, longName, shortName);

        successCode = response.getMessage();
        if (response.getIsSuccess()) {
            errorShow = "display:none;";
            successShow = "";
        } else {
            errorShow = "";
            successShow = "display:none;";
        }

        return "redirect:editGroup?id=" + id;
    }
}
