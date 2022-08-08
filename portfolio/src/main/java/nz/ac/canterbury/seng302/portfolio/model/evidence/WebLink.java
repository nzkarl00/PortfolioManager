package nz.ac.canterbury.seng302.portfolio.model.evidence;

import javax.persistence.*;

/**
 * The entity representation for web links.
 * As authenticated user can add 1 - 10 web links to a piece of evidence.
 * Stores a URL along with properties gathered (in-memory) about the URL.
 * Like if the URL is secure or whether fetching it results in a 404.
 * The link contains some in memory only details (ie. not stored in database)
 * these details are usually filled by the WebLinkClient.
 * If the link has been trialed, it's fetched property will be true, and it is safe to access
 * the isNotFound and isSecure getters.
 * The intention behind making these properties in-memory only, is that links die all the time
 * And it's pretty cheap to check that we are getting a 404 on them, although maybe latency will become an issue.
 */
@Entity()
public class WebLink {
    public static final int MAX_URL_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * Every web link is associated with a parent piece of evidence.
     */
    @ManyToOne
    @JoinColumn(name="parent_evidence_id", nullable=false)
    protected Evidence parentEvidence;

    /**
     * The URL of the link.
     */
    @Column(name="url", length = MAX_URL_LENGTH, nullable = false)
    public String url;

    /**
     * Whether or not the Link has been fetched with a client, to check if it is secure or not found
     */
    // For those seeing Transient for the first time.
    // It stops JPA persisting this property, which is desired as we should fetch everytime.
    @Transient
    private boolean fetched = false;

    /**
     * Whether or not the Link is a secure one, ie. uses HTTPS
     * Defaults to null, must be fetched first.
     */
    @Transient
    private boolean secure = false;

    /**
     * Wheter or not the Link results in a 404 error, ie. it is not found
     * Defaults to null, must be fetched first.
     */
    @Transient
    private boolean notFound = false;

    /**
     * Crate a Link with the given URL
     * @param url the URL of the link
     */
    public WebLink (String url, Evidence parentEvidence) {
        this.url = url;
        this.parentEvidence = parentEvidence;
    }

    /**
     * Gets the Database ID of the weblink.
     * A unique identifier.
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Whether or not the link has been fetched.
     * The link must be fetched to read the other properties.
     * @return
     */
    public boolean isFetched() {
        return fetched;
    }

    /**
     * Whether or not the fetching of the link indicated that the link was secure, ie. HTTPS
     * Precondition:
     * - Link has been fetched
     * @return true if the link is a secure link
     */
    public boolean isSecure() {
        if (!fetched) {
            throw new IllegalStateException("Link must be fetched first");
        }
        return secure;
    }

    /**
     * Whether or not the fetching of the link indicated that the URL could not be found
     * Precondition:
     * - Link has been fetched
     * @return true if the link results in a 404
     */
    public boolean isNotFound() {
        if (!fetched) {
            throw new IllegalStateException("Link must be fetched first");
        }
        return notFound;
    }

    /**
     * Returns the piece of Evidence to which the link belongs
     * @return
     */
    public Evidence getParentEvidence() {
        return parentEvidence;
    }

    /**
     * Sets the results of fetching the link, and marks the link as fetched
     * @param secure whether the fetch resulted in marking the link as secure
     * @param notFound whether the fetch resulted in a 404
     */
    public void setFetchResult(boolean secure, boolean notFound) {
        this.fetched = true;
        this.secure = secure;
        this.notFound = notFound;
    }

    /**
     * Set that the link is a secure one.
     * @param secure
     */
    public void setSecure(boolean secure) {
        assert(fetched);
        this.secure = secure;
    }

    /**
     * Set that the link results in a 404.
     * @param notFound
     */
    public void setNotFound(boolean notFound) {
        assert(fetched);
        this.notFound = notFound;
    }
}
