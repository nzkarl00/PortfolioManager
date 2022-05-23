package nz.ac.canterbury.seng302.identityprovider.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

/**
 * This class specifies the attributes and methods associated with a group type
 * There is a mapping made to the GroupMembership with a one-to-many relationship
 */
@Entity
@Table(name = "Groups")
public class Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long groupId;
    @Column(name = "group_name", length = 50)
    private String groupName;

    //https://stackoverflow.com/questions/25996758/difference-between-lazycollectionlazycollectionoption-false-and-onetomanyfe
    // The fundamental difference between the annotations is that @OneToMany and its parameters (e.g. fetch = FetchType.EAGER) is a pure JPA.
    // It can be used with any JPA provider, such as Hibernate or EclipseLink.
    //@LazyCollection on the other hand, is Hibernate specific, and obviously works only if Hibernate is used.
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany (mappedBy = "registeredGroups", cascade = CascadeType.ALL)
    protected List<GroupMembership> members;

    public Groups() {}

    /**
     * The main constructor with all the required details
     * @param groupName the assigned group name
     */
    public Groups(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName(){ return groupName; }

}
