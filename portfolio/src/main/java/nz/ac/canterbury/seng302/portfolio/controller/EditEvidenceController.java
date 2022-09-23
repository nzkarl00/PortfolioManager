package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.User;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedGroupsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.gitlab4j.api.GitLabApiException;
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
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Responsible for the edit evidence page
 */
@Controller
public class EditEvidenceController {

    private final String evidenceRedirect = "redirect:evidence";
    Logger logger = LoggerFactory.getLogger(EditEvidenceController.class);
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
    @Autowired
    private GroupsClientService groupsService;
    @Autowired
    private LinkedCommitRepository linkedCommitRepository;

    /**
     * Adds list of all users to the given model
     */
    public static void userGroups(Model model,
                                  AccountClientService accountClientService) {
        PaginatedUsersResponse response =
                accountClientService.getPaginatedUsers(-1, 0, "", 0);

        List<String> users = new ArrayList<>();
        for (UserResponse user : response.getUsersList()) {
            User temp = new User(user);
            users.add(temp.id + ":" + temp.username);
        }
        model.addAttribute("allUsers", users);
    }

    /**
     * Validates arguments passed to the edit evidence route.
     * Currently, only validates skill related components.
     */
    protected static void validateEditEvidenceParameters(
            Integer _id,
            String _title,
            String _date,
            Integer _projectId,
            String _categories,
            String skillsDelete,
            String skillsEdit,
            String skillsNew,
            String _links,
            String _description
    ) {
        // Delete may be empty, or 1 or more numbers separated by spaces
        if ((!Pattern.matches("^(\\d+)(?>( \\d+))*$", skillsDelete)) && !(Objects.equals(skillsDelete, ""))) {
            throw new IllegalArgumentException(
                    "Skills to delete must be a sentence of numbers");
        }
        // Skills edit must be of form 9:some-skill_name 10:skill where 9 and 10 are existing IDs.
        if ((!Pattern.matches("^(\\d+:[\\w-_]+(?>( \\d+:[\\w-_]+))*)$", skillsEdit)) && !(Objects.equals(skillsEdit, ""))) {
            throw new IllegalArgumentException(
                    "Skills to edit is of incorrect form");
        }
        // Capture the words/strings in the skillsEdit, and check agains the more advanced validator
        // To ensure they are valid
        String[] editTitles = skillsEdit.split("\\s?\\d+:");
        for (String skillTitle : editTitles) {
            if (!skillTitle.isEmpty() && !SkillTag.isValidTitle(skillTitle)) {
                throw new IllegalArgumentException(
                        "skillsEdit, skill title is invalid: " + skillTitle);
            }
        }

        // SkillsNew must also be validated.
        String[] newTitles = skillsNew.split(" ");
        for (String skillTitle : newTitles) {
            if (!skillTitle.isEmpty() && !SkillTag.isValidTitle(skillTitle)) {
                throw new IllegalArgumentException(
                        "skillsNew, skill title is invalid: " + skillTitle);
            }
        }
    }

    /**
     * Extracts the add, remove and edit operations to be performed into three respective lists.
     */
    protected static EvidenceService.ParsedEditSkills parseSkillParameters(
            String skillsNew,
            String skillsDelete,
            String skillsEdit
    ) {
        // Extract new skills
        List<String> newSkillsExtracted;
        if (!skillsNew.isEmpty()) {
            newSkillsExtracted = Arrays.stream(skillsNew.split("\\s")).toList();
        } else {
            newSkillsExtracted = List.of();
        }

        // Extract delete IDs
        List<Integer> deleteIDsExtracted;
        if (!skillsDelete.isEmpty()) {
            Pattern deletePattern = Pattern.compile("\\d+");
            deleteIDsExtracted = deletePattern.matcher(skillsDelete)
                    .results()
                    .map((MatchResult res) -> Integer.parseInt(res.group()))
                    .toList();
        } else {
            deleteIDsExtracted = List.of();
        }

        // Extract skills to edit
        HashMap<Integer, String> skillsToModify = new HashMap<>();
        if (!skillsEdit.isEmpty()) {
            for (String entry : skillsEdit.split("\\s")) {
                String[] split = entry.split(":");
                if (split.length != 2) {
                    throw new IllegalArgumentException(
                            "Skills to modify is invalid");
                }
                Integer existingID = Integer.parseInt(split[0]);
                String newTitle = split[1];
                skillsToModify.put(existingID, newTitle);
            }
        }

        return new EvidenceService.ParsedEditSkills(newSkillsExtracted,
                deleteIDsExtracted, skillsToModify);
    }

