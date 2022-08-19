package nz.ac.canterbury.seng302.portfolio.model.userGroups;

import nz.ac.canterbury.seng302.portfolio.model.evidence.LinkedCommit;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import javax.persistence.*;
import java.util.List;

/**
 * A code repository (gitlab) associated with a specific group record.
 */
@Entity
@Table(name="group_repo")
public class GroupRepo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "parent_group_id", nullable = false)
    private int parentGroupId;

    @Column(name = "owner", length = 60)
    private String owner = null;
    @Column(name = "name", length = 60)
    private String name = null;
    @Column(name = "alias", length = 60)
    /** The user assigned alias for the stored repository */
    private String alias = null;
    @Column(name = "api_key", length = 60)
    private String apiKey = null;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "parentGroupRepo", cascade = CascadeType.ALL)
    protected List<LinkedCommit> linkedCommit;

    /**
     * Creates a group repository.
     * @param parentGroupId The internal (non-gitlab) group id to register the repo to.
     * @param repoOwner The user or group to which the repo belongs
     * @param repoName The name of the repo, scoped to the user or group
     */
    public GroupRepo(final int parentGroupId, final String repoOwner, final String repoName, final String apiKey) {
        this.owner = repoOwner;
        this.name = repoName;
        this.parentGroupId = parentGroupId;
        this.apiKey = apiKey;
    }

    public GroupRepo() {

    }
    /**
     * Get the parent group to which the repository belongs
     *
     * @return
     */
    public int getParentGroupId() {
        return this.parentGroupId;
    }

    /**
     * Get the repository owner as recorded in Gitlab
     *
     * @return
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Get the repository name as recorded in Gitlab
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the unique integer ID of the record.
     *
     * @return
     */
    public int getId() {
        return this.id;
    }

    /**
     * Get the API key to access the repository
     *
     * @return
     */
    public String getApiKey() {
        return this.apiKey;
    }

    /**
     * Get the user assigned alias for the repo.
     * @return
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * Sets the repository name as recorded in gitlab.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the owner to which the repository belongs.
     *
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Sets the repo API Key used to query gitlab
     * @param apiKey
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Set the user assigned alias for the repository.
     * @param alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }
}


