package nz.ac.canterbury.seng302.portfolio.model.evidence;

import javax.persistence.*;

@Entity
public class Category {

    public static final int MAX_CATEGORY_NAME_LENGTH = 100;

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
    @Column(name="category_name", length = 30, nullable = false)
    public String categoryName;

    public Category(Evidence parentEvidence, String categoryName) {
        this.parentEvidence = parentEvidence;
        this.categoryName = categoryName;
    }

    protected Category() {}

    /**
     * @return The ID of the category
     */
    public int getId() {return id;}

    /**
     * @return The parent evidence object of the category
     */
    public Evidence getParentEvidence() {return parentEvidence;}

    /**
     * @return The name of the category
     */
    public String getCategoryName() {return categoryName;}
}
