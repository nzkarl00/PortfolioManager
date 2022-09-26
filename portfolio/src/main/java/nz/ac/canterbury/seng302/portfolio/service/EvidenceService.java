package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.CustomExceptions;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepo;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepoRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvidenceService {
    @Autowired
    EvidenceRepository evidenceRepository;
    @Autowired
    EvidenceTagRepository evidenceTagRepository;
    @Autowired
    ProjectService projectService;
    @Autowired
    EvidenceUserRepository evidenceUserRepository;
    @Autowired
    WebLinkRepository webLinkRepository;
    @Autowired
    AccountClientService accountClientService;
    @Autowired
    GroupRepoRepository groupRepoRepository;
    @Autowired
    GitlabClient gitlabClient;
    @Autowired
    SkillTagRepository skillTagRepository;

    Logger logger = LoggerFactory.getLogger(EvidenceService.class);

    /**
     * Splits an HTML form input list, into multiple array elements.
     *
     * @param stringFromHTMLWithTilda The string containing items delimited by ~
     * @return an array representation of the list
     */
    public static List<String> extractListFromHTMLStringWithTilda(
            String stringFromHTMLWithTilda) {
        if (stringFromHTMLWithTilda.equals("")) {
            return Collections.emptyList();
        }

        return Arrays.asList(stringFromHTMLWithTilda.split("~"));
    }


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
     * Gets a list of evidence that matches all the provided parameters
     *
     * @param userId       ID of the user that evidence must match
     * @param projectId    ID of the project that must be associated
     * @param categoryName Name of the category that must be associated
     * @param skillName    Name of the skill that must be associated
     * @return List of evidence matching all the given criteria
     * @throws CustomExceptions.ProjectItemNotFoundException If the project with this ID does not exist, throws an exception
     */
    @Transactional
    public List<Evidence> getEvidenceList(Integer userId, Integer projectId,
                                          String categoryName, String skillName)
            throws CustomExceptions.ProjectItemNotFoundException {
        List<Evidence> evidenceList = new ArrayList<>();
        if (projectId >
                0) { // -1 is the ID provided when project is not specified
            Project project = projectService.getProjectById(projectId);
            evidenceList =
                    evidenceRepository.findAllByAssociatedProject(project);

        }
        if (userId != null) {
            if (!evidenceList.isEmpty()) {
                // Intersection of current list and query
                evidenceList = evidenceList.stream()
                        .filter(evidenceRepository.findAllByParentUserId(
                                userId)::contains).toList();
            } else {
                evidenceList = evidenceRepository.findAllByParentUserId(userId);
            }
        }
        if (!Objects.equals(categoryName, "")) {
            if (!evidenceList.isEmpty()) {
                // Intersection of current list and query
                evidenceList = evidenceList.stream()
                        .filter(evidenceRepository.getEvidenceByCategoryInt(
                                Evidence.categoryStringToInt(
                                        categoryName))::contains).toList();
            } else {
                evidenceList = evidenceRepository.getEvidenceByCategoryInt(
                        Evidence.categoryStringToInt(categoryName));
            }
        }
        if (!Objects.equals(skillName, "")) {
            List<EvidenceTag> evidenceTags =
                    evidenceTagRepository.findAllByParentSkillTagId(
                            skillTagRepository.findByTitle(skillName).getId());
            if (!evidenceList.isEmpty()) {
                // Intersection of current list and query
                evidenceList = evidenceList.stream()
                        .filter(evidenceTags.stream()
                                .map(EvidenceTag::getParentEvidence)
                                .toList()::contains).toList();
            } else {
                evidenceList = evidenceTags.stream()
                        .map(EvidenceTag::getParentEvidence).toList();
            }

        }
        evidenceList = new ArrayList<>(
                evidenceList); // Casts list back to mutable form to be sorted
        evidenceList.sort((o1, o2) -> o2.getDate().compareTo(
                o1.getDate())); // Puts evidence in the correct date order
        return evidenceList;
    }

    /**
     * Gets all the evidence associated with a single user.
     *
     * @param userId User to get evidence from
     * @return List of evidence that belongs to the user
     */
    public List<Evidence> getEvidenceForUser(int userId) {
        return evidenceRepository.findAllByParentUserIdOrderByDateDesc(userId);
    }

    @Transactional
    public List<Evidence> filterBySkill(List<Evidence> evidenceList,
                                        String skillName) {
        List<Evidence> filteredEvidence = new ArrayList<>();
        for (Evidence evidence : evidenceList) {
            boolean isValid = false;
            List<EvidenceTag> tagList =
                    evidence.getEvidenceTags();
            for (EvidenceTag tag : tagList) {
                if (tag.getParentSkillTag().getTitle().equals(skillName)) {
                    isValid = true;
                    break;
                }
            }
            if (isValid) {
                filteredEvidence.add(evidence);
            }
        }
        return filteredEvidence;
    }

    public List<Evidence> filterByCategory(List<Evidence> evidenceList,
                                           String categoryName) {
        List<Evidence> filteredEvidence = new ArrayList<>();
        for (Evidence evidence : evidenceList) {
            boolean isValid = false;

            List<String> evidenceCats = evidence.getCategoryStrings();
            for (String catString : evidenceCats) {
                if (catString.equals(categoryName)) {
                    isValid = true;
                    break;
                }
            }

            if (isValid) {
                filteredEvidence.add(evidence);
            }
        }
        return filteredEvidence;
    }

    /**
     * This function loops through the provided evidences from the filtering
     * and retrieves all the skill tags from them to display in the side panel
     *
     * @param evidenceList the list of evidence to find the skill tags from
     * @return the final list of skill tags
     */
    public Set<SkillTag> getFilterSkills(List<Evidence> evidenceList) {
        Set<SkillTag> returning = new HashSet<>();
        for (Evidence evidence : evidenceList) {
            for (EvidenceTag tag : evidence.getEvidenceTags()) {
                if (!tag.getParentSkillTag().getTitle().equals("No_skills")) {
                    returning.add(tag.getParentSkillTag());
                }
            }
        }
        returning.add(skillTagRepository.findByTitle("No_skills"));
        return returning;
    }

    /**
     * Given a piece of evidence that exist already,
     * this function will validate the list of users,
     * so they can be added as contributors to this piece of evidence.
     *
     * @param userStrings A list of users in string formatted in "userId: username" pairs
     * @param evidence    An existing piece of evidence to be modified by adding a list of users to it
     */
    public void addUsersToExistingEvidence(List<String> userStrings,
                                           Evidence evidence) {
        // Validate that the username exists in the IDP
        List<String[]> validUsers = validateUserIdPairExist(userStrings);

        //Loop through all associated users so that we can add them to the existing evidence as contributors
        logger.info("[EVIDENCE SERVICE] adding contributors to the evidence");
        for (String[] validUser : validUsers) {
            int userId = Integer.parseInt(validUser[0]);
            String userName = validUser[1];
            EvidenceUser evidenceUser =
                    new EvidenceUser(userId, userName, evidence);
            evidenceUserRepository.save(evidenceUser);
        }
    }

    /**
     * Given a list of users, validate if their userId: userName pair exist in the IDP already
     *
     * @param userStrings A list of users in string formatted in "userId: username" pairs
     * @return A string list of valid users who exist in the IDP, formatted in "userId: username" pairs
     */
    public List<String[]> validateUserIdPairExist(List<String> userStrings) {

        List<String[]> validUsers = new ArrayList<>();

        logger.info("[EVIDENCE SERVICE] validating users");
        for (String userString : userStrings) {
            String[] userIdPair = userString.split(":");
            int userId = Integer.parseInt(userIdPair[0]);
            String userName = userIdPair[1];

            // Make a call to the IDP and make sure the userId and username pair match with what is expected
            UserResponse response = accountClientService.getUserById(userId);
            if (response.getUsername().equals(userName)) {
                validUsers.add(userIdPair);
            }
        }

        return validUsers;
    }

    /**
     * For every user associated to a piece of evidence, duplicate the evidence and generate a new evidence for them
     * And then creating an association between each evidence user and their parent evidence
     *
     * @param userStrings   List of {id}:{username} that a user has said also worked on the piece of evidence they're creating
     * @param parentProject The parent project that all pieces of evidence will relate to
     * @param title         The title that all pieces of evidence will relate to
     * @param description   The description that all pieces of evidence will relate to
     * @param evidenceDate  The evidenceDate that all pieces of evidence will relate to
     */
    public List<Evidence> generateEvidenceForUsers(List<String> userStrings,
                                                   Project parentProject,
                                                   String title,
                                                   String description,
                                                   LocalDate evidenceDate,
                                                   int categories) {
        // Extract then validate usernames
        // This is assuming that the list is formatted as {id}:{username}
        List<Evidence> allEvidence = new ArrayList<>();

        // Validate that the username exists in the IDP
        List<String[]> validUsers = validateUserIdPairExist(userStrings);

        //Loop through all associated users so we can create their pieces of evidence
        for (String[] user : validUsers) {
            int userId = Integer.parseInt(user[0]);
            Evidence userEvidence =
                    new Evidence(userId, parentProject, title, description,
                            evidenceDate, categories);
            evidenceRepository.save(userEvidence);

            //Loop through all associated users again so that we can associate them to the evidence we created
            // TODO: can refactor this to use to addUsersToExistingEvidence function
            for (String[] associated : validUsers) {
                int associatedId = Integer.parseInt(associated[0]);
                String associatedName = associated[1];
                logger.debug(associatedName);
                EvidenceUser evidenceUser =
                        new EvidenceUser(associatedId, associatedName,
                                userEvidence);
                evidenceUserRepository.save(evidenceUser);
            }
            allEvidence.add(userEvidence);
        }
        return allEvidence;
    }

    /**
     * Takes an evidence ID and returns a list of all skill tag titles that are associated with it.
     *
     * @param evidenceId The evidence ID to be checked against
     * @return List of skill tag title strings
     */
    @Transactional
    public List<String> getSkillTagStringsByEvidenceId(Evidence evidence) {
        List<EvidenceTag> evidenceTagList =
                evidence.getEvidenceTags();
        return evidenceTagList.stream()
                .map(evidenceTag -> evidenceTag.getParentSkillTag().getTitle())
                .collect(Collectors.toList());
    }

    /**
     * This method is called to add skills to the repo. If the skills is already in the repo for skills then use it, if not
     * make a new skill from the given inputs and save it to the repo.
     *
     * @param parentProject The project the evidence is linked with
     * @param evidence      Evidence for the skill
     * @param skills        string of skills to be added to the skills repo
     */
    public void addSkillsToRepo(Project parentProject, Evidence evidence,
                                String skills) {
        //Create new skill for any skill that doesn't exist, create evidence tag for all skills
        if (skills.replace(" ", "").length() > 0) {
            List<String> skillList = extractListFromHTMLStringWithTilda(skills);
            for (String skillString : skillList) {
                String validSkillString = skillString.replace(" ", "_");
                SkillTag skillFromRepo =
                        skillTagRepository.findByTitleIgnoreCase(
                                validSkillString);
                saveSkillsAndEvidenceTags(parentProject, evidence,
                        validSkillString, skillFromRepo);
            }
        }

        // If there's no skills, add the no_skills
        List<EvidenceTag> evidenceTagList =
                evidenceTagRepository.findAllByParentEvidenceId(
                        evidence.getId());
        if (evidenceTagList.isEmpty()) {
            logger.info(
                    "[EVIDENCE_SERVICE] Attempted to create new evidence tag using 'No_Skill' tag and save it to evidenceTagRepository");
            SkillTag noSkillTag = skillTagRepository.findByTitle("No_skills");
            EvidenceTag noSkillEvidence = new EvidenceTag(noSkillTag, evidence);
            evidenceTagRepository.save(noSkillEvidence);
        }

    }

    /**
     * This method is called to check if a input skill tag is in the repo already or not. If it is it will save the
     * skill tag under that or else make a new
     * skill tage to save it.
     *
     * @param parentProject    The project the evidence is linked with
     * @param evidence         Evidence for the skill
     * @param validSkillString input skill to be checked
     * @param skillFromRepo    skill tag from the repo if matches validSkillString (case-insensitive)
     */
    public void saveSkillsAndEvidenceTags(
            Project parentProject,
            Evidence evidence,
            String validSkillString,
            SkillTag skillFromRepo
    ) {
        if (skillFromRepo == null) {
            logger.info(
                    "[EVIDENCE_SERVICE] Attempted to create new skill tag and save it to skillTagRepository");
            SkillTag newSkill = new SkillTag(parentProject, validSkillString);
            skillTagRepository.save(newSkill);
            EvidenceTag noSkillEvidence = new EvidenceTag(newSkill, evidence);
            evidenceTagRepository.save(noSkillEvidence);
        } else {
            logger.info(
                    "[EVIDENCE_SERVICE] Finding all evidence tag using the evidence id, by using evidenceTagRepository");
            List<EvidenceTag> allEvidenceTagsForEvidence =
                    evidenceTagRepository.findAllByParentEvidenceId(
                            evidence.getId());

            // Checks to see if the skill tag isn't already in the piece of evidence.
            boolean isInEvidence = allEvidenceTagsForEvidence.stream()
                    .anyMatch(eachEvidenceTag -> (skillFromRepo.getId() ==
                            eachEvidenceTag.getParentSkillTag().getId()));

            if (!isInEvidence) {
                logger.info(
                        "[EVIDENCE_SERVICE] Attempted to create new evidence tag using existing skill tag and save it to evidenceTagRepository");
                EvidenceTag noSkillEvidence =
                        new EvidenceTag(skillFromRepo, evidence);
                evidenceTagRepository.save(noSkillEvidence);
            }
        }
    }

    /**
     * Splits an HTML form input list, into multiple array elements.
     *
     * @param stringFromHTMLWithSpace The string containing items delimited by a space
     * @return An array of the individual values present in the string
     */
    public List<String> extractListFromHTMLStringWithSpace(
            String stringFromHTMLWithSpace) {
        if (stringFromHTMLWithSpace.equals("")) {
            return new ArrayList<>();
        }

        return Arrays.asList(stringFromHTMLWithSpace.split(" "));
    }

    /**
     * Validate web link strings
     *
     * @param links Web A list of web links to be checked
     * @return an error message, if something is wrong
     */
    public Optional<String> validateLinks(List<String> links) {
        for (String link : links) {
            if (!WebLink.urlHasProtocol(link)) {
                logger.trace(
                        "[WEBLINK] Rejecting web link as the link is not valid, link: " +
                                link);
                return Optional.of(
                        "The provided link is not valid, must contain http(s):// protocol: " +
                                link);
            }
        }
        return Optional.empty();
    }

    /**
     * Construct web links, must be validated first.
     *
     * @param links          The link of links which are associated with a given piece of evidence
     * @param parentEvidence The evidence object which the weblink belongs to
     * @return An array of weblink objects which contain both the link text and the parent evidence
     */
    public List<WebLink> constructLinks(List<String> links,
                                        Evidence parentEvidence)
            throws MalformedURLException {
        ArrayList<WebLink> resultLinks = new ArrayList<>();
        // Validate all links
        for (String link : links) {
            // Web links are valid, so construct them all
            resultLinks.add(new WebLink(link, parentEvidence));
        }
        return resultLinks;
    }

    /**
     * Builds commit to save from a commit hash with group ID.
     * @param hashAndGroupStrings An array of strings containing all commit hashes and group IDs separated by a "+"
     * @param parentEvidence Evidence to connect to the newly constructed commit
     * @exception GitLabApiException can be thrown if there is an error fetching the commit from GitLabApi
     */
    public List<LinkedCommit> constructCommits(List<String> hashAndGroupStrings, Evidence parentEvidence) throws GitLabApiException {
        List<LinkedCommit> constructedCommits = new ArrayList<>();
        for (String hashAndGroupString: hashAndGroupStrings) {
            List<String> hashAndGroup = Arrays.asList(hashAndGroupString.split("\\+"));
            Optional<GroupRepo> optionalGroupRepo = groupRepoRepository.findByParentGroupId(Integer.parseInt(hashAndGroup.get(1)));
            GroupRepo groupRepo;
            if (optionalGroupRepo.isEmpty()) {
                logger.warn("No repository found for the group id " + hashAndGroup.get(1));
            } else {
                groupRepo = optionalGroupRepo.get();
                Commit foundCommit = gitlabClient.getSingleCommit(hashAndGroup.get(0), groupRepo);
                logger.info(foundCommit.toString());
                constructedCommits.add(new LinkedCommit(parentEvidence, groupRepo.getName(), groupRepo.getOwner(), hashAndGroup.get(0),
                        foundCommit.getAuthorName(), foundCommit.getTitle(), DateParser.convertToLocalDateTime(foundCommit.getCreatedAt())));
            }
        }
        return constructedCommits;
    }

    /**
     * Given a list of links, add then all to the existing piece of evidence
     *
     * @param links    String list of links to be constructed into WebLink() and added to the evidence
     * @param evidence An existing piece of evidence to be modified by adding a list of links to it
     */
    public void addLinksToEvidence(List<String> links, Evidence evidence)
            throws MalformedURLException {
        Optional<String> possibleError = validateLinks(links);
        if (possibleError.isEmpty()) {
            try {
                logger.info("we are trying to add the links");
                webLinkRepository.saveAll(constructLinks(links, evidence));
            } catch (MalformedURLException e) {
                logger.error(
                        "[EVIDENCE] Somehow links were attempted for construction with malformed URL",
                        e);
                logger.error("[EVIDENCE] Links not saved");
            }
        }
    }

    /**
     * Deletes evidence from the repository and removes any orphaned skill tags.
     *
     * @param evidence The evidence to be deleted
     */
    public void deleteEvidence(Evidence evidence) {
        List<EvidenceTag> evidenceTags = evidence.getEvidenceTags();
        List<SkillTag> skillTags = evidenceTags.stream()
            .map(EvidenceTag::getParentSkillTag)
            .filter(skillTag -> !Objects.equals(skillTag.getTitle(), "No_skills"))
            .toList(); // All skill tags associated with deleted evidence

        evidenceTags.forEach(tag -> evidenceTagRepository.delete(tag));
        evidenceRepository.delete(evidence);
        for (SkillTag skillTag : skillTags) {
            if (evidenceTags.containsAll(skillTag.getEvidenceTags())) {
                // If every evidence tag associated with a skill tag also belongs to deleted evidence
                skillTag.clearEvidenceTags();
                skillTagRepository.delete(skillTag);
            }
        }
    }

    /**
     * Returns a set of every skill that is used in a piece of evidence a given user is the parent of.
     *
     * @param id the id of the user being skill checked
     * @return a set of skill objects
     */
    public List<SkillTag> getUserSkills(Integer id) {
        List<Evidence> evidenceList =
                evidenceRepository.findAllByParentUserIdOrderByDateDesc(id);
        // A hashset of tag IDs that have been found already, to determine if
        // The tag should be added to the tag list.
        HashSet<Integer> tagIDs = new HashSet<>();
        ArrayList<SkillTag> tagList = new ArrayList<>();
        for (Evidence evidence : evidenceList) {
            List<EvidenceTag> tagsForEvidence = evidence.getEvidenceTags();
            for (EvidenceTag evidenceTag : tagsForEvidence) {
                SkillTag skillTag = evidenceTag.getParentSkillTag();
                if (!tagIDs.contains(skillTag.getId())) {
                    tagIDs.add(skillTag.getId());
                    tagList.add(skillTag);
                }
            }
        }

        // Ensure no skills is in the list
        boolean containsNoSkills = (tagList.size() == 0);
        if (containsNoSkills) {
            tagList.add(skillTagRepository.findByTitle("No_skills"));
        }
        tagList.sort(Comparator.comparing(SkillTag::getTitle));
        return tagList;
    }


    /**
     * Handle edits (create, delete, update) made to skill tags.
     * This is within the context of a piece of evidence, as is the case for the controller that needs to
     * apply skill tag edits within the context of editing a piece of evidence.
     * This code is significantly janky, largely due to the weirdness of hibernate/JPA
     * and me trying to not refactor everything.
     *
     */
    public void handleSkillTagEditsForEvidence(final ParsedEditSkills input,
                                               Evidence evidence) {
        // Delete, then add, then rename
        // Delete tags
        logger.debug("Handling skill tag deletions");
        for (Integer skillID : input.skillIDsToDelete) {
            deleteSkillTagFromEvidence(evidence, skillID);
        }
        evidenceRepository.save(evidence);
        for (Integer skillID : input.skillIDsToDelete) {
            deleteOrphanedSkillTag(skillID);
        }

        // Create
        logger.debug("Handling skill tag creations");
        for (String skillStr : input.skillsToAdd) {
            SkillTag skillFromRepo = skillTagRepository.findByTitleIgnoreCase(
                    skillStr
            );
            saveSkillsAndEvidenceTags(evidence.getAssociatedProject(), evidence,
                    skillStr, skillFromRepo);
        }

        // Now manage tag renames.
        logger.debug("Handling skill tag modifications");
        input.skillsToModify.forEach((id, newTitle) -> modifySkillTag(evidence.getId(), id, newTitle));

        // Finally, apply or remove no skills tag.
        optionallyApplyNoSkillsTag(evidence.getId());
    }

    private void modifySkillTag(final Integer evidenceID,
                                final Integer skillTagID,
                                final String newTagTitle) {
        Optional<Evidence> evidenceOption =
                evidenceRepository.findById(evidenceID);
        assert (evidenceOption.isPresent());
        Evidence evidence = evidenceOption.get();
        Optional<SkillTag> skillTagOption =
                skillTagRepository.findById(skillTagID);
        assert (skillTagOption.isPresent());
        SkillTag skillTag = skillTagOption.get();

        // If the skill tag is no skills, do nothing
        if (Objects.equals(skillTag.getTitle(), "No_skills")) {
            return;
        }

        // For each evidence tag, find the owner of the related piece of evidence
        // Count the number of owners.
        long numberOfParentUsers = skillTag.getEvidenceTags()
                .stream()
                .map((EvidenceTag tag) -> tag.getParentEvidence()
                        .getParentUserId())
                .distinct()
                .count();

        // Two cases, one there is only one owner, in which case simply rename
        // The skill. In the other case, there are multiple owners, in which case
        // We must delete the tags for the editing owner and then make new ones with a
        // new skill tag.
        if (numberOfParentUsers < 2) {
            // Simply rename the skill tag and save it
            skillTag.setTitle(newTagTitle);
            skillTagRepository.save(skillTag);
        } else {
            // Otherwise we need to do the more complicated process
            // Find all evidence belonging to the owner.
            List<Evidence> evidenceForUser =
                    evidenceRepository.findAllByParentUserId(
                            evidence.getParentUserId());

            evidenceForUser.stream().collect(Collectors.groupingBy(
                    Evidence::getAssociatedProject)
            ).forEach((project, evidenceInProject) -> {
                logger.info(String.format(
                        "Creating new skill tag for Project=<%d>, contains %d piece of evidence",
                        project.getId(), evidenceInProject.size()
                ));
                // try to get an existing skill tag with the corresponding title/project
                Optional<SkillTag> trySkillTag =
                        skillTagRepository.findByTitleAndParentProject(
                                newTagTitle, project);
                SkillTag newSkillTag;
                // set newSkillTag to the existing skill tag or create a new one
                if (trySkillTag.isEmpty()) {
                    newSkillTag = new SkillTag(project, newTagTitle);
                    skillTagRepository.save(newSkillTag);
                } else {
                    newSkillTag = trySkillTag.get();
                }

                // Now for each piece of evidence, remove the existing evidence tag
                // And link to the new tag.
                evidenceInProject.forEach((Evidence evidenceItem) -> {
                    // Delete current tag linking to old skill tag
                    // There won't always be a link between evidence and skill
                    // Only delete existing tags
                    Optional<EvidenceTag> existingTag =
                            evidenceTagRepository.findByParentEvidenceAndParentSkillTag(
                                    evidenceItem, skillTag
                            );
                    if (existingTag.isPresent()) {
                        evidenceTagRepository.delete(existingTag.get());

                        // Create new evidence tag
                        EvidenceTag newEvidenceTag =
                                new EvidenceTag(newSkillTag, evidenceItem);
                        evidenceTagRepository.save(newEvidenceTag);
                        evidence.removeEvidenceTag(existingTag.get());
                        evidence.addEvidenceTag(newEvidenceTag);
                        evidenceRepository.save(evidenceItem);
                    }
                });
            });
        }
    }

    /**
     * Removes the evidenece tag linking a skill tag to the piece of evidence.
     */
    private void deleteSkillTagFromEvidence(final Evidence evidence,
                                            final Integer skillID) {
        EvidenceTag evidenceTag =
                evidenceTagRepository.findByParentEvidenceIdAndParentSkillTagId(
                        evidence.getId(), skillID);
        evidence.removeEvidenceTag(evidenceTag);
    }

    /**
     * Checks if a skill tag is 'orphaned'
     * ie. no longer in use by any EvidenceTag and if so, deletes it.
     */
    private void deleteOrphanedSkillTag(final Integer skillID) {
        // Now read the skill tag and see how many evidence tags it has
        // If 0, delete it.
        Optional<SkillTag> skillTag = skillTagRepository.findById(skillID);
        if (skillTag.isPresent()) {
            if (skillTag.get().getEvidenceTags().size() == 0) {
                skillTagRepository.delete(skillTag.get());
            }
        }
    }

    /**
     * Given a piece of evidence, adds or removes the no_skills tag appropriately
     * depending on whether any other tags are set.
     */
    private void optionallyApplyNoSkillsTag(final Integer evidenceID) {
        Optional<Evidence> evidenceOption = evidenceRepository.findById(
                evidenceID
        );
        assert (evidenceOption.isPresent());
        Evidence evidence = evidenceOption.get();

        SkillTag noSkillTag = skillTagRepository.findByTitle("No_skills");
        List<EvidenceTag> allTags = evidenceTagRepository.findAllByParentEvidenceId(evidenceID);
        // Determine if the evidence has a tag of no_skills
        boolean evidenceContainsNoSkillTag = allTags
                .stream()
                .anyMatch(
                        (EvidenceTag tag) -> tag.getParentSkillTag().getId() ==
                                noSkillTag.getId());

        if (evidence.getEvidenceTags().size() == 0) {
            EvidenceTag noSkillEvidence = new EvidenceTag(noSkillTag, evidence);
            evidenceTagRepository.save(noSkillEvidence);
        } else if (allTags.size() > 1 &&
                evidenceContainsNoSkillTag) {
            // Otherwise if the only tag is not skill tag, remove skill tag
            // Ensure skill tag is not applied
            EvidenceTag noSkillEvidenceTag =
                    evidenceTagRepository.findByParentEvidenceIdAndParentSkillTagId(
                            evidenceID,
                            noSkillTag.getId()
                    );
            logger.info("WE ARE DELETING");
            evidenceTagRepository.delete(noSkillEvidenceTag);
            evidence.removeEvidenceTag(noSkillEvidenceTag);
            evidenceRepository.save(evidence);
        }
    }

    /**
     * A local class to contain the parsed information about skill edits to make.
     * Maybe be used when editing evidence.
     */
    public static class ParsedEditSkills {
        public List<Integer> skillIDsToDelete;
        public HashMap<Integer, String> skillsToModify;
        public List<String> skillsToAdd;

        public ParsedEditSkills(List<String> add, List<Integer> delete,
                                HashMap<Integer, String> modify) {
            skillIDsToDelete = delete;
            skillsToModify = modify;
            skillsToAdd = add;
        }
    }
}
