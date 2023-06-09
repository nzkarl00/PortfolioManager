package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.CustomExceptions;
import nz.ac.canterbury.seng302.portfolio.model.AuthenticatedUser;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepo;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepoRepository;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.User;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedGroupsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.gitlab4j.api.GitLabApiException;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
import java.util.function.Consumer;

/**
 * responsible for the main/landing page of the project(s)
 */
@Controller
public class EvidenceListController {

    @Autowired
    private EvidenceRepository evidenceRepository;
    @Autowired
    private EvidenceTagRepository evidenceTagRepository;
    @Autowired
    private WebLinkRepository webLinkRepository;
    @Autowired
    private SkillTagRepository skillRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private AccountClientService accountClientService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private NavController navController;
    @Autowired
    private EvidenceService evidenceService;
    @Autowired
    private GroupRepoRepository groupRepoRepository;
    @Autowired
    private GroupsClientService groupsService;
    @Autowired
    private GitlabClient gitlabClient;
    @Autowired
    private LinkedCommitRepository linkedCommitRepository;
    @Autowired
    private HighFiveRepository highFiveRepository;

    @Value("${portfolio.base-url}")
    private String baseUrl;


    private String errorMessage = "";

    Logger logger = LoggerFactory.getLogger(EvidenceListController.class);

    /**
     * Directs the user to the landing project page
     * @param principal auth state for the currently authenticated user
     * @param model The model to be used by the application for web integration
     * @return redirects to the landing page
     * @throws Exception which is raised by the repositories having a potential failure when reading objects from the DB
     */
    @Transactional
    @GetMapping("/evidence")
    public String evidenceListController(@AuthenticationPrincipal AuthState principal,
                                         @RequestParam(required = false, value = "ui") Integer userId,
                                         @RequestParam(required = false, value = "pi") Integer projectId,
                                         @RequestParam(required = false, value = "si") String skillName,
                                         @RequestParam(required = false, value = "ci") String categoryName,
                                         Model model) throws Exception {
        logger.info("[EVIDENCE] Request to view list of evidence");

        setPageTitle(model, "List Of Evidence");
        int id = AuthStateInformer.getId(principal);
        if (userId == null) {
            userId = id;
        }
        setTitle(model, userId, projectId, categoryName, skillName);

        // Use empty evidence list, as we re-fetch evidence client side anyway.
        List<Evidence> evidenceList = List.of();

        logger.debug("[EVIDENCE] Getting all projects");
        List<Project> allProjects = projectService.getAllProjects();
        model.addAttribute("projectList", allProjects);
        model.addAttribute("filterSkills", evidenceService.getFilterSkills(evidenceList));
        model.addAttribute("userSkills", evidenceService.getUserSkills(AuthStateInformer.getId(principal)));
        model.addAttribute("userID", id);

        // Attributes For header
        UserResponse userReply;
        logger.debug("[EVIDENCE] Getting current user details");
        userReply = accountClientService.getUserById(id);
        navController.updateModelForNav(principal, model, userReply, id);
        // End of Attributes for header
        //Attributes for form
        boolean showForm = false;

        if (projectId != null) {
            showForm = true;
            logger.debug("[EVIDENCE] Getting specific project and attaching to model");
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("project", project);
        }
        model.addAttribute("showForm", showForm);
        model.addAttribute("errorMessage", errorMessage);
        this.errorMessage = "";

        logger.info("[EVIDENCE] Returning evidence list template");
        return "evidenceList";
    }

