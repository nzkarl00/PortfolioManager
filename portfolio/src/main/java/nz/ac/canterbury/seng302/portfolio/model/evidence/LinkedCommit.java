package nz.ac.canterbury.seng302.portfolio.model.evidence;

import com.fasterxml.jackson.annotation.JsonBackReference;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDate;

import static nz.ac.canterbury.seng302.portfolio.service.ValidateService.validateEnoughCharacters;

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
    protected LocalDate timestamp;

    public LinkedCommit() {}

    /**
     * A link commit to a parent evidence
     * @param parentEvidence that this LinkedCommit is associated with
     * @param parentGroupRepo that this LinkedCommit was retrieved from
     * @param hash of the LinkedCommit
     * @param author of the LinkedCommit
     * @param title of the LinkedCommit that must not exceed MAX_TITLE_LENGTH
     * @param timestamp of the LinkedCommit
     */
    public LinkedCommit(
        Evidence parentEvidence,
        GroupRepo parentGroupRepo,
        String hash,
        String author,
        String title,
        LocalDate timestamp
    ) {
        this.parentEvidence = parentEvidence;
        this.parentGroupRepo = parentGroupRepo;
        this.hash = hash;
        this.author = author;
        this.title = title;
        this.timestamp = timestamp;
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
        validateEnoughCharacters(title);
    }

}
