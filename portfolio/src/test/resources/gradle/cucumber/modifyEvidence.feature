Feature: U32 (U13): Modifying pieces of evidence

    @Close
    Scenario: AC1 There is a clickable edit icon for an evidence I own.
        Given There is an existing evidence that I own
        When I navigate to that evidence
        Then I can click on the edit icon
        And I can see a pencil icon for the edit icon
        And I can see the edit icon is next to the delete icon

    @Close
    Scenario: AC7 I cannot modify others’ pieces of evidence.
        Given There is an existing evidence that I don't own
        When I navigate to that evidence
        Then I will not find an edit icon at all

    @Close
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

    @Close
    Scenario Outline: AC2 The save button is only enabled if the piece of evidence has been modified in some way.
        Given I am on edit mode for an evidence I own
        And I can see this evidence has the <field> filled in
        And I can see the save button is visible, but disabled
        When I have make a modification to the evidence <field>
        Then I can see the save button enabled for me to click on
        Examples:
            | field         |
            | "Title"       |
            | "Date"        |
            | "Category"    |
            | "Description" |
            | "Users"       |
            | "Skills"      |
            | "Links"       |
            | "Commits"     |

    @Close
    Scenario: AC3 When I click on a skill tag, I can edit it.
    This is useful if I find that I made a spelling mistake.
    when this piece of evidence is saved, the tag will be changed globally for me.
    (i.e., the spelling will be corrected on all my pieces of evidence and in associated headings).
        Given I am on edit mode for an evidence I own
        And I can see this evidence has an "original_skill" skill tag
        When I click the skill tag
        And I change the skill to "updated_skill"
        And I save this piece of evidence
        Then I can see the skill tag is updated globally
        And I can see the skill tag is updated on all my piece of evidence
        And I can see the skill tag is updated in the headings


    @Close
    Scenario: AC4 I can remove any of the skill tags.
    This might be by providing an “x” on each tag button or some other means.
    Removing a tag only removes it from this piece of evidence.
    It does not delete the tag globally.
        Given I am on edit mode for an evidence I own
        And I can see this evidence has an "original_skill" skill tag
        When I click the delete button on a skill tag
        And I save this piece of evidence
        Then I can see this skill tag deleted from this piece of evidence
        And This skill tag won't be deleted globally

    @Close
    Scenario: AC4 I can add other skill tags.
        Given I am on edit mode for an evidence I own
        When User inputs "new_skill" into the edit skill input textbox
        And I save this piece of evidence
        Then I can see this "new_skill" skill tag added to this piece of evidence

    @Close
    Scenario Outline: AC5 I can remove existing categories from this piece of evidence, similar to tags.
        Given I am on edit mode for an evidence I own
        And I can see this evidence has a <category>
        When I delete the existing <category>
        And I save this piece of evidence
        Then I can see this skill tag deleted from this piece of evidence
        And This skill tag won't be deleted globally
        Examples:
            | category              |
            | "Quantitative Skills" |
            | "Qualitative Skills"  |
            | "Service"             |

    @Close
    Scenario Outline: AC5 I can add categories to this piece of evidence, similar to tags.
        Given I am on edit mode for an evidence I own
        And I can see this evidence does not have a <category> added already
        When User add a <category> to this evidence
        And I save this piece of evidence
        Then I can see this <category> added to this piece of evidence

        Examples:
            | category              |
            | "Quantitative Skills" |
            | "Qualitative Skills"  |
            | "Service"             |

    Scenario Outline: U13 AC6 Properties that are mandatory remain.
    U7 AC4 - The save button is only enabled once the mandatory fields are filled in.
        Given I am on edit mode for an evidence I own
        When I remove data from the <mandatoryField>
        Then I will see the save button is disabled
        And I will see an error message to explain why I cannot save
        Examples:
            | mandatoryField  |
            | "Title"         |
            | "Date"          |
            | "Description"   |

    Scenario Outline: Usual validation is carried out
    U7 AC 1 - I cannot save unless these are filled in with valid data.
    Invalid data design decision here: https://eng-git.canterbury.ac.nz/seng302-2022/team-700/-/wikis/Design-Decisions
        Given I am on edit mode for an evidence I own
        When I modify the <mandatoryField> with <invalidData>
        And Usual validation fails on the modified <invalidData>
        Then I will see the save button is disabled
        And I will see an error message to explain why I cannot save
        Examples:
            | mandatoryField  | invalidData |
            | "Title"         | "回"         |
            | "Date"          | "ā"         |
            | "Description"   | "1"         |
            | "Title"         | "[*]"       |
            | "Date"          | "1😊😊"      |
            | "Description"   | "1+2=3!"    |

    Scenario Outline: Usual validation is carried out
    U7 AC 1 - I cannot save unless these are filled in with valid data.
    Valid data design decision here: https://eng-git.canterbury.ac.nz/seng302-2022/team-700/-/wikis/Design-Decisions
        Given I am on edit mode for an evidence I own
        When I modify the <mandatoryField> with <validData>
        And Usual validation pass on the modified <validData>
        Then I will see the save button is enabled
        Examples:
            | mandatoryField  | validData   |
            | "Title"         | "😊"         |
            | "Date"          | "Māori"     |
            | "Description"   | "ab"        |
            | "Title"         | "a回"        |
            | "Date"          | "1😊😊char"  |
            | "Description"   | "回回"       |