    @PostMapping("evidence-project")
    @Transactional
    public String sendProjectEvidence(@AuthenticationPrincipal AuthState principal,
                                      @RequestParam(required = false, value = "ui") Integer userId,
                                      @RequestParam(required = false, value = "pi") Integer projectId,
                                      @RequestParam(required = false, value = "si") String skillName,
                                      @RequestParam(required = false, value = "ci") String categoryName,
                                      Model model) throws CustomExceptions.ProjectItemNotFoundException {
        if (userId == null) {
            userId = AuthStateInformer.getId(principal);
        }
        List<Evidence> evidenceList = evidenceService.getEvidenceList(userId, projectId, categoryName, skillName);
        if (projectId != -1) {
            model.addAttribute("date", DateParser.dateToStringHtml(new Date()));
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("project", project);
        }

        model.addAttribute("evidenceList", evidenceList);
        List<Pair<Integer, List<String>>> skillTemp = new ArrayList<>();
        List<Pair<Integer, List<String>>> categoryTemp = new ArrayList<>();
        final HashMap<Integer, List<String>> evidenceSkillMap = new HashMap<>();
        final HashMap<Integer, List<String>> evidenceCategoryMap = new HashMap<>();
        List<Integer> evidenceHighFiveList = new ArrayList<>(); // list of evidenceIds that the currently logged in user has high fived
        Consumer<Evidence> putEvidenceIntoMap = (Evidence evidence) -> {
            skillTemp.add(new Pair<>(evidence.getId(), evidenceService.getSkillTagStringsByEvidenceId(evidence)));
            categoryTemp.add(new Pair<>(evidence.getId(), evidence.getCategoryStrings()));

            HighFive highFive = highFiveRepository.findByParentEvidenceAndParentUserId(evidence, AuthStateInformer.getId(principal));
            if (highFive != null) {
                evidenceHighFiveList.add(evidence.getId());
            }
        };

        evidenceList.forEach(putEvidenceIntoMap);

        skillTemp.forEach(pair -> evidenceSkillMap.put(pair.getValue0(), pair.getValue1()));
        categoryTemp.forEach(pair -> evidenceCategoryMap.put(pair.getValue0(), pair.getValue1()));

        navController.updateModelForNav(principal, model, accountClientService.getUserById(userId), userId);

        model.addAttribute("skillMap", evidenceSkillMap);
        model.addAttribute("categoryMap", evidenceCategoryMap);
        model.addAttribute("highFiveList", evidenceHighFiveList);
        model.addAttribute("user", new User(accountClientService.getUserById(AuthStateInformer.getId(principal))));
        model.addAttribute("username", AuthStateInformer.getUsername(principal));
        model.addAttribute("userId", AuthStateInformer.getId(principal));
        return "fragments/evidenceItems.html :: evidenceItems";
    }

    @GetMapping("evidence-form")
    public String sendEvidenceForm(@AuthenticationPrincipal final AuthState principal,
                                   @RequestParam(required = false, value = "pi") final Integer projectId,
                                   final Model model) throws CustomExceptions.ProjectItemNotFoundException {
        Runnable addProjectSprints = () -> {
            try {
                Project project = this.projectService.getProjectById(projectId);
                List<Sprint> sprintList = this.sprintService.getSprintByParentId(project.getId());
                model.addAttribute("sprintList", sprintList);
                model.addAttribute("project", project);
            } catch (CustomExceptions.ProjectItemNotFoundException e) {
                e.printStackTrace();
            }
        };

        model.addAttribute("date", LocalDate.now());
        model.addAttribute("baseUrl", baseUrl);

        Runnable addUsers = () -> {
            EditEvidenceController.userGroups(model, this.accountClientService);
        };

        Runnable addSkills = () -> {
            Set<String> skillTagListNoSkill = this.evidenceService.getAllUniqueSkills();
            skillTagListNoSkill.remove("No_skills");
            model.addAttribute("autoSkills", skillTagListNoSkill);
        };

        // for some reason these cannot be put into parallel, I think it is the userId
        // but efficiency has been increased regardless
        final int userId = AuthStateInformer.getId(principal);
        model.addAttribute("userId", userId);
        PaginatedGroupsResponse groupList = getGroupsFromUser(this.groupsService, userId);
        model.addAttribute("groupList", groupList.getGroupsList());
        navController.updateModelForNav(principal, model, this.accountClientService.getUserById(userId), userId);

        // Add all the tasks we need to do in parallel to a list
        List<Runnable> tasksToParallelise = new ArrayList<>();
        tasksToParallelise.add(addSkills);
        tasksToParallelise.add(addUsers);
        tasksToParallelise.add(addProjectSprints);

        // run said tasks in parallel
        tasksToParallelise.parallelStream().forEach(Runnable::run);

        return "fragments/evidenceForm.html :: evidenceForm";
    }

