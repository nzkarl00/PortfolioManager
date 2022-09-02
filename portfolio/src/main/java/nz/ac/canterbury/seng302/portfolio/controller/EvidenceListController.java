package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.AuthenticatedUser;
import nz.ac.canterbury.seng302.portfolio.CustomExceptions;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.openqa.selenium.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.*;

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
  private NavController navController;
  @Autowired
  private GroupsClientService groupsClientService;
  @Autowired
  private EvidenceService evidenceService;

  private String errorMessage = "";

  Logger logger = LoggerFactory.getLogger(EvidenceListController.class);

  /**
   * Directs the user to the landing project page
   * @param principal auth state for the currently authenticated user
   * @param model The model to be used by the application for web integration
   * @return redirects to the landing page
   * @throws Exception which is raised by the repositories having a potential failure when reading objects from the DB
   */
  @GetMapping("/evidence")
  public String evidenceListController( @AuthenticationPrincipal AuthState principal,
                                        @RequestParam(required = false , value="ui") Integer userId,
                                        @RequestParam(required = false , value="pi") Integer projectId,
                                        @RequestParam(required = false , value="si") String skillName,
                                        @RequestParam(required = false , value="ci") String categoryName,
                                        Model model) throws Exception {
    logger.info("[EVIDENCE] Request to view list of evidence");

    setPageTitle(model,"List Of Evidence");

    List<SkillTag> skillList = skillRepository.findAll();

    List<Evidence> evidenceList = evidenceService.getFilteredEvidenceForUserInProject(userId, projectId, categoryName, skillName);
    setTitle(model, userId, projectId, categoryName, skillName);
    HashMap<Integer, List<String>> evidenceSkillMap = new HashMap<>();
    HashMap<Integer, List<String>> evidenceCategoryMap = new HashMap<>();
    for (Evidence evidence: evidenceList) {
      evidenceSkillMap.put(evidence.getId(), evidenceService.getSkillTagStringsByEvidenceId(evidence.getId()));
      evidenceCategoryMap.put(evidence.getId(), evidence.getCategoryStrings());
    }
    int id = AuthStateInformer.getId(principal);

    //TODO get rid of once this is actually used
    logger.info("[EVIDENCE] getting all the groups for user");
    logger.info(groupsClientService.getAllGroupsForUser(id).toString());

    model.addAttribute("skillMap", evidenceSkillMap);
    model.addAttribute("categoryMap", evidenceCategoryMap);
    model.addAttribute("evidenceList", evidenceList);
    Set<String> skillTagList = evidenceService.getAllUniqueSkills();
      Set<String> skillTagListNoSkill = evidenceService.getAllUniqueSkills();
    skillTagListNoSkill.remove("No_skills");
    model.addAttribute("allSkills", skillTagList);
      model.addAttribute("autoSkills", skillTagListNoSkill);
    model.addAttribute("skillList", skillList);
    model.addAttribute("filterSkills", evidenceService.getFilterSkills(evidenceList));
    model.addAttribute("userID", id);

    // Attributes For header
    UserResponse userReply;
    userReply = accountClientService.getUserById(id);
    navController.updateModelForNav(principal, model, userReply, id);
    // End of Attributes for header
    //Attributes for form
    boolean showForm = false;
    if (projectId != null) {
      showForm = true;
      model.addAttribute("date", DateParser.dateToStringHtml(new Date()));
      Project project = projectService.getProjectById(projectId);
      model.addAttribute("project", project);
      model.addAttribute("projectId", project.getId());
    }
    model.addAttribute("showForm", showForm);
    model.addAttribute("errorMessage", errorMessage);
    this.errorMessage = "";

    return "evidenceList";
  }

  private void setPageTitle(Model model, String title) {
    model.addAttribute("title", title);
  }

  /**
   * Saves a new evidence if the user has permissions and the correct input is given
   * @param principal auth state for the currently authenticated user
   * @param title evidence title
   * @param date evidence date
   * @param projectId the id of the project that the evidence is linked too
   * @param otherUsers A list of usernames of other people (not the author) who worked on this evidence
   * @param categories the category the evidence is associated with
   * @param skills the skills the evidence is associated with
   * @param links are an optional list of links associated with this new piece of evidence
   * @param description evidence description
   * @param model The model to be used by the application for web integration
   * @return redirect to the evidence page
   * @throws CustomExceptions.ProjectItemNotFoundException if the project ID is not associated with any existing project
   */
  @PostMapping("/add-evidence")
  public String addEvidence(
          @AuthenticationPrincipal AuthState principal,
          @RequestParam(value = "titleInput") String title,
          @RequestParam(value = "dateInput") String date,
          @RequestParam(value = "projectId") Integer projectId,
          @RequestParam(value = "otherUsers") Optional <String> otherUsers,
          @RequestParam(value = "categoryInput") String categories,
          @RequestParam(value = "skillInput") String skills,
          @RequestParam(value = "linksInput") Optional <String> links,
          @RequestParam(value = "descriptionInput") String description,
          Model model
  ) throws CustomExceptions.ProjectItemNotFoundException {
      logger.info("[EVIDENCE] Attempting to add new evidence");
      if (!principal.getIsAuthenticated()) {
          logger.debug("[EVIDENCE] Redirecting, user not authenticated");
          return "redirect:evidence?pi=" + projectId.toString();
      }

      Integer accountID = AuthStateInformer.getId(principal);
      model.addAttribute("authorId", accountID);
      AuthenticatedUser thisUser = new AuthenticatedUser(principal);
      model.addAttribute("authorUserName", thisUser.getUsername());
      Project parentProject = projectService.getProjectById(projectId);
      if (parentProject == null) {
          logger.debug("[EVIDENCE] Attempted to add evidence to a project that could not be found");
          // In future we can use a 404 here
          errorMessage = "Project does not exist";
          return "redirect:evidence";
      }

      // Extract then validate links
      List<String> extractedLinks = extractListFromHTMLString(links.orElse(""));
      Optional<String> possibleError = evidenceService.validateLinks(extractedLinks);
      // prioritise mandatory fields first, then link errors
      this.errorMessage = possibleError.orElse(errorMessage);

      this.errorMessage = validateMandatoryFields(title, description, date, parentProject);

      // If error occurs, return early
      if (!errorMessage.equals("")) {
          model.addAttribute("errorMessage", errorMessage);
          return "redirect:evidence?pi=" + projectId;
      }

      int categoriesInt = Evidence.categoryStringToInt(categories);

      List<String> extractedUsers = extractListFromHTMLString(otherUsers.orElse(""));

      //TODO program out once front-end is working as intended
      if (extractedUsers.isEmpty()) {
          extractedUsers.add(accountID + ":" + thisUser.getUsername());
      }

      List<Evidence> allUserEvidence = evidenceService.generateEvidenceForUsers(extractedUsers, parentProject, title, description, LocalDate.parse(date), categoriesInt);

      // If no error occurs with the mandatoryfields then save the evidence to the repo and relavent skills or links
      logger.info("[EVIDENCE] Saving evidence to repo");
      for (Evidence evidence : allUserEvidence) {
          logger.info(String.format("[EVIDENCE] Saved evidence to repo, id=<%s>", evidence.getId()));
          errorMessage = "Evidence has been added";

          evidenceService.addSkillsToRepo(parentProject, evidence, skills);

          noSkillsCheck(evidence);

          if (!extractedLinks.isEmpty()) {
              logger.debug("[EVIDENCE] Saving web links");
              try {
                  webLinkRepository.saveAll(constructLinks(extractedLinks, evidence));
              } catch (MalformedURLException e) {
                  logger.error("[EVIDENCE] Somehow links were attempted for construction with malformed URL", e);
                  logger.error("[EVIDENCE] Links not saved");
              }
          }
      }
      return "redirect:evidence?pi=" + projectId;
  }

    @PostMapping("/delete-evidence")
    public String deleteEvidence(@RequestParam(required = false, value = "projectId") String projectId,
                                @RequestParam(value = "evidenceId") String evidenceId,
                                @AuthenticationPrincipal AuthState principal) {
        Evidence targetEvidence = evidenceRepository.findById(Integer.parseInt(evidenceId));
        if (targetEvidence == null) {
            logger.debug("[EVIDENCE] Redirecting, evidence id " + evidenceId + " does not exist");
            return "redirect:evidence?pi=" + projectId;
        }
        Integer accountID = AuthStateInformer.getId(principal);
        if (!principal.getIsAuthenticated() || accountID != targetEvidence.getParentUserId()) {
            logger.debug("[EVIDENCE] Redirecting, user does not have permissions to delete evidence " + evidenceId);
            return "redirect:evidence?pi=" + projectId;
        }
        evidenceRepository.delete(targetEvidence);
        return "redirect:evidence?pi=" + projectId;
    }




    /**
    * Construct web links, must be validated first.
    * @param links The link of links which are associated with a given piece of evidence
    * @param parentEvidence The evidence object which the weblink belongs to
    * @return An array of weblink objects which contain both the link text and the parent evidence
    */
  private List<WebLink> constructLinks(List<String> links, Evidence parentEvidence) throws MalformedURLException {
    ArrayList<WebLink> resultLinks = new ArrayList<>();
    // Validate all links
    for (String link : links) {
      // Web links are valid, so construct them all
      resultLinks.add(new WebLink(link, parentEvidence));
    }
    return resultLinks;
  }

  /**
   * Splits an HTML form input list, into multiple array elements.
   * @param stringFromHTML The string of values posted by the evidence form in format Item1~Item2~Item3
   * @return An array of the individual values present in the string
   */
  private List<String> extractListFromHTMLString(String stringFromHTML) {
      if (stringFromHTML.equals("")) {
          return new ArrayList<>();
      }

      return Arrays.asList(stringFromHTML.split(" "));
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
   * @param title the title field
   * @param description the description field
   * @param date the string representation of the date for the piece of evidence
   * @param parentProject the Project the piece of evidence 'belongs' to
   * @return A String error message if requirement not met, else return ""
  */
  private String validateMandatoryFields(String title, String description, String date, Project parentProject) {
      this.errorMessage = "";

      LocalDate evidenceDate = LocalDate.parse(date);
      LocalDate projectStartDate = parentProject.getLocalStartDate();
      LocalDate projectEndDate = parentProject.getLocalEndDate();

      // Check if the given evidence date is within the project date
      if (!(evidenceDate.isAfter(projectStartDate) && evidenceDate.isBefore(projectEndDate))
          && !(evidenceDate.isEqual(projectEndDate) || evidenceDate.isEqual(projectStartDate))) {
          // Give this error priority
          return "Dates must fall within project dates";
      }

      // https://stackoverflow.com/questions/14278170/how-to-check-whether-a-string-contains-at-least-one-alphabet-in-java
      // Checks if there is at least one character in title
      if(title.length() < 1 || containsNoLetter(title)) {
          errorMessage = "Title must more than one character and should not be only made from numbers and symbols";
      }

      // Checks if there is at least one character in description
      if(description.length() < 1 || containsNoLetter(description)) {
          errorMessage = "Description must more than one character and should not be only made from numbers and symbols";
      }

      // Check if the given evidence date is within the project date
      if (!(evidenceDate.isAfter(projectStartDate) &&
          evidenceDate.isBefore(projectEndDate))
          && !(evidenceDate.isEqual(projectEndDate) ||
          evidenceDate.isEqual(projectStartDate))) {
          errorMessage = "Dates must fall within project dates";
      }

      return errorMessage;
}

  public boolean containsNoLetter(String sample) {
      for (int i=0; i < sample.length(); ++i) {
          if (Character.isLetter(sample.charAt(i))) {
              return false;
          }
      }
      return true;
  }

    /**
   * Takes the parameters and returns the appropriate evidence list based on search priority
   * @param model The Spring model
   * @param userId Id of user to get evidence from
   * @param projectId Id of project to get evidence from
   * @param categoryName name of category to get evidence from
   * @param skillName name of skill to get evidence from
   * @throws InvalidArgumentException possible exceptions can be raised from project ID not being valid and skillID not being valid
   */
  private void setTitle(Model model, Integer userId, Integer projectId, String categoryName, String skillName) throws Exception {

    if (projectId != null){
      Project project = projectService.getProjectById(projectId);
      setPageTitle(model, "Evidence from project: " + project.getName());
    } else if (userId != null){
      UserResponse userReply = accountClientService.getUserById(userId); // Get the user
      setPageTitle(model, "Evidence from user: " + userReply.getUsername());
    }else if (categoryName != null){
        setPageTitle(model, "Evidence from category: " + categoryName);
    } else if (skillName != null){
            setPageTitle(model, "Evidence from skill tag: " + skillName.replaceAll("_", " "));
    }
  }


  /**
   * Directs the user to the evidence page with required params
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
