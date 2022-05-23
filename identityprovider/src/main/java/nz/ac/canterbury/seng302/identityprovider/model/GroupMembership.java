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

}
