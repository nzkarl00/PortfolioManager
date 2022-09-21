package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepo;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * A client used to communicate with the Gitlab API.
 * Allows fetching of basic repository information given an API key.
 * Wraps Gitlab4J, see https://github.com/gitlab4j/gitlab4j-api#using-gitlab4j-api for info
 * providing a subset of functionality that is required for the application.
 */
@Service
public class GitlabClient {
    @Value("${portfolio.gitlab-instance-url}")
    private String gitlabInstanceURL;
    // The maximum number of commits to display per page.
    private static int MAX_COMMITS_PER_PAGE = 1000;

    // Levenshtein Distance used to determine fuzzy matches.
    // This limits the maximum character difference to 5, essentially makes the algo run in bounded time.
    private static int LEVENSHTEIN_DISTANCE_THRESHOLD = 5;
    private static LevenshteinDistance levenshteinDistance = new LevenshteinDistance(LEVENSHTEIN_DISTANCE_THRESHOLD);

    Logger logger = LoggerFactory.getLogger(GitlabClient.class);

    /**
     * Generate repository name string from repo owner and name
     * @param repoOwner
     * @param repoName
     * @return
     */
    static protected String genRepoName(String repoOwner, String repoName) {
        assert(repoOwner != null);
        assert(repoName != null);
        return String.format("%s/%s", repoOwner, repoName);
    }

    /**
     * Fetch a gitlab project
     * @param apiKey The Gitlab Instance API key
     * @param repoOwner
     * @param repoName
     * @return
     * @throws GitLabApiException
     */
    public Project getProject(String apiKey, String repoOwner, String repoName) throws GitLabApiException {
        logger.debug(String.format("Fetching gitlab project for repo"));
        try (GitLabApi api = new GitLabApi(gitlabInstanceURL, apiKey)) {
            return api
                    .getProjectApi()
                    .getProject(String.format("%s/%s", repoOwner, repoName), true);
        }
    }

    /**
     * Determine the number of commits to fetch per page.
     * @param lastN
     * @return
     */
    private int commitsPerPage(int lastN) {
        assert(lastN >= -1);
        return lastN == -1 || lastN > MAX_COMMITS_PER_PAGE ? MAX_COMMITS_PER_PAGE : lastN;
    }


    /**
     * Get the last N commits for a project
     * @param api the Gitlab API
     * @param repoOwner
     * @param repoName
     * @param lastN the number of last commits to get
     * @throws GitLabApiException
     */
    public List<Commit> getCommits(
        GitLabApi api,
        String repoOwner,
        String repoName,
        int lastN
    ) throws GitLabApiException {
        logger.debug(String.format("Fetching last %d gitlab commits for repo: %s/%s", lastN, repoOwner, repoName));

        Pager<Commit> commitPager = api.getCommitsApi().getCommits(
            genRepoName(
                repoOwner,
                repoName
            ),
            commitsPerPage(lastN)
        );
        List<Commit> commits = commitPager.current();

        logger.debug(String.format(
            "Retrieved commits, showing last %d.",
            lastN
        ));
        return commits;
    }

    /**
     * Gets a single commit from the GitLab API
     * @param commitHash hash to find the commit by
     * @param groupRepo repository to search
     * @return commit found in the given repository with the given hash
     * @throws GitLabApiException if there is an error finding the commit
     */
    public Commit getSingleCommit(String commitHash, GroupRepo groupRepo) throws GitLabApiException {
        GitLabApi api = new GitLabApi(gitlabInstanceURL, groupRepo.getApiKey());
        try {
            logger.info("Getting commit with hash: " + commitHash + " from GitLabAPI");
            Commit commit = api.getCommitsApi().getCommit(genRepoName(groupRepo.getOwner(), groupRepo.getName()), commitHash);
            return commit;
        } catch (Exception e) {
            throw new GitLabApiException("Failed to get commit " + commitHash);
        } finally {
            api.close();
        }


    }

