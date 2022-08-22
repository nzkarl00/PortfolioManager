package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvidenceService {
    @Autowired
    private SkillTagRepository skillTagRepository;
    @Autowired
    private WebLinkRepository webLinkRepository;
    @Autowired
    EvidenceRepository evidenceRepository;
    @Autowired
    EvidenceTagRepository evidenceTagRepository;
    @Autowired
    ProjectService projectService;
    @Autowired
    EvidenceUserRepository evidenceUserRepository;
    @Autowired
    AccountClientService accountClientService;

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
    public List<Evidence> getFilteredEvidenceForUserInProject(Integer userId, Integer projectId, String categoryName, String skillName) throws Exception {
        if (projectId != null){
            Project project = projectService.getProjectById(Integer.valueOf(projectId));
            return evidenceRepository.findAllByAssociatedProjectOrderByDateDesc(project);
        } else if (userId != null){
            return evidenceRepository.findAllByParentUserIdOrderByDateDesc(Integer.valueOf(userId));
        }else if (categoryName != null){
            List<Category> categoryTag = categoryRepository.findAllByCategoryName(categoryName);
            List<Evidence> evidenceCategoryList = new ArrayList<>();
            for (Category tag: categoryTag){
                evidenceCategoryList.add(tag.getParentEvidence());
            }
            return evidenceCategoryList;
        }else if (skillName != null){
            List<EvidenceTag> evidenceTags = evidenceTagRepository.findAllByParentSkillTagId(skillTagRepository.findByTitle(skillName).getId());
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
     * For every user associated to a piece of evidence, duplicate the evidence and save it for them
     * And then creating an association between each evidence user and their parent evidence
     * @param extractedUsernames List of {id}:{username} that a user has said also worked on the piece of evidence they're creating
     * @param parentProject The parent project that all pieces of evidence will relate to
     * @param title The title that all pieces of evidence will relate to
     * @param description The description that all pieces of evidence will relate to
     * @param evidenceDate The evidenceDate that all pieces of evidence will relate to
     * @return
     */
    public List<Evidence> generateEvidenceForUsers(List<String> extractedUsernames, Project parentProject, String title, String description, LocalDate evidenceDate) {
        // Extract then validate usernames
        // This is assuming that the list is formatted as {id}:{username}
        List<Evidence> allEvidence = new ArrayList<>();
        List<String[]> validUsers = new ArrayList<>();

        // Validate that the username exists in the IDP
        for(String username: extractedUsernames) {
            String[] split = username.split(":");
            int userId = Integer.parseInt(split[0]);

            // Make a call to the IDP and make sure the username and given userId match with what is expected
            UserResponse response = accountClientService.getUserById(userId);
            if (response.getUsername().equals(split[1])) {
                validUsers.add(split);
            }
        }

        //Loop through all associated users so we can create their pieces of evidence
        for(String[] user: validUsers) {
            Evidence userEvidence = new Evidence(Integer.parseInt(user[0]), parentProject, title, description, evidenceDate);
            evidenceRepository.save(userEvidence);
            //Loop through all associated users again so that we can associate them to the evidence we created
            for(String[] associated: validUsers) {
                EvidenceUser evidenceUser = new EvidenceUser(Integer.parseInt(associated[0]), associated[1], userEvidence);
                evidenceUserRepository.save(evidenceUser);
            }
        }
        return allEvidence;
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
     * @return List of skilltag
     */
    public List<SkillTag> getSkillTagByEvidenceId(int evidenceId) {
        List<EvidenceTag> evidenceTagList = evidenceTagRepository.findAllByParentEvidenceId(evidenceId);
        return evidenceTagList.stream().map(evidenceTag -> evidenceTag.getParentSkillTag()).collect(Collectors.toList());
    }

    /**
     * Takes an evidence ID and returns a list of all links that are associated with it.
     * @param parentEvidenceId The evidence ID to be checked against
     * @return List of links
     */
    public List<WebLink> getLinksByEvidenceId(int parentEvidenceId) {
        List<WebLink> links = webLinkRepository.findByParentEvidence(parentEvidenceId);
        return links;
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
