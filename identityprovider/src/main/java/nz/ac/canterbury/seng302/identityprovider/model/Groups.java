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
    private int groupId;
    @Column(name = "group_long_name", length = 50)
    private String groupLongName;
    @Column(name = "group_short_name", length = 10)
    private String groupShortName;

    //https://stackoverflow.com/questions/25996758/difference-between-lazycollectionlazycollectionoption-false-and-onetomanyfe
    // The fundamental difference between the annotations is that @OneToMany and its parameters (e.g. fetch = FetchType.EAGER) is a pure JPA.
    // It can be used with any JPA provider, such as Hibernate or EclipseLink.
    //@LazyCollection on the other hand, is Hibernate specific, and obviously works only if Hibernate is used.
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany (mappedBy = "registeredGroups", cascade = CascadeType.ALL)
    protected List<GroupMembership> members;

    public Groups() {}


    public Groups(int id) {
        this.groupId = id;
    }

    /**
     * The main constructor with all the required details
     * @param groupShortName the assigned group name with a max length of 10
     * @param groupLongName the assigned group name with a max length of 50
     */
    public Groups(String groupLongName, String groupShortName) {
        this.groupLongName = groupLongName;
        this.groupShortName = groupShortName;
    }

    public String groupShortName(){ return groupShortName; }
    public String groupLongName(){ return groupLongName; }
    public int getId(){return groupId;}

    public List<GroupMembership> getMembers() {return members;}

}
