package nz.ac.canterbury.seng302.portfolio.model.userGroups;

import org.springframework.web.bind.annotation.RequestParam;

public class GroupTemplate {
    public String longName;
    public String shortName;
    public String repoOwner;
    public String repoName;
    public String repoAlias;
    public String apiKey;

    public GroupTemplate() {};

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getRepoOwner() {
        return repoOwner;
    }

    public void setRepoOwner(String repoOwner) {
        this.repoOwner = repoOwner;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoAlias() {
        return repoAlias;
    }

    public void setRepoAlias(String repoAlias) {
        this.repoAlias = repoAlias;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