    private PaginatedGroupsResponse getGroupsFromUser(GroupsClientService service, int userId) {
        return service.getAllGroupsForUser(userId);
    }

    /**
     * Checks if the group repo is accessible.
     * @param groupId the id of the group to
     * @return true if the repo can be reached.
     */
    @GetMapping("/repo-check")
    public ResponseEntity<Boolean> repoCheck(@RequestParam(value = "group-id") Integer groupId) {
        GroupRepo groupRepo;
        Optional<GroupRepo> existingGroupRepo = groupRepoRepository.findByParentGroupId(groupId);
        if (groupId == -1 || existingGroupRepo.isEmpty()) {
            return ResponseEntity.ok(false);
        } else {
            groupRepo = existingGroupRepo.get();
            try {
                gitlabClient.getProject(groupRepo.getApiKey(), groupRepo.getOwner(), groupRepo.getName());
                return ResponseEntity.ok(true);
            } catch (Exception e) {
                return ResponseEntity.ok(false);
            }
        }
    }

    private void setPageTitle(Model model, String title) {
        model.addAttribute("title", title);
    }

    /**
     * Saves a new evidence if the user has permissions and the correct input is given
     *
     * @param principal   auth state for the currently authenticated user
     * @param title       evidence title
     * @param date        evidence date
     * @param projectId   the id of the project that the evidence is linked too
     * @param users       A list of usernames of other people (not the author) who worked on this evidence
     * @param categories  the category the evidence is associated with
     * @param skills      the skills the evidence is associated with
     * @param links       are an optional list of links associated with this new piece of evidence
     * @param description evidence description
     * @param model       The model to be used by the application for web integration
     * @return redirect to the evidence page
     * @throws CustomExceptions.ProjectItemNotFoundException if the project ID is not associated with any existing project
     */
    @PostMapping("/add-evidence")
    public String addEvidence(
        @AuthenticationPrincipal AuthState principal,
        @RequestParam(value = "titleInput") String title,
        @RequestParam(value = "dateInput") String date,
        @RequestParam(value = "projectId") Integer projectId,
        @RequestParam(value = "userID") Integer userId,
        @RequestParam(value = "categoryInput") String categories,
        @RequestParam(value = "skillInput") String skills,
        @RequestParam(value = "userInput") Optional <String> users,
        @RequestParam(value = "linksInput") Optional <String> links,
        @RequestParam(value = "commitsInput") Optional <String> commitsWithGroupIds,
        @RequestParam(value = "descriptionInput") String description,
        Model model
    ) throws CustomExceptions.ProjectItemNotFoundException {
        logger.info("[EVIDENCE] Attempting to add new evidence");
        Integer accountID = AuthStateInformer.getId(principal);
        String redirect = "redirect:evidence?pi=" + projectId.toString() + "&ui=" + accountID;
        if (!principal.getIsAuthenticated()) {
            logger.debug("[EVIDENCE] Redirecting, user not authenticated");
            return redirect;
        }
        model.addAttribute("authorId", accountID);
        AuthenticatedUser thisUser = new AuthenticatedUser(principal);
        model.addAttribute("authorUserName", thisUser.getUsername());
        Project parentProject = projectService.getProjectById(projectId);
        if (parentProject == null) {
            logger.debug("[EVIDENCE] Attempted to add evidence to a project that could not be found");
            // In future we can use a 404 here
            errorMessage = "Project does not exist";
            return "redirect:evidence?ui=" + accountID;
        }

        // Extract then validate links
        List<String> extractedLinks = evidenceService.extractListFromHTMLStringWithSpace(links.orElse(""));
        Optional<String> possibleError = evidenceService.validateLinks(extractedLinks);
        // prioritise mandatory fields first, then link errors
        this.errorMessage = possibleError.orElse(errorMessage);

        this.errorMessage = validateMandatoryFields(title, description, date, parentProject);

        // If error occurs, return early
        if (!errorMessage.equals("")) {
            model.addAttribute("errorMessage", errorMessage);
            return redirect;
        }

        int categoriesInt = Evidence.categoryStringToInt(categories);

        List<String> extractedUsers = EvidenceService.extractListFromHTMLStringWithTilda(users.orElse(""));
        List<String> extractedCommits = EvidenceService.extractListFromHTMLStringWithTilda(commitsWithGroupIds.orElse(""));
        List<Evidence> allUserEvidence = evidenceService.generateEvidenceForUsers(extractedUsers, parentProject, title, description, LocalDate.parse(date), categoriesInt);
        // If no error occurs with the mandatory fields then save the evidence to the repo and relevant skills or links
        logger.info("[EVIDENCE] Saving evidence to repo");
        for (Evidence evidence : allUserEvidence) {
            logger.info(String.format("[EVIDENCE] Saved evidence to repo, id=<%s>", evidence.getId()));
            errorMessage = "Evidence has been added";
            evidenceService.addSkillsToRepo(parentProject, evidence, skills);

            noSkillsCheck(evidence);

            if (!extractedLinks.isEmpty()) {
                logger.debug("[EVIDENCE] Saving web links");
                try {
                    webLinkRepository.saveAll(evidenceService.constructLinks(extractedLinks, evidence));
                } catch (MalformedURLException e) {
                    logger.error("[EVIDENCE] Somehow links were attempted for construction with malformed URL", e);
                    logger.error("[EVIDENCE] Links not saved");
                }
            }

            if (!extractedCommits.isEmpty()) {
                logger.debug("[EVIDENCE] Saving commits");
                try {
                    linkedCommitRepository.saveAll(evidenceService.constructCommits(extractedCommits, evidence));
                } catch (GitLabApiException e) {
                    logger.error("[EVIDENCE] GitLabAPI error finding selected commits");
                    logger.error("[EVIDENCE] Commits not saved");
                }
            }
        }
        return redirect;
    }

