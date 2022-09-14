package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepo;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepoRepository;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.util.Validation;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Commit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.regex.Pattern;

/**
 * A Controller to control searching for commits associated with a group repository.
 */
@Controller
public class CommitSearchController {
    @Value("${portfolio.gitlab-instance-url}")
    private String gitlabInstanceURL;

    @Autowired
    private GroupRepoRepository groupRepoRepository;

    @Autowired
    private GitlabClient gitlabClient;

    Logger logger = LoggerFactory.getLogger(CommitSearchController.class);

    /**
     * Search for commits associated with a group's repository.s
     * @param principal
     * @param groupID
     * @param commitHash
     * @param authorName
     * @param authorEmail
     * @param dateRangeStart
     * @param dateRangeEnd
     * @return
     * @throws Exception
     */
    @GetMapping("evidence/search-commits")
    public String searchCommits(
        @AuthenticationPrincipal AuthState principal,
        @RequestParam(value = "group-id") Integer groupID,
        @RequestParam(value = "commit-hash") Optional<String> commitHash,
        @RequestParam(value = "author-name") Optional<String> authorName,
        @RequestParam(value = "author-email") Optional<String> authorEmail,
        @RequestParam(value = "date-start") Optional<String> dateRangeStart,
        @RequestParam(value = "date-end") Optional<String> dateRangeEnd,
        Model model
    ) throws Exception {
        logger.info(String.format("Attempting to carry out commit search for group id=<%d>", groupID));
        Map<String, Object> res = new HashMap<String, Object>();


        // TODO take this out once front-end validation is complete
        Map<String, Object> test = new HashMap<String, Object>();
        String testApiKey = "naz71Wwxyp31nYzaEgxZ";
        List<Commit> testCommits = gitlabClient.getCommits(new GitLabApi(gitlabInstanceURL, testApiKey), "lra63", "example-for-api", 5);
        for (Commit commit : testCommits) {
            test.put(commit.getTitle(), commit);
        }

        Map<String, Object> output = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : test.entrySet()) {
            if (output.size() < 5) {
                output.put(entry.getKey(), entry.getValue());
            }
        }
        model.addAttribute("commitMap", output);

        // Validation
        try {
            validateDetailsParameters(commitHash, authorName, authorEmail, dateRangeStart, dateRangeEnd);
        } catch (IllegalArgumentException e) {
            logger.info("Parameters passed to searchForCommit are invalid, rejecting: " + e.getMessage());
            model.addAttribute("errorMessage", "Parameters passed to searchForCommit are invalid, rejecting: " + e.getMessage());
            return "fragments/commitDisplay.html :: commitDisplay";
        }

        Optional<GroupRepo> repoOption = groupRepoRepository.findByParentGroupId(groupID);

        // Reject with 404 if there is no group repo with the provided ID.
        if (repoOption.isEmpty()) {
            logger.info("No repository found for the group id=<%d>", groupID);
            model.addAttribute("errorMessage", String.format("No repository is configured for group with ID=%d", groupID));
            return "fragments/commitDisplay.html :: commitDisplay";
        }
        GroupRepo repo = repoOption.get();
        logger.debug(String.format("Retrieved repo entity for group id=<%d>", groupID));

        try {
            List<Commit> commits = gitlabClient.getFilteredCommits(
                    repo.getApiKey(),
                    repo.getOwner(),
                    repo.getName(),
                    commitHash,
                    authorName,
                    authorEmail,
                    dateRangeStart.map((dateStr) -> DateParser.stringToDate(dateStr)),
                    dateRangeEnd.map((dateStr) -> DateParser.stringToDate(dateStr))
            );

            res.put("count", commits.size());
            // Convert each commit into a commit message.
            List<CommitMessage> commitMessages = commits.stream().map((commit) -> new CommitMessage(commit)).toList();

            ArrayList<CommitMessage> sortedCommits = new ArrayList<CommitMessage>(commitMessages);
            //This lambda expression sorts commits in terms of their timestamp
            sortedCommits.sort(
                    //The negative sign before the compareTo function will reverse the sorting order
                    //Because we are comparing dates this will give a reverse chronological ordering
                    (CommitMessage previous, CommitMessage next) -> (-previous.getTimestamp().compareTo(next.getTimestamp()))
            );
            res.put("commits", sortedCommits);
        } catch (Exception e) {
            logger.error(String.format("Could not get commits for group with ID=<%d>", groupID), e);
            model.addAttribute("errorMessage", "Communicating with the Gitlab API failed, please try again");
            return "fragments/commitDisplay.html :: commitDisplay";
        }

        return "fragments/commitDisplay.html :: commitDisplay";
    }

    /**
     * Validates that the parameters to the commit search method are valid.
     * @param commitHash
     * @param authorName
     * @param authorEmail
     * @param dateRangeStart
     * @param dateRangeEnd
     * @throws IllegalArgumentException if any parameters are invalid.
     */
    protected static void validateDetailsParameters(
            Optional<String> commitHash,
            Optional<String> authorName,
            Optional<String> authorEmail,
            Optional<String> dateRangeStart,
            Optional<String> dateRangeEnd
    ) throws IllegalArgumentException {
        if (commitHash.isPresent()) {
            if (commitHash.get().isEmpty()) {
                throw new IllegalArgumentException("Commit hash must not be empty if specified");
            }
            if (commitHash.get().length() > 40) {
                throw new IllegalArgumentException("Commit hash may not be more than 40 characters");
            }
            if (!Pattern.matches("^[A-Fa-f0-9]+$", commitHash.get())) {
                throw new IllegalArgumentException("Commit hash must be a hex string");
            }
        }

        if (authorName.isPresent()) {
            if (authorName.get().isEmpty()) {
                throw new IllegalArgumentException("Author Name must not be empty if specified");
            }
            if (!Pattern.matches("^([A-Za-z 0-9])+$", authorName.get())) {
                throw new IllegalArgumentException("Author name must be comprised of upper and lowercase letters and spaces");
            }
        }

        if (authorEmail.isPresent()) {
            if (authorEmail.get().isEmpty()) {
                throw new IllegalArgumentException("Author Email must not be empty if specified");
            }
            if (!Validation.isValidEmail(authorEmail.get())) {
                throw new IllegalArgumentException("Author email must be a valid email");
            }
        }

        // If dates are defined
        if (dateRangeEnd.isPresent() && dateRangeStart.isPresent()) {
            if (dateRangeEnd.get().isEmpty() || dateRangeStart.get().isEmpty()) {
                throw new IllegalArgumentException("Date Range values must not be empty if specified");
            }
            if (!DateParser.isValidDateString(dateRangeEnd.get())) {
                throw new IllegalArgumentException("Invalid end date format, please use yyyy-MM-dd");
            }

            if (!DateParser.isValidDateString(dateRangeStart.get())) {
                throw new IllegalArgumentException("Invalid start date format, please use yyyy-MM-dd");
            }
        }
    }

    /**
     * A Commit object to return in a JSON response.
     */
    private static class CommitMessage {
        public String authorName;
        public String authorEmail;
        public String hash;
        public String title;
        public String message;
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        public Date timestamp;

        /**
         * Construct a commit message from a Gitlab4J Commit
         * @param commit
         */
        public CommitMessage(Commit commit) {
            timestamp = commit.getCreatedAt();
            hash = commit.getId();
            authorName = commit.getAuthorName();
            authorEmail = commit.getAuthorEmail();
            title = commit.getTitle();
            message = commit.getMessage();
        }

        public String getAuthorName() {
            return authorName;
        }

        public String getAuthorEmail() {
            return authorEmail;
        }

        public String getHash() {
            return hash;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }
}
