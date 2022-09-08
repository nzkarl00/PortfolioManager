Feature: U32 (U13): Modifying pieces of evidence

    Scenario: AC1 There is a clickable edit icon for an evidence I own
        Given There is an existing evidence that I own
        When I navigate to that evidence
        Then I can click on the edit icon
        And I can see a pencil icon for the edit icon
        And I can see the edit icon is next to the delete icon

    Scenario: AC1 There is not a clickable edit icon for an evidence I don't own
        Given There is an existing evidence that I don't own
        When I navigate to that evidence
        Then I will not find an edit icon at all

    Scenario: AC2 Clicking on this icon allows me to edit this piece of evidence.
    It should be obvious that I am in edit mode.
    The buttons for save and cancel appear; the save button is initially visible but disabled.
    In the edit mode, the edit icon is not visible.
        Given There is an existing evidence that I own
        When I click on the edit icon
        Then I can obviously see I am in the edit mode
        And I can see the save button is visible, but disabled
        And I can see the cancel button
        And I cannot see the edit icon as it is not visible in edit mode

    Scenario Outline: AC2 The save button is only enabled if the piece of evidence has been modified in some way.
        Given I am on edit mode for an evidence I own
        And I can see the save button is visible, but disabled
        When I have make a modification to any of the evidence <fields>
        Then I can see the save button enabled for me to click on

        Examples:
        | fields        |
        | "Title"       |
        | "Date"        |
        | "Category"    |
        | "Description" |
        | "Users"       |
        | "Skills"      |
        | "Links"       |
        | "Commits"     |

    @Close
    Scenario: AC2 I cannot click save until I have edited an attribute
        Given I am logged in
        And I have a piece of evidenc
        When I navigate to my evidence
        When I click the edit icon
        Then I cannot click the save button
        When I change the title
        Then I can click the save button

    @Close
    Scenario: AC3 When I click on a skill tag, I can edit it.
    This is useful if I find that I made a spelling mistake.
    when this piece of evidence is saved, the tag will be changed globally for me
    (i.e., the spelling will be corrected on all my pieces of evidence and in associated headings).
        Given I am authenticated as a admin
        Given There is evidence in the table
        And I go to the evidence page with a project id
        And I open the piece of evidence
        And I click the edit button
        And I click the "skill" tag
        And I change the skill to "skill1"
        Then The edit list is updated


    @Close
    Scenario: AC4 I can remove any of the skill tags.
    This might be by providing an “x” on each tag button or some other means.
    Removing a tag only removes it from this piece of evidence.
    It does not delete the tag globally. I can also add other skill tags.
        Given I am authenticated as a admin
        Given There is evidence in the table
        And I go to the evidence page with a project id
        And I open the piece of evidence
        And I click the edit button
        And I click the delete "skill" button
        Then The delete list is updated

    @Close
    Scenario: AC4 I can remove any of the skill tags.
    This might be by providing an “x” on each tag button or some other means.
    Removing a tag only removes it from this piece of evidence.
    It does not delete the tag globally. I can also add other skill tags.
        Given I am authenticated as a admin
        Given There is evidence in the table
        And I go to the evidence page with a project id
        And I open the piece of evidence
        And I click the edit button
        When User inputs "new_skill" into the edit skill input textbox.
        Then The new list is updated
