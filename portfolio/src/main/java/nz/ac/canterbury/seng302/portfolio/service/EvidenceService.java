package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Category;
import nz.ac.canterbury.seng302.portfolio.model.evidence.CategoryRepository;
import nz.ac.canterbury.seng302.portfolio.model.evidence.SkillTag;
import nz.ac.canterbury.seng302.portfolio.model.evidence.SkillTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EvidenceService {
    @Autowired
    private SkillTagRepository skillTagRepository;
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
     * Takes an evidence ID and returns the string name of all categories that belong to it
     * @param evidenceId The ID of evidence being searched for categories
     * @return List of category names as strings that belong to the evidence of id evidenceId
     */
    public List<String> getCategoryStringsByEvidenceId(int evidenceId) {
        List<Category> categoryList = categoryRepository.findAllByParentEvidence(evidenceId);
        return categoryList.stream().map(Category::getCategoryName).collect(Collectors.toList());
    }
}
