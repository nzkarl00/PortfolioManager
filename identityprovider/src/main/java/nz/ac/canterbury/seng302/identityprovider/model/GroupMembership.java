package nz.ac.canterbury.seng302.identityprovider.model;

import javax.persistence.*;

/**
 * This class specifies the attributes and methods associated with a group type and an GroupMembership type
 * There is a mapping made to the GroupMembership with a many-to-one relationship and
 *  a many-to-one relationship to AccountProfile
 */
@Entity
@Table(name = "Group_Membership")
public class GroupMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long groupMembershipId;

    @ManyToOne
    @JoinColumn(name="parent_group_id", nullable=false)
    private Groups registeredGroups;

    @ManyToOne
    @JoinColumn(name="parent_account_id", nullable = false)
    private AccountProfile registeredGroupUser;

    public GroupMembership() {};

    /**
     * The main constructor with all the required details to add to the GroupMembership table
     * @param registeredGroups is the registered groups from the groups table
     * @param registeredGroupUser is the users that is registered in a group
     */
    public GroupMembership(Groups registeredGroups, AccountProfile registeredGroupUser) {
        this.registeredGroups = registeredGroups;
        this.registeredGroupUser = registeredGroupUser;

    }

    public Groups getRegisteredGroups(){return registeredGroups;}

    public GroupMembership(AccountProfile registeredUser, Groups parentGroup) {
        this.registeredGroupUser = registeredUser;
        this.registeredGroups = parentGroup;
    }

    public AccountProfile getRegisteredGroupUser() {return registeredGroupUser;}

    public Long getGroupMembershipId() {return groupMembershipId;}

    public void setGroupMembershipId(Long id) {this.groupMembershipId = id;}
}
