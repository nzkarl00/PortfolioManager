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

    List<Evidence> evidenceList;

    List<SkillTag> skillList = skillRepository.findAll();

    evidenceList = getEvidenceFunction(userId, projectId, categoryId, skillId);

    model.addAttribute("evidenceList", evidenceList);
    Set<String> skillTagList = evidenceService.getAllUniqueSkills();
    model.addAttribute("allSkills", skillTagList);
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


  /**
   * Saves a new evidence if the user has permissions and the correct input is given
   * @param principal
   * @param title evidence title
   * @param date evidence date
   * @param projectId the id of the project that the evidence is linked too
   * @param evidenceCategory the category the evidence is associated with
   * @param skills the skills the evidence is associated with
   * @param description evidence description
   * @param model The model to be used by the application for web integration
   * @return redirect to the evidence page
   * @throws Exception
   */
  @PostMapping("/add-evidence")
  public String newEvidence(
          @AuthenticationPrincipal AuthState principal,
          @RequestParam(value = "titleInput") String title,
          @RequestParam(value = "dateInput") String date,
          @RequestParam(value = "projectId") Integer projectId,
          @RequestParam(value = "categoryHidden") String categories,
          @RequestParam(value = "skillHidden") String skills,
          @RequestParam(value = "linksInput") Optional <String> links,
          @RequestParam(value = "descriptionInput") String description,
          Model model
  ) throws Exception {
    System.out.println(categories);
    logger.info(String.format("Attempting to add new evidence"));
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

    Integer accountID = AuthStateInformer.getId(principal);
    Project parentProject = projectService.getProjectById(projectId);
    LocalDate evidenceDate = LocalDate.parse(date);
    LocalDate projectStartDate = parentProject.getLocalStartDate();
    LocalDate projectEndDate = parentProject.getLocalEndDate();

    // Check if the given evidence date is within the project date
    if (!(evidenceDate.isAfter(projectStartDate) && evidenceDate.isBefore(projectEndDate))
            && !(evidenceDate.isEqual(projectEndDate) || evidenceDate.isEqual(projectStartDate))) {
      errorMessage = "Dates must fall within project dates";
    }
    // If no error occurs then save the evidence to the repo and relavent skills
    if(errorMessage.equals("")) {
      Evidence evidence = new Evidence(accountID, parentProject, title, description, evidenceDate);
      evidenceRepository.save(evidence);
      logger.info(String.format("Evidence has been created and saved to the repo evidenceId=<%s>", evidence.getId()));
      errorMessage = "Evidence has been added";

      //Create new skill for any skill that doesn't exist, create evidence tag for all skills
      if (skills.replace(" ", "").length() > 0) {
        List<String> SkillItemsString = Arrays.asList(skills.split("~"));
        Project projectCurrent = projectService.getProjectById(projectId);
        for (String skillString : SkillItemsString) {
          String validSkillString = skillString.replace(" ", "_");
          SkillTag skillFromRepo = skillRepository.findByTitle(validSkillString);
          if (skillFromRepo == null) {
            SkillTag newSkill = new SkillTag(projectCurrent, validSkillString);
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
    model.addAttribute("errorMessage", errorMessage);
    return "redirect:evidence?pi=" + projectId;
  }
  /**
   * Takes the parameters and returns the appropriate evidence list based on search priority
   * @param userId Id of user to get evidence from
   * @param projectId Id of project to get evidence from
   * @param categoryId Id of category to get evidence from
   * @param skillId Id of skill to get evidence from
   * @return A properly sorted and filtered list of evidence
   * @throws Exception
   */
  private List<Evidence> getEvidenceFunction(Integer userId, Integer projectId, Integer categoryId, Integer skillId) throws Exception {

    if (projectId != null){
      Project project = projectService.getProjectById(Integer.valueOf(projectId));
      return evidenceRepository.findAllByAssociatedProjectOrderByDateDesc(project);
    } else if (userId != null){
      return evidenceRepository.findAllByParentUserIdOrderByDateDesc(Integer.valueOf(userId));
    }else if (categoryId != null){
      return evidenceRepository.findAllByOrderByDateDesc();
    }else if (skillId != null){
      List<EvidenceTag> evidenceTags = evidenceTagRepository.findAllByParentSkillTagId(Integer.valueOf(skillId));
      List<Evidence> evidenceSkillList = new ArrayList<>();
      for (EvidenceTag tag: evidenceTags){
        evidenceSkillList.add(tag.getParentEvidence());
      }
      return evidenceSkillList;
    }else{
      return evidenceRepository.findAllByOrderByDateDesc();
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
