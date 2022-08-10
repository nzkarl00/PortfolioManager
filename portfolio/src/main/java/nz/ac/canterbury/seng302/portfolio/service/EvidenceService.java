package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
     * @param userId Id of user to get evidence from
     * @param projectId Id of project to get evidence from
     * @param categoryId Id of category to get evidence from
     * @param skillId Id of skill to get evidence from
     * @return A properly sorted and filtered list of evidence
     * @throws Exception
     */
    public List<Evidence> getFilteredEvidenceForUserInProject(Integer userId, Integer projectId, Integer categoryId, Integer skillId) throws Exception {
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
     * Takes an evidence ID and returns a list of all skill tag titles that are associated with it.
     * @param evidenceId The evidence ID to be checked against
     * @return List of skilltag title strings
     */
    public List<String> getSkillTagStringsByEvidenceId(int evidenceId) {
        List<EvidenceTag> evidenceTagList = evidenceTagRepository.findAllByParentEvidenceId(evidenceId);
        return evidenceTagList.stream().map(evidenceTag -> evidenceTag.getParentSkillTag().getTitle()).collect(Collectors.toList());
    }

    /**
     * Takes an evidence ID and returns a list of all skill tags that are associated with it.
     * @param evidenceId The evidence ID to be checked against
     * @return List of skilltag title strings
     */
    public List<SkillTag> getSkillTagByEvidenceId(int evidenceId) {
        List<EvidenceTag> evidenceTagList = evidenceTagRepository.findAllByParentEvidenceId(evidenceId);
        return evidenceTagList.stream().map(evidenceTag -> evidenceTag.getParentSkillTag()).collect(Collectors.toList());
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
}
