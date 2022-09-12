package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.User;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceTag;
import nz.ac.canterbury.seng302.portfolio.model.evidence.WebLink;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Responsible for the edit evidence page
 */
@Controller
public class EditEvidenceController {

    @Autowired
    private NavController navController;
    @Autowired
    private AccountClientService accountClientService;
    @Autowired
    private EvidenceService evidenceService;
    @Autowired
    private EvidenceRepository evidenceRepository;
    @Autowired
    private EvidenceUserRepository evidenceUserRepository;
    @Autowired
    private WebLinkRepository webLinkRepository;

    Logger logger = LoggerFactory.getLogger(EditEvidenceController.class);

    /**
     * Directs the user to the edit page for the evidence they specify with the ID
     * @param principal auth state for the currently authenticated user
     * @param evidenceId The id of the evidence to edit
     * @param model The model to be used by the application for web integration
     * @return the html template to give to the user
     */
    @GetMapping("/edit-evidence")
    public String editEvidence(
        @AuthenticationPrincipal AuthState principal,
        @RequestParam(value = "id") Optional<Integer> evidenceId,
        Model model) {
        logger.info("[GET EDIT EVIDENCE] try to get the edit page for evidence of id " + evidenceId.orElse(-1));
        int id = AuthStateInformer.getId(principal);
        int evidenceIdActualised;
        UserResponse userReply = accountClientService.getUserById(id);
        navController.updateModelForNav(principal, model, userReply, id);

        if (evidenceId.isPresent()) {
            evidenceIdActualised = evidenceId.get();
        } else {
            return "redirect:evidence";
        }

        Evidence evidence = evidenceRepository.findById(evidenceIdActualised);
        if (evidence == null) {
            return "redirect:evidence";
        }
        // get the links and pass the urls to the frontend
        List<WebLink> links = evidence.getLinks();
        List<String> linkUrls = new ArrayList<>();
        for (WebLink link : links) {
            linkUrls.add(link.getUrl());
        }


        // Creating a mapping of ID: Usernames, for the users who are contributing to this evidence
        List<String> evidenceUsers = new ArrayList<>();
        for (EvidenceUser user : evidence.getEvidenceUsersId()) {
            evidenceUsers.add(user.getUserid() + ":" + user.getUsername());
        }
        model.addAttribute("users", evidenceUsers);


        PaginatedUsersResponse response = accountClientService.getPaginatedUsers(-1, 0, "", 0);

        List<String> users = new ArrayList<>();
        for (UserResponse user: response.getUsersList()) {
            User temp = new User(user);
            users.add(temp.id + ":" + temp.username);
        }
        model.addAttribute("allUsers", users);

        List<EvidenceTag> tags = evidence.getEvidenceTags();
        List<String> skills = new ArrayList<>();
        for (EvidenceTag tag: tags) {
            skills.add(tag.getParentSkillTag().getId() + ":" + tag.getParentSkillTag().getTitle());
        }

        Set<String> skillTagList = evidenceService.getAllUniqueSkills();
        logger.debug(skills.toString());

        model.addAttribute("allSkills", skillTagList);
        model.addAttribute("skills", skills);
        model.addAttribute("links", linkUrls);
        model.addAttribute("evidence", evidence);
        model.addAttribute("title", "Edit Evidence: " + evidence.getTitle());

        return "editEvidence";
    }

    /**
     * The route to post an evidence edit through
     * @param principal auth state for the currently authenticated user
     * @param title new/existing title
     * @param date new/existing date
     * @param projectId old project id
     * @param categories string list of categories
     * @param skillsDelete string list of skills to delete
     * @param skillsEdit string list of skills to edit
     * @param skillsNew string list of skills to add
     * @param links string list of links
     * @param description new/existing description
     * @param id the id for the piece of evidence to edit
     * @param model The model to be used by the application for web integration
     * @return redirect to the evidence page once the edit is complete
     */
    @Transactional
    @PostMapping("/edit-evidence")
    public String addEvidence(
        @AuthenticationPrincipal AuthState principal,
        @RequestParam(value = "titleInput") String title,
        @RequestParam(value = "dateInput") String date,
        @RequestParam(value = "projectId") Integer projectId,
        @RequestParam(value = "categoryInput") String categories,
        @RequestParam(value = "skillDeleteInput") String skillsDelete,
        @RequestParam(value = "skillEditInput") String skillsEdit,
        @RequestParam(value = "skillNewInput") String skillsNew,
        @RequestParam(value = "linksInput") String links,
        @RequestParam(value = "descriptionInput") String description,
        @RequestParam(value = "evidenceId") Integer id,
        @RequestParam(value = "userInput") String users,
        Model model) throws MalformedURLException {

        Evidence evidence = evidenceRepository.findById((int) id);

        // Validating the mandatory fields from U7
        Evidence.validateProperties(evidence.getAssociatedProject(), title, description, LocalDate.parse(date));
        evidence.setDate(LocalDate.parse(date));
        evidence.setDescription(description);
        evidence.setTitle(title);

        evidence.setCategories(Evidence.categoryStringToInt(categories));

        // delete all past users from this user's evidence, then add all modified users for this user's evidence
        evidenceUserRepository.deleteAllByEvidence(evidence);
        evidenceService.addUsersToExistingEvidence(evidenceService.extractListFromHTMLStringWithTilda(users), evidence);

        // delete all past weblinks from this user's evidence, then add all modified weblinks for this user's evidence
        webLinkRepository.deleteAllByEvidence(evidence);
        evidenceService.addLinksToEvidence(evidenceService.extractListFromHTMLStringWithSpace(links), evidence);

        evidenceRepository.save(evidence);

        return "redirect:evidence";
    }
}
