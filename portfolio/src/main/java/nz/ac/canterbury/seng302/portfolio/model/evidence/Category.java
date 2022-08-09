package nz.ac.canterbury.seng302.portfolio.model.evidence;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Entity
public class Category {

    public static final int MAX_CATEGORY_NAME_LENGTH = 100;
    public static final List<String> validCategoryNames = List.of("Qualitative Skills", "Quantitative Skills", "Service");

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

    public static void validateCategoryName(String name) throws IllegalArgumentException {
        if (!validCategoryNames.contains(name)) {
            throw new IllegalArgumentException("Category name is not valid, must be one of " + validCategoryNames.toString());
        }
    }
}
