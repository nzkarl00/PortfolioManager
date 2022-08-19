package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.CustomExceptions;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvidenceService {
    @Autowired
    private SkillTagRepository skillTagRepository;
    @Autowired
    EvidenceRepository evidenceRepository;
    @Autowired
    EvidenceTagRepository evidenceTagRepository;
    @Autowired
    ProjectService projectService;
    @Autowired
    private CategoryRepository categoryRepository;

    Logger logger = LoggerFactory.getLogger(EvidenceService.class);

    /**
     * Get a list of all unique skill tag names
     */
    public Set<String> getAllUniqueSkills() {
        List<SkillTag> tagList = skillTagRepository.findAll();
        Set<String> skillList = new HashSet<>();
        tagList.forEach(tag -> skillList.add(tag.getTitle()));
        return skillList;
    }

    /**
     * Takes the parameters and returns the appropriate evidence list based on search priority
     * @param userId ID of user to get evidence from
     * @param projectId ID of project to get evidence from
     * @param categoryName name of category to get evidence from
     * @param skillName name of skill to get evidence from
     * @return A properly sorted and filtered list of evidence
     * @throws CustomExceptions.ProjectItemNotFoundException if the project associated with the given projectID does not exist
     */
    public List<Evidence> getFilteredEvidenceForUserInProject(Integer userId, Integer projectId, String categoryName, String skillName) throws CustomExceptions.ProjectItemNotFoundException {
        if (projectId != null){
            Project project = projectService.getProjectById(projectId);
            return evidenceRepository.findAllByAssociatedProjectOrderByDateDesc(project);
        } else if (userId != null){
            return evidenceRepository.findAllByParentUserIdOrderByDateDesc(userId);
        }else if (categoryName != null){
            List<Category> categoryTag = categoryRepository.findAllByCategoryName(categoryName);
            List<Evidence> evidenceCategoryList = new ArrayList<>();
            for (Category tag: categoryTag){
                evidenceCategoryList.add(tag.getParentEvidence());
            }
            evidenceCategoryList.sort((o1, o2) -> {
                // compare two instance of `Score` and return `int` as result.
                return o2.getDate().compareTo(o1.getDate());
            });
            return evidenceCategoryList;
        }else if (skillName != null){
            List<EvidenceTag> evidenceTags = evidenceTagRepository.findAllByParentSkillTagId(skillTagRepository.findByTitle(skillName).getId());
            List<Evidence> evidenceSkillList = new ArrayList<>();
            for (EvidenceTag tag: evidenceTags){
                evidenceSkillList.add(tag.getParentEvidence());
            }
            evidenceSkillList.sort((o1, o2) -> {
                // compare two instance of `Score` and return `int` as result.
                return o2.getDate().compareTo(o1.getDate());
            });
            return evidenceSkillList;
        }else{
            return evidenceRepository.findAllByOrderByDateDesc();
        }
    }



    /**
     * This function loops through the provided evidences from the filtering
     * and retrieves all the skill tags from them to display in the side panel
     * @param evidenceList the list of evidence to find the skill tags from
     * @return the final list of skill tags
     */
    public Set<SkillTag> getFilterSkills(List<Evidence> evidenceList) {
        Set<SkillTag> returning = new HashSet<>();
        for (Evidence evidence : evidenceList) {
            for (EvidenceTag tag: evidenceTagRepository.findAllByParentEvidenceId(evidence.getId())) {
                if (!tag.getParentSkillTag().getTitle().equals("No_skills")) {
                    returning.add(tag.getParentSkillTag());
                }
            }
        }
        returning.add(skillTagRepository.findByTitle("No_skills"));
        return returning;
    }

    /**
     * Takes an evidence ID and returns a list of all skill tag titles that are associated with it.
     * @param evidenceId The evidence ID to be checked against
     * @return List of skill tag title strings
     */
    public List<String> getSkillTagStringsByEvidenceId(int evidenceId) {
        List<EvidenceTag> evidenceTagList = evidenceTagRepository.findAllByParentEvidenceId(evidenceId);
        return evidenceTagList.stream().map(evidenceTag -> evidenceTag.getParentSkillTag().getTitle()).collect(Collectors.toList());
    }

    /**
     * Takes an evidence ID and returns the string name of all categories that belong to it
     * @param evidenceId The ID of evidence being searched for categories
     * @return List of category names as strings that belong to the evidence of id evidenceId
     */
    public List<String> getCategoryStringsByEvidenceId(int evidenceId) {
        List<Category> categoryList = categoryRepository.findAllByParentEvidenceId(evidenceId);
        return categoryList.stream().map(Category::getCategoryName).collect(Collectors.toList());
    }

    public void addSkillsToRepo(Project parentProject, Evidence evidence, String skills) {
        //Create new skill for any skill that doesn't exist, create evidence tag for all skills
        if (skills.replace(" ", "").length() > 0) {
            List<String> skillList = extractListFromHTMLStringSkills(skills);

            for (String skillString : skillList) {
                String validSkillString = skillString.replace(" ", "_");
                SkillTag skillFromRepo = skillTagRepository.findByTitle(validSkillString);

                saveSkillsAndEvidenceTags(parentProject, evidence, validSkillString, skillFromRepo, skillTagRepository, evidenceTagRepository);
            }
        }

        // If there's no skills, add the no_skills
        List<EvidenceTag> evidenceTagList = evidenceTagRepository.findAllByParentEvidenceId(evidence.getId());
        if (evidenceTagList.size() == 0) {
            SkillTag noSkillTag = skillTagRepository.findByTitle("No_skills");
            EvidenceTag noSkillEvidence = new EvidenceTag(noSkillTag, evidence);
            evidenceTagRepository.save(noSkillEvidence);
        }

    }

    public static void saveSkillsAndEvidenceTags(Project parentProject, Evidence evidence, String validSkillString, SkillTag skillFromRepo, SkillTagRepository skillTagRepository, EvidenceTagRepository evidenceTagRepository) {
        if (skillFromRepo == null) {
            SkillTag newSkill = new SkillTag(parentProject, validSkillString);
            skillTagRepository.save(newSkill);
            EvidenceTag noSkillEvidence = new EvidenceTag(newSkill, evidence);
            evidenceTagRepository.save(noSkillEvidence);
        } else {
            EvidenceTag noSkillEvidence = new EvidenceTag(skillFromRepo, evidence);
            evidenceTagRepository.save(noSkillEvidence);
        }
    }

    /**
     * Splits an HTML form input list, into multiple array elements.
     * @param stringFromHTML The string containing items delimited by ~
     * @return an array representation of the list
     */
    private List<String> extractListFromHTMLStringSkills(String stringFromHTML) {
        if (stringFromHTML.equals("")) {
            return Collections.emptyList();
        }

        return Arrays.asList(stringFromHTML.split("~"));
    }



    /**
     * Validate web link strings
     * @param links Web A list of web links to be checked
     * @return an error message, if something is wrong
     */
    public Optional<String> validateLinks(List<String> links) {
        for (String link : links) {
            if (!WebLink.urlHasProtocol(link)) {
                logger.trace("[WEBLINK] Rejecting web link as the link is not valid, link: " + link);
                return Optional.of("The provided link is not valid, must contain http(s):// protocol: " + link);
            }
        }
        return Optional.empty();
    }
}