    @PostMapping("/delete-evidence")
    @Transactional
    public String deleteEvidence(@RequestParam(required = false, value = "projectId") String projectId,
                                 @RequestParam(value = "evidenceId") String evidenceId,
                                 @RequestParam(value = "userID") Integer userId,
                                 @AuthenticationPrincipal AuthState principal) {
        Evidence targetEvidence = evidenceRepository.findById(Integer.parseInt(evidenceId));
        int accountID = AuthStateInformer.getId(principal);
        String redirect = "redirect:evidence?pi=" + projectId.toString() + "&ui=" + accountID;
        if (targetEvidence == null) {
            logger.debug("[EVIDENCE] Redirecting, evidence id " + Integer.parseInt(evidenceId) + " does not exist");
            return redirect;
        }
        if (!principal.getIsAuthenticated() || accountID != targetEvidence.getParentUserId()) {
            logger.debug("[EVIDENCE] Redirecting, user does not have permissions to delete evidence " + Integer.parseInt(evidenceId));
            return redirect;
        }
        evidenceService.deleteEvidence(targetEvidence);
        return redirect;
    }

    private void noSkillsCheck(Evidence evidence) {
        // If there's no skills, add the no_skills
        List<EvidenceTag> evidenceTagList = evidenceTagRepository.findAllByParentEvidenceId(evidence.getId());
        if (evidenceTagList.isEmpty()) {
            SkillTag noSkillTag = skillRepository.findByTitle("No_skills");
            EvidenceTag noSkillEvidence = new EvidenceTag(noSkillTag, evidence);
            evidenceTagRepository.save(noSkillEvidence);
        }
    }

