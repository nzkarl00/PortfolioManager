package nz.ac.canterbury.seng302.portfolio.model.evidence;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class LinkedCommit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", unique = true)
    private int id;

    /**
     * A LinkedCommit can be associated with one or more parent piece of evidence.
     * An evidence can have one or more LinkedCommit
     */
    @ManyToOne
    @JsonBackReference // This prevents infinite reference looping between tables
    @JoinColumn(name="parent_evidence_id", nullable=false)
    protected Evidence parentEvidence;

    /**
     * Source repo name and owner to identify the commit
     */
    @Column(name="repo_name", nullable = false)
    protected String repoName;
    @Column(name="repo_owner", nullable = false)
    protected String repoOwner;

    /**
     * Searchable attributes of a LinkedCommit are its
     * - hash
     * - author
     * - title
     * - timestamp
     */
    @Column(name="commit_hash", nullable = false)
    protected String hash = "";
    @Column(name="author", nullable = false)
    protected String author = "";
    public static final int MAX_TITLE_LENGTH = 80;
    @Column(name="title", nullable = false)
    protected String title = "";
    @Column(name="timestamp", nullable = false)
    protected LocalDateTime timestamp;

    public LinkedCommit() {}

    /**
     * A link commit to a parent evidence
     * @param parentEvidence that this LinkedCommit is associated with
     * @param repoName Repo name used to identify where this LinkedCommit was retrieved from
     * @param repoOwner Repo owner used to identify where this LinkedCommit was retrieved from
     * @param hash of the LinkedCommit
     * @param author of the LinkedCommit
     * @param title of the LinkedCommit that must not exceed MAX_TITLE_LENGTH
     * @param timestamp both date and time of the LinkedCommit
     */
    public LinkedCommit(
        Evidence parentEvidence,
        String repoName,
        String repoOwner,
        String hash,
        String author,
        String title,
        LocalDateTime timestamp
    ) {
        this.parentEvidence = parentEvidence;
        this.repoName = repoName;
        this.repoOwner = repoOwner;
        this.hash = hash;
        this.author = author;
        this.title = title;
        this.timestamp = timestamp;
    }

    /**
     * Validate properties before constructing an invalid LinkedCommit item.
     * @param title of the LinkedCommit
     * @throws IllegalArgumentException If one argument is invalid, throws an exception
     */
    public static void validateProperties(
        String title
    ) throws IllegalArgumentException {
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(String.format("Title length must not exceed %d characters", MAX_TITLE_LENGTH));
        }
    }

    /**
     * Get the ID of the piece of LinkedCommit
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the piece of Evidence to which the LinkedCommit belongs to
     * @return
     */
    public Evidence getParentEvidence() {
        return parentEvidence;
    }

    /**
     * Returns the name of the repo from which the LinkedCommit came from
     * @return
     */
    public String getRepoName() { return repoName; }

    /**
     * Returns the owner of the repo from which the LinkedCommit came from
     * @return
     */
    public String getRepoOwner() { return repoOwner; }

    /**
     * Return the hash of the commit
     * @return
     */
    public String getHash() { return hash; }

    /**
     * Return the author of the commit
     * @return
     */
    public String getAuthor() { return author; }

    /**
     * Return the title of the commit
     * @return
     */
    public String getTitle() { return title; }

    /**
     * Return the timestamp (date and time) of the commit
     * @return
     */
    public LocalDateTime getTimeStamp() { return timestamp; }

    /**
     * Set the evidence that will be linked to this commit
     * @param parentEvidence
     */
    public void setParentEvidence(Evidence parentEvidence) {
        this.parentEvidence = parentEvidence;
    };

    /**
     * Set the name of the repo this commit is retrieved from
     * @param repoName
     */
    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    /**
     * Set the owner of the repo this commit is retrieved from
     * @param repoOwner
     */
    public void setRepoOwner(String repoOwner) {
        this.repoOwner = repoOwner;
    }

    /**
     * Set the hash string of this LinkCommit
     * @param hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * author
     * @param author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Set the title string of this LinkCommit
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the date and time as a timestamp of this LinkCommit
     * @param timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