    /**
     * Get the last N commits for a project, within a specified date range.
     * Uses date instead of LocalDate as this is what the Gitlab4j api expects.
     * Handle date conversion in higher layers.
     * Ranges are inclusive.
     * @param api the Gitlab API
     * @param repoOwner
     * @param repoName
     * @param lastN the number of last commits to get
     * @param since the start of the date range
     * @param until the end of the date range
     * @throws GitLabApiException
     */
    public List<Commit> getCommitsInDateRange(
            GitLabApi api,
            String repoOwner,
            String repoName,
            int lastN,
            Date since,
            Date until
    ) throws GitLabApiException {
        logger.debug(String.format("" +
                "Fetching last %d gitlab commits for repo: %s/%s, limiting by date range",
                lastN, repoOwner, repoName)
        );
        Pager<Commit> commitPager = api.getCommitsApi().getCommits(
            genRepoName(
                repoOwner,
                repoName
            ),
            "",
            since,
            until,
            commitsPerPage(lastN)
        );
        List<Commit> commits = commitPager.current();
        logger.debug(String.format(
                "Retrieved commits, showing last %d.",
                lastN
        ));
        return commits;
    }

    /**
     * Returns true if the provided commit matches the provided search terms.
     * Most searching is fuzzy, and matches even non-exact matches.
     * @param commit The commit to check
     * @param commitHash The optional commit hash to match against
     * @param authorName The optional author name to match against
     * @param authorEmail The optional author email to match against
     * @return
     */
    private boolean commitMatchesSearch(
            final Commit commit,
            final Optional<String> commitHash,
            final Optional<String> authorName,
            final Optional<String> authorEmail
    ) {
        if (
                authorName.isPresent()
        ) {
            // Note that with levenshteinDistance, -1 means the threshold has been exceeded.
            int nameDistance = levenshteinDistance.apply(commit.getAuthorName(), authorName.get());
            if (nameDistance > 3 || nameDistance == -1) {
                return false;
            }
        }
        if (
                authorEmail.isPresent()
        ) {
            int emailDistance = levenshteinDistance.apply(commit.getAuthorEmail(), authorEmail.get());
            if (emailDistance > 3 || emailDistance == -1) {
                return false;
            }
        }
        if (
                commitHash.isPresent() &&
                        !commit.getId().contains(commitHash.get())
        ) {
            return false;
        }
        return true;
    }

    /**
     * Return a list of commits that matches the provided search terms.
     * See commitMatchesSearch for the searching implementation.
     * @param apiKey The Gitlab API Key to use
     * @param repoOwner The owner account of the repo
     * @param repoName The repository name
     * @param commitHash An optional hash to match against the commit
     * @param authorName The optional name of the commit author, eg. John Doe
     * @param authorEmail The optional email of the commit author, eg. johndoe@example.com
     * @param since An optional date range to start
     * @param until An optional date to end the date range
     * @return A list of Commits that pass the filtered criteria.
     * @throws GitLabApiException
     */
    public List<Commit> getFilteredCommits(
            String apiKey,
            String repoOwner,
            String repoName,
            Optional<String> commitHash,
            Optional<String> authorName,
            Optional<String> authorEmail,
            Optional<Date> since,
            Optional<Date> until
    ) throws GitLabApiException {
        int commitsToFetch = commitsPerPage(1000);
        GitLabApi api = new GitLabApi(gitlabInstanceURL, apiKey);
        // If there is a date range, filter by it.
        // Otherwise, get all.
        List<Commit> commits;
        if (since.isPresent() && until.isPresent()) {
            commits = getCommitsInDateRange(
                    api,
                    repoOwner,
                    repoName,
                    commitsToFetch,
                    since.get(),
                    until.get()
            );
        } else {
            commits = getCommits(
                api,
                repoOwner,
                repoName,
                commitsToFetch
            );
        }

        // Apply the other filters.
        return commits.stream()
                .filter((commit) -> commitMatchesSearch(commit, commitHash, authorName, authorEmail))
                .toList();
    }
}