    /**
     * Directs the user to the edit page for the evidence they specify with the ID
     *
     * @param principal  auth state for the currently authenticated user
     * @param evidenceId The id of the evidence to edit
     * @param model      The model to be used by the application for web integration
     * @return the html template to give to the user
     */
    @GetMapping("/edit-evidence")
    public String editEvidence(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "id") Optional<Integer> evidenceId,
            Model model) {
        logger.info(
                String.format(
                        "Received get request for edit-evidence page, evidence-id=<%d>",
                        evidenceId.orElse(-1)
                ));
        int id = AuthStateInformer.getId(principal);
        int evidenceIdActualised;
        UserResponse userReply = accountClientService.getUserById(id);
        navController.updateModelForNav(principal, model, userReply, id);

        if (evidenceId.isPresent()) {
            evidenceIdActualised = evidenceId.get();
        } else {
            return evidenceRedirect;
        }

        Evidence evidence = evidenceRepository.findById(evidenceIdActualised);

        //Check is evidence is present or the user is the parent evidence user
        if (evidence == null || evidence.getParentUserId() != id) {
            return evidenceRedirect;
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


        userGroups(model, accountClientService);

        List<EvidenceTag> tags = evidence.getEvidenceTags();

        // Get the SkillTags as a list.
        List<String> skills = tags.stream()
                // Never display the no_skills tag
                .filter((EvidenceTag tag) -> !tag.getParentSkillTag().getTitle()
                        .equals("No_skills"))
                .map((EvidenceTag tag) -> tag.getParentSkillTag().getId() +
                        ":" + tag.getParentSkillTag().getTitle())
                .collect(Collectors.toList());

        List<String> skillsTitleList = tags.stream()
                .filter((EvidenceTag tag) -> !tag.getParentSkillTag().getTitle()
                        .equals("No_skills"))
                .map((EvidenceTag tag) -> tag.getParentSkillTag().getTitle())
                .collect(Collectors.toList());

        Set<String> skillTagList = evidenceService.getAllUniqueSkills();
        logger.debug(skills.toString());
        model.addAttribute("existingCommits", evidence.getLinkedCommit());
        PaginatedGroupsResponse groupList = groupsService.getAllGroupsForUser(evidence.getParentUserId());
        model.addAttribute("groupList", groupList.getGroupsList());
        skillTagList.remove("No_skills");

        model.addAttribute("allSkills", skillTagList);
        model.addAttribute("skills", skills);
        model.addAttribute("skillsTitleList", skillsTitleList);
        model.addAttribute("links", linkUrls);
        model.addAttribute("evidence", evidence);
        model.addAttribute("project", evidence.getAssociatedProject());
        model.addAttribute("title", "Edit Evidence: " + evidence.getTitle());

        return "editEvidence";
    }

    /**
     * The route to post an evidence edit through
     *
     * @param principal    auth state for the currently authenticated user
     * @param title        new/existing title
     * @param date         new/existing date
     * @param projectId    old project id
     * @param categories   string list of categories
     * @param skillsDelete string list of skills to delete
     * @param skillsEdit   string list of skills to edit
     * @param skillsNew    string list of skills to add
     * @param links        string list of links
     * @param description  new/existing description
     * @param id           the id for the piece of evidence to edit
     * @return redirect to the evidence page once the edit is complete
     * @exception MalformedURLException if an invalid link is given
     * @exception GitLabApiException if there is an issue fetching commit data from gitlab API
     */
    @Transactional
    @PostMapping("/edit-evidence")
    public String editEvidence(
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
            @RequestParam(value = "commitsInput") String newCommits,
            @RequestParam(value = "commitsDelete") String deletedCommits) throws MalformedURLException, GitLabApiException {
        logger.info(
                String.format(
                        "Received POST request to edit-evidence, evidence-id=<%d>",
                        id
                ));

        // Validate the parameters, currently only validates skill parameters.
        validateEditEvidenceParameters(
                id,
                title,
                date,
                projectId,
                categories,
                skillsDelete,
                skillsEdit,
                skillsNew,
                links,
                description
        );

        // Parse skill tags
        EvidenceService.ParsedEditSkills parsedSkills = parseSkillParameters(
                skillsNew,
                skillsDelete,
                skillsEdit
        );

        Evidence evidence = evidenceRepository.findById((int) id);
        if (evidence == null || AuthStateInformer.getId(principal) !=
                evidence.getParentUserId()) {
            return evidenceRedirect;
        }
        evidence.setCategories(Evidence.categoryStringToInt(categories));

        // Validating the mandatory fields from U7
        Evidence.validateProperties(evidence.getAssociatedProject(), title,
                description, LocalDate.parse(date));
        evidence.setDate(LocalDate.parse(date));
        evidence.setDescription(description);
        evidence.setTitle(title);

        evidence.setCategories(Evidence.categoryStringToInt(categories));

        // delete all past users from this user's evidence, then add all modified users for this user's evidence
        evidenceUserRepository.deleteAllByEvidence(evidence);
        evidenceService.addUsersToExistingEvidence(
                EvidenceService.extractListFromHTMLStringWithTilda(users),
                evidence);

        // delete all past weblinks from this user's evidence, then add all modified weblinks for this user's evidence
        webLinkRepository.deleteAllByEvidence(evidence);
        evidenceService.addLinksToEvidence(
                evidenceService.extractListFromHTMLStringWithSpace(links),
                evidence);

        List<LinkedCommit> newLinkedCommits = evidenceService.constructCommits(EvidenceService.extractListFromHTMLStringWithTilda(newCommits).stream().filter(hashAndGroupString -> hashAndGroupString.contains("+")).collect(Collectors.toList()), evidence);
        for (String deletedCommit: EvidenceService.extractListFromHTMLStringWithTilda(deletedCommits)) {
            linkedCommitRepository.deleteByParentEvidenceAndHash(evidence, deletedCommit);
        }
        linkedCommitRepository.saveAll(newLinkedCommits);
        evidenceRepository.save(evidence);

        // Now deal with skill tags
        // Delete first, then edit, then add new.
        evidenceService.handleSkillTagEditsForEvidence(parsedSkills, evidence);

        return evidenceRedirect;
    }
}
