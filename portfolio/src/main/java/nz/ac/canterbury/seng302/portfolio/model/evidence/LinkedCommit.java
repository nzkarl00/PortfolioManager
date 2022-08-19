package nz.ac.canterbury.seng302.portfolio.model.evidence;

import com.fasterxml.jackson.annotation.JsonBackReference;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepo;

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
     * Source repo column
     */
    @ManyToOne
    @JoinColumn(name="group_repo", nullable = false)
    protected GroupRepo parentGroupRepo;

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
    @Column(name="title", length = MAX_TITLE_LENGTH, nullable = false)
    protected String title = "";
    @Column(name="timestamp", nullable = false)
    protected LocalDateTime timestamp;

    public LinkedCommit() {}

    /**
     * A link commit to a parent evidence
     * @param parentEvidence that this LinkedCommit is associated with
     * @param parentGroupRepo that this LinkedCommit was retrieved from
     * @param hash of the LinkedCommit
     * @param author of the LinkedCommit
     * @param title of the LinkedCommit that must not exceed MAX_TITLE_LENGTH
     * @param timestamp both date and time of the LinkedCommit
     */
    public LinkedCommit(
        Evidence parentEvidence,
        GroupRepo parentGroupRepo,
        String hash,
        String author,
        String title,
        LocalDateTime timestamp
    ) {
        this.parentEvidence = parentEvidence;
        this.parentGroupRepo = parentGroupRepo;
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
     * Returns the GroupRepo from which the LinkedCommit came from
     * @return
     */
    public GroupRepo getParentGroupRepo() {
        return parentGroupRepo;
    }

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
     * Set the group repo this commit is retrieved from
     * @param parentGroupRepo
     */
    public void setParentGroupRepo(GroupRepo parentGroupRepo) {
        this.parentGroupRepo = parentGroupRepo;
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