    /**
     * Checks for validation, for all the mandatory fields.
     *
     * @param title         the title field
     * @param description   the description field
     * @param date          the string representation of the date for the piece of evidence
     * @param parentProject the Project the piece of evidence 'belongs' to
     * @return A String error message if requirement not met, else return ""
     */
    private String validateMandatoryFields(String title, String description, String date, Project parentProject) {
        this.errorMessage = "";

        LocalDate evidenceDate = LocalDate.parse(date);
        LocalDate projectStartDate = parentProject.getLocalStartDate();
        LocalDate projectEndDate = parentProject.getLocalEndDate();

        // Check if the given evidence date is within the project date
        if (!(evidenceDate.isAfter(projectStartDate) && evidenceDate.isBefore(projectEndDate)) &&
                !(evidenceDate.isEqual(projectEndDate) || evidenceDate.isEqual(projectStartDate))) {
            // Give this error priority
            return "Dates must fall within project dates";
        }

        // https://stackoverflow.com/questions/14278170/how-to-check-whether-a-string-contains-at-least-one-alphabet-in-java
        // Checks if there is at least one character in title
        if (title.length() < 1 || containsNoLetter(title)) {
            errorMessage = "Title must more than one character and should not be only made from numbers and symbols";
        }

        // Checks if there is at least one character in description
        if (description.length() < 1 || containsNoLetter(description)) {
            errorMessage = "Description must more than one character and should not be only made from numbers and symbols";
        }

        // Check if the given evidence date is within the project date
        if (!(evidenceDate.isAfter(projectStartDate) &&
                evidenceDate.isBefore(projectEndDate)) &&
                !(evidenceDate.isEqual(projectEndDate) ||
                        evidenceDate.isEqual(projectStartDate))) {
            errorMessage = "Dates must fall within project dates";
        }

        return errorMessage;
    }

    public boolean containsNoLetter(String sample) {
        for (int i = 0; i < sample.length(); ++i) {
            if (Character.isLetter(sample.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Takes the parameters and returns the appropriate evidence list based on search priority
     *
     * @param model        The Spring model
     * @param userId       Id of user to get evidence from
     * @param projectId    Id of project to get evidence from
     * @param categoryName name of category to get evidence from
     * @param skillName    name of skill to get evidence from
     */
    private void setTitle(Model model, Integer userId, Integer projectId, String categoryName, String skillName) {
        if (categoryName != null) {
            setPageTitle(model, "Evidence from category: " + categoryName);
        } else if (skillName != null) {
            setPageTitle(model, "Evidence from skill tag: " + skillName.replaceAll("_", " "));
        } else if (userId != null || projectId != null) {
            UserResponse userReply = accountClientService.getUserById(userId); // Get the user
            setPageTitle(model, "Evidence from user: " + userReply.getUsername());
        }

    }

    /**
     * Directs the user to the evidence page with required params
     *
     * @return redirects to the landing page
     */
    @GetMapping("/search-evidence")
    public String searchEvidenceParam(@RequestParam(required = false, value = "ui") String userId,
                                      @RequestParam(required = false, value = "pi") String projectId,
                                      @RequestParam(required = false, value = "si") String skillName,
                                      @RequestParam(required = false, value = "ci") String categoryName) {
        String returnString = "redirect:evidence?";
        if (userId != null) {
            returnString += "ui=" + (userId);
        }
        if (projectId != null) {
            returnString += "pi=" + (projectId);
        }
        if (skillName != null) {
            returnString += "si=" + (skillName);
        }
        if (categoryName != null) {
            returnString += "ci=" + (categoryName);
        }

        return returnString;

    }
}
