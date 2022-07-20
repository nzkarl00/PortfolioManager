package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.DeadlineRepository;
import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.DeadlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.GitLabApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A client used to communicate with the Gitlab API.
 * Allows fetching of basic repository information given an API key.
 * Wraps Gitlab4J, see https://github.com/gitlab4j/gitlab4j-api#using-gitlab4j-api for info
 * providing a subset of functionality that is required for the application.
 */
public class GitlabClient {
    private String gitlabInstanceURL;

    private final String apiKey;
    private final GitLabApi client;

    Logger logger = LoggerFactory.getLogger(GitlabClient.class);

    public GitlabClient(final String url, final String apiKey) {
        this.gitlabInstanceURL = url;
        this.apiKey = apiKey;
        System.out.println(gitlabInstanceURL);
        this.client = new GitLabApi(url, apiKey);
    }

    public Project getProject(String repoOwner, String repoName) throws GitLabApiException {
        logger.info(String.format("Fetching gitlab project for repo: %s/%s", repoOwner, repoName));
        return client.getProjectApi().getProject(String.format("%s/%s", repoOwner, repoName), true);
    }


}