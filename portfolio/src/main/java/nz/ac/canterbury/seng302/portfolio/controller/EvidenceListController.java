package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.model.evidence.SkillTag;
import nz.ac.canterbury.seng302.portfolio.model.evidence.SkillTagRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
  private EvidenceService evidenceService;

  private String errorMessage = "";

  Logger logger = LoggerFactory.getLogger(EvidenceListController.class);

  /**
   * Directs the user to the landing project page
   * @param principal
   * @param model The model to be used by the application for web integration
   * @return redirects to the landing page
   * @throws Exception
   */
  @GetMapping("/evidence")
  public String evidenceListController( @AuthenticationPrincipal AuthState principal,
                                        @RequestParam(required = false , value="ui") Integer userId,
                                        @RequestParam(required = false , value="pi") Integer projectId,
                                        @RequestParam(required = false , value="si") Integer skillId,
                                        @RequestParam(required = false , value="ci") Integer categoryId,
                                        Model model) throws Exception {
    logger.info(String.format("Fetching evidence details"));

    setPageTitle(model,"List Of Evidence");

    List<SkillTag> skillList = skillRepository.findAll();


    List<Evidence> evidenceList = evidenceService.getFilteredEvidenceForUserInProject(userId, projectId, categoryId, skillId);
    setTitle(model, userId, projectId, categoryId, skillId);
    HashMap<Integer, List<String>> evidenceSkillMap = new HashMap<>();
    for (Evidence evidence: evidenceList) {
      evidenceSkillMap.put(evidence.getId(), evidenceService.getSkillTagStringsByEvidenceId(evidence.getId()));
    }
    model.addAttribute("skillMap", evidenceSkillMap);
    model.addAttribute("evidenceList", evidenceList);
    Set<String> skillTagList = evidenceService.getAllUniqueSkills();
    Set<String> skillTagListNoSkill = evidenceService.getAllUniqueSkills();
    skillTagListNoSkill.remove("No_skills");
    model.addAttribute("allSkills", skillTagList);
    model.addAttribute("autoSkills", skillTagListNoSkill);
    model.addAttribute("skillList", skillList);

    Integer id = AuthStateInformer.getId(principal);

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
   * @param principal
   * @param title evidence title
   * @param date evidence date
   * @param projectId the id of the project that the evidence is linked too
   * @param categories the category the evidence is associated with
   * @param skills the skills the evidence is associated with
   * @param links are an optional list of links associated with this new piece of evidence
   * @param description evidence description
   * @param model The model to be used by the application for web integration
   * @return redirect to the evidence page
   * @throws Exception
   */
  @PostMapping("/add-evidence")
  public String addEvidence(
          @AuthenticationPrincipal AuthState principal,
          @RequestParam(value = "titleInput") String title,
          @RequestParam(value = "dateInput") String date,
          @RequestParam(value = "projectId") Integer projectId,
          @RequestParam(value = "categoryInput") String categories,
          @RequestParam(value = "skillInput") String skills,
          @RequestParam(value = "linksInput") Optional <String> links,
          @RequestParam(value = "descriptionInput") String description,
          Model model
  ) throws Exception {
      logger.info(String.format("Attempting to add new evidence"));

      Integer accountID = AuthStateInformer.getId(principal);
      Project parentProject = projectService.getProjectById(projectId);
      if (parentProject == null) {
          logger.debug("[EVIDENCE] Attempted to add evidence to a project that could not be found");
          // TODO: Change to 404
          errorMessage = "Project does not exist";
          return "redirect:evidence?pi=" + projectId;
      }
      LocalDate evidenceDate = LocalDate.parse(date);
      LocalDate projectStartDate = parentProject.getLocalStartDate();
      LocalDate projectEndDate = parentProject.getLocalEndDate();

      // Check if the given evidence date is within the project date
      if (!(evidenceDate.isAfter(projectStartDate) && evidenceDate.isBefore(projectEndDate))
              && !(evidenceDate.isEqual(projectEndDate) || evidenceDate.isEqual(projectStartDate))) {
          errorMessage = "Dates must fall within project dates";
          return "redirect:evidence?pi=" + projectId;
      }

      this.errorMessage = validateMandatoryFields(title, description, evidenceDate, projectStartDate, projectEndDate);

      // If error occurs, return early
      if (!errorMessage.equals("")) {
          model.addAttribute("errorMessage", errorMessage);
          return "redirect:evidence?pi=" + projectId;
      }

      // Extract then validate links
      List<String> extractedLinks = null;
      if (links.isPresent()) {
          extractedLinks = extractListFromHTMLString(links.get());
          Optional<String> possibleError = validateLinks(extractedLinks);
          if (possibleError.isPresent()) {
              errorMessage = possibleError.get();
              return "redirect:evidence?pi=" + projectId;
          }
      }

      // If no error occurs with the mandatoryfields then save the evidence to the repo and relavent skills or links
      Evidence evidence = new Evidence(accountID, parentProject, title, description, evidenceDate);
      logger.info("[EVIDENCE] Saving evidence to repo");
      evidenceRepository.save(evidence);
      logger.info(String.format("[EVIDENCE] Saved evidence to repo, id=<%s>", evidence.getId()));
      errorMessage = "Evidence has been added";

      addSkillsToRepo(parentProject, evidence, skills);

      // If there's no skills, add the no_skills
      List<EvidenceTag> evidenceTagList = evidenceTagRepository.findAllByParentEvidenceId(evidence.getId());
      if (evidenceTagList.size() == 0) {
          SkillTag noSkillTag = skillRepository.findByTitle("No_skills");
          EvidenceTag noSkillEvidence = new EvidenceTag(noSkillTag, evidence);
          evidenceTagRepository.save(noSkillEvidence);
      }

      if (extractedLinks != null) {
          logger.debug("[EVIDENCE] Saving web links");
          webLinkRepository.saveAll(constructLinks(extractedLinks, evidence));
      }

      return "redirect:evidence?pi=" + projectId;
  }

  /**
   * Validate web link strings
   * @param links
   * @return an error message, if something is wrong
   */
  private Optional<String> validateLinks(List<String> links) {
    for (String link : links) {
      if (!WebLink.urlHasProtocol(link)) {
        logger.trace("[WEBLINK] Rejecting web link as the link is not valid, link: " + link);
        return Optional.of("The provided link is not valid, must contain http(s):// protocol: " + link);
      };
    }
    return Optional.empty();
  }

  /**
   * Construct web links, must be validated first.
   * @param links
   * @param parentEvidence
   * @return
   */
  private List<WebLink> constructLinks(List<String> links, Evidence parentEvidence) {
    ArrayList<WebLink> resultLinks = new ArrayList<WebLink>();
    // Validate all links
    for (String link : links) {
      // Web links are valid, so construct them all
      resultLinks.add(new WebLink(link, parentEvidence));
    }
    return resultLinks;
  }

  /**
   * Splits an HTML form input list, into multiple array elements.
   * @param stringFromHTML
   * @return
   */
  private List<String> extractListFromHTMLString(String stringFromHTML) {
      if (stringFromHTML.equals("")) {
          return new ArrayList();
      }

      List<String> resultList = Arrays.asList(stringFromHTML.split(" "));
      return resultList;
  }

  private String validateMandatoryFields(String title, String description, LocalDate evidenceDate, LocalDate projectStartDate, LocalDate projectEndDate) {
      this.errorMessage = "";

      // https://stackoverflow.com/questions/14278170/how-to-check-whether-a-string-contains-at-least-one-alphabet-in-java
      // Checks if there is at least one character in title
      if(!(title.matches(".*[a-zA-Z]+.*")) || title.length() <= 1) {
          errorMessage = "Title must more than one character and should not be only made from numbers and symbols";
      }

      // Checks if there is at least one character in description
      if(!(description.matches(".*[a-zA-Z]+.*")) || description.length() <= 1) {
          errorMessage = "Description must more than one character and should not be only made from numbers and symbols";
      }

      // Check if the given evidence date is within the project date
      if (!(evidenceDate.isAfter(projectStartDate) && evidenceDate.isBefore(projectEndDate))
          && !(evidenceDate.isEqual(projectEndDate) || evidenceDate.isEqual(projectStartDate))) {
          errorMessage = "Dates must fall within project dates";
      }

      return errorMessage;
  }

  private void addSkillsToRepo(Project parentProject, Evidence evidence, String skills) {
      //Create new skill for any skill that doesn't exist, create evidence tag for all skills
      if (skills.replace(" ", "").length() > 0) {
          List<String> skillList = extractListFromHTMLString(skills);

          for (String skillString : skillList) {
              String validSkillString = skillString.replace(" ", "_");
              SkillTag skillFromRepo = skillRepository.findByTitle(validSkillString);

              if (skillFromRepo == null) {
                  SkillTag newSkill = new SkillTag(parentProject, validSkillString);
                  skillRepository.save(newSkill);
                  EvidenceTag noSkillEvidence = new EvidenceTag(newSkill, evidence);
                  evidenceTagRepository.save(noSkillEvidence);
              } else {
                  EvidenceTag noSkillEvidence = new EvidenceTag(skillFromRepo, evidence);
                  evidenceTagRepository.save(noSkillEvidence);
              }
          }
      }

      // If there's no skills, add the no_skills
      List<EvidenceTag> evidenceTagList = evidenceTagRepository.findAllByParentEvidenceId(evidence.getId());
      if (evidenceTagList.size() == 0) {
          SkillTag noSkillTag = skillRepository.findByTitle("No_skills");
          EvidenceTag noSkillEvidence = new EvidenceTag(noSkillTag, evidence);
          evidenceTagRepository.save(noSkillEvidence);
      }

  }

  /**
   * Takes the parameters and returns the appropriate evidence list based on search priority
   * @param model The Spring model
   * @param userId Id of user to get evidence from
   * @param projectId Id of project to get evidence from
   * @param categoryId Id of category to get evidence from
   * @param skillId Id of skill to get evidence from
   * @return A properly sorted and filtered list of evidence
   * @throws Exception
   */
  private void setTitle(Model model, Integer userId, Integer projectId, Integer categoryId, Integer skillId) throws Exception {

    if (projectId != null){
      Project project = projectService.getProjectById(projectId);
      setPageTitle(model, "Evidence from project: " + project.getName());
      return;
    } else if (userId != null){
      UserResponse userReply = accountClientService.getUserById(userId); // Get the user
      setPageTitle(model, "Evidence from user: " + userReply.getUsername());
      return;
    }else if (categoryId != null){
      switch (categoryId) {
        case 0:
          setPageTitle(model, "Evidence from category: Quantitative Skills");
          return;
        case 1:
          setPageTitle(model, "Evidence from category: Qualitative Skills");
          return;
        case 2:
          setPageTitle(model, "Evidence from category: Service");
          return;
      }
    } else if (skillId != null){
      Optional<SkillTag> skillTag = skillRepository.findById(skillId);
      if (!skillTag.isPresent()) {
        throw new InvalidArgumentException("Skill with corresponding ID does not exist");
      }
      setPageTitle(model, "Evidence from skill tag: " + skillTag.get().getTitle().replaceAll("_", " "));
      return;
    }else{
      return;
    }
  }


  /**
   * Directs the user to the evidence page with required params
   * @param principal
   * @param model The model to be used by the application for web integration
   * @return redirects to the landing page
   * @throws Exception
   */
  @GetMapping("/search-evidence")
  public String searchEvidenceParam( @AuthenticationPrincipal AuthState principal,
                                        @RequestParam(required = false , value="ui") String user_id,
                                        @RequestParam(required = false , value="pi") String project_id,
                                        @RequestParam(required = false , value="si") String skill_id,
                                        @RequestParam(required = false , value="ci") String category_id,
                                        Model model) throws Exception {
    String returnString = "redirect:evidence?";
    if (user_id != null) {
      returnString += "ui=" + (user_id);
    }
    if (project_id != null) {
      returnString += "pi=" + (project_id);
    }
    if (skill_id != null) {
      returnString += "si=" + (skill_id);
    }
    if (category_id != null) {
      returnString += "ci=" + (category_id);
    }

    return returnString;


  }

}
