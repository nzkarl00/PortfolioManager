package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
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

    /**
     * Get a list of all unique skill tag names
     */
    public Set<String> getAllUniqueSkills() {
        List<SkillTag> tagList = (List<SkillTag>) skillTagRepository.findAll();
        Set<String> skillList = new HashSet<String>();
        tagList.stream().forEach(tag -> skillList.add((String) tag.getTitle()));
        return skillList;
    }
}
