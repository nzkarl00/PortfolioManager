package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.userGroups.Group;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepo;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepoRepository;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupTemplate;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GitlabClient;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyGroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class EditGroupController {
    @Autowired
    private GroupsClientService groupsService;

    @Autowired
    private NavController navController;

    @Autowired
    private AccountClientService accountClientService;

    @Autowired
    private GroupRepoRepository groupRepoRepository;

    @Autowired
    private GitlabClient gitlabClient;

    String errorShow = "display:none;";
    String errorCode = "";
    String successShow = "display:none;";
    String successCode = "successCode";

    @Value("${portfolio.gitlab-instance-url}")
    private String gitlabInstanceURL;

    Logger logger = LoggerFactory.getLogger(AddGroupController.class);

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
        Group group = new Group(groupresponse);
        String role = AuthStateInformer.getRole(principal);

        // Do not allow editing default group
        if (group.isDefaultGroup()) {
            return "redirect:groups";
        } else if (role.equals("teacher") || role.equals("admin") || groupresponse.getMembersList().contains(userReply)) {
            // Nothing
        } else {
            return "redirect:groups";
        }
        Integer userId = AuthStateInformer.getId(principal);
        navController.updateModelForNav(principal, model, userReply, userId);
        model.addAttribute("id", id);

        GroupDetailsResponse response = groupsService.getGroup(id);

        GroupTemplate template = new GroupTemplate();
        template.setLongName(response.getLongName());
        template.setShortName(response.getShortName());
        model.addAttribute("groupId", id);
        // Try fetch the repo data from Repo store.
        Optional<GroupRepo> groupRepo = groupRepoRepository.findByParentGroupId(group.getId());
        if (groupRepo.isPresent()) {
            template.setRepoOwner(groupRepo.get().getOwner());
            template.setRepoName(groupRepo.get().getName());
            template.setApiKey(groupRepo.get().getApiKey());
            template.setRepoAlias(groupRepo.get().getAlias());
        }
        model.addAttribute("groupForm", template);
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
    @PostMapping("/editGroup")
    public String editGroup(
        @AuthenticationPrincipal AuthState principal,
        @RequestParam Integer id,
        @RequestParam String longName,
        @RequestParam String shortName,
        @RequestParam String repoOwner,
        @RequestParam String repoName,
        @RequestParam String repoAlias,
        @RequestParam String apiKey,
        @ModelAttribute("groupForm") GroupTemplate g,
        BindingResult result,
        Model model ) {

        Integer userid = AuthStateInformer.getId(principal);
        UserResponse userReply;
        userReply = accountClientService.getUserById(userid);
        GroupDetailsResponse groupresponse = groupsService.getGroup(id);
        Group group = new Group(groupresponse);
        String role = AuthStateInformer.getRole(principal);

        if (group.isDefaultGroup()) {
                return "redirect:groups";
        } else if (role.equals("teacher") || role.equals("admin") || groupresponse.getMembersList().contains(userReply)) {
            // Nothing
        } else {
            return "redirect:groups";
        }

        ModifyGroupDetailsResponse response = groupsService.modifyGroup(id, longName, shortName);
        successCode = response.getMessage();
        if (!response.getIsSuccess()) {
            result.addError(new ObjectError("globalError", successCode));
        }

        // Now deal with group repo stuff
        // If any of the below fields are set
        if (!(repoOwner.length() == 0 && repoName.length() == 0 && apiKey.length() == 0 && repoAlias.length() == 0)) {

            // All must be set
            if (repoOwner.length() == 0 || repoName.length() == 0 || apiKey.length() == 0 || repoAlias.length() == 0) {
                result.addError(new ObjectError("globalError", "If any of Repo Owner, Repo Name, Repo Alias or API Key is set, all must be set."));
            }
            // Try and fetch an existing Repo record
            GroupRepo groupRepo = null;
            Optional<GroupRepo> existingGroupRepo = groupRepoRepository.findByParentGroupId(id);
            if (!existingGroupRepo.isPresent()) {
                groupRepo = new GroupRepo(id, repoOwner, repoName, apiKey);
            } else {
                groupRepo = existingGroupRepo.get();
            }

            groupRepo.setOwner(repoOwner);
            groupRepo.setName(repoName);
            groupRepo.setApiKey(apiKey);
            groupRepo.setAlias(repoAlias);
            // Check with Gitlab client that the repo exists.
            try {
                gitlabClient.getProject(apiKey, repoOwner, repoName);
            } catch (Exception e) {
                logger.warn(String.format(
                        "Provided API integration did not work: repoOwner=%s repoName=%s apiKey=%s",
                        repoOwner,
                        repoName,
                        apiKey
                ), e);
                // If the connection fails, save it if there was no existing repo record
                // However if there was no existing record, then don't save it.
                if (existingGroupRepo.isPresent()) {
                    result.addError(new ObjectError("globalError", "Group details not saved as API connection details are incorrect."));
                } else {
                    errorShow = "";
                    successShow = "display:none;";
                    errorCode = "Group details saved, however Gitlab API Connection is broken.";
                }
            }
            if (result.hasGlobalErrors()) {
                navController.updateModelForNav(principal, model, userReply, userid);
                model.addAttribute("id", id);
                model.addAttribute("groupId", id);
                return "editGroup";
            }
            try {
                groupRepoRepository.save(groupRepo);
            } catch (Exception e) {
                errorShow = "";
                errorCode = "Could not save group repository details.";
                successShow = "display:none;";
            }
        }
        return "redirect:group?id=" + id;
    }
}
