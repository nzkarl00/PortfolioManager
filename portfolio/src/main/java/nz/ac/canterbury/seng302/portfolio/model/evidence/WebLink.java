package nz.ac.canterbury.seng302.portfolio.model.evidence;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

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
    private static final Pattern httpsPattern = Pattern.compile("^https://.*$");
    private static final Pattern protocolPattern = Pattern.compile("^https?://.*$");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", unique = true)
    private int id;

    /**
     * Every web link is associated with a parent piece of evidence.
     */
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="parent_evidence_id", nullable=false)
    protected Evidence parentEvidence;

    /**
     * The URL of the link.
     */
    @Column(name="url", length = MAX_URL_LENGTH, nullable = false)
    public URL url;

    /**
     * Whether or not the Link has been fetched with a client, to check if it is secure or not found
     */
    // For those seeing Transient for the first time.
    // It stops JPA persisting this property, which is desired as we should fetch everytime.
    @Transient
    private boolean fetched = true;

    /**
     * Whether or not the Link results in a 404 error, ie. it is not found
     * Defaults to null, must be fetched first.
     */
    @Column(name="not_found")
    private boolean notFound = false;

    /**
     * No Arg constructor for Hibernate
     */
    protected WebLink() {}

    /**
     * Crate a Link with the given URL
     * Procondition:
     * - url must be a valid URL, use urlIsValid to check
     * @param url the URL of the link
     */
    public WebLink (String url, Evidence parentEvidence) throws MalformedURLException {
        urlIsValid(url);
        this.url = new URL(url);
        this.parentEvidence = parentEvidence;
    }

    /**
     * Gets the URL of the WebLink,
     * This URL is clickable, with a protocol, eg. https://www.google.com/
     * @return url the URL of the link
     */
    public String getUrl() {
        return this.url.toString();
    }

    /**
     * Gets a cleaner version of the URL without protocols
     * This has the HTTP:// and HTTPS:// removed so is not clickable
     * eg. www.google.com/
     * @return
     */
    public String getUrlWithoutProtocol() {
        return this.url.toString().substring(this.url.toString().indexOf("://") + 3);
    }

    /**
     * Checks if the supplied URL is valid
     * @param url
     * @return true if valid
     * @throws MalformedURLException if URL is invalid, specifying the reason
     */
    public static boolean urlIsValid(String url) throws MalformedURLException {
        if (!urlHasProtocol(url)) {
            throw new MalformedURLException("URL must contain an HTTP(s) protocol definition");
        }
        try {
            new URL(url);
        } catch(MalformedURLException e) {
            throw new MalformedURLException("URL is not properly formed");
        }
        return true;
    }

    /**
     * True if the URL has http as the protocol
     * @param url
     * @return
     */
    public static boolean urlHasProtocol(String url) {
        return protocolPattern.matcher(url).matches();
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
     * @return true if the link is a secure link
     */
    public boolean isSecure() {
        return url.getProtocol().equals("https");
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
     * Set the ID of the link
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the results of fetching the link, and marks the link as fetched
     * @param notFound whether the fetch resulted in a 404
     */
    public void setFetchResult(boolean notFound) {
        this.notFound = notFound;
        this.fetched = true;
    }

    /**
     * Set that the link results in a 404.
     * @param notFound
     */
    public void setNotFound(boolean notFound) {
        assert(fetched);
        this.notFound = notFound;
    }

    /**
     * Set whether the link has been fetched or not.
     * @param fetched, true if has been fetched
     */
    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }
}
