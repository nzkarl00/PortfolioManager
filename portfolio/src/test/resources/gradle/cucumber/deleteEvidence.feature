Feature: U12: deleting your own evidence

    @Admin
    @Close
    Scenario: AC1: Clickable Icon on My Own Evidence
        And I go to the evidence page with a project id
        And I click the Add Evidence button
        When I have filled out all mandatory title "Evidence Delete", description "Deleting evidence work. Frontend", and date fields to an evidence
        And I click the save button
        Then I will see a message that this evidence has saved successfully
        When I go to the evidence page
        And I view that piece of evidence "Evidence Delete"
        Then I can click the delete Icon for "Evidence Delete"

    @Admin
    @Close
    Scenario: AC1: Clickable Icon on My Own Evidence
        When I go to the evidence page
        And I view that piece of evidence "Evidence Delete"
        Then I can see a delete icon for "Evidence Delete"
        Then I can click the delete Icon for "Evidence Delete"


    @Admin
    @Close
    Scenario: AC2: Clicking Icon shows a prompt
        When I go to the evidence page
        And I view that piece of evidence "Evidence Delete"
        Then I can see a delete icon for "Evidence Delete"
        Then I can click the delete Icon for "Evidence Delete"
        Then A model appears containing the evidence title "Evidence Delete"


    @Admin
    @Close
    Scenario: AC3: I can accept the delete prompt to delete or cancel
        When I go to the evidence page
        And I view that piece of evidence "Evidence Delete"
        Then I can see a delete icon for "Evidence Delete"
        Then I can click the delete Icon for "Evidence Delete"
        Then A model appears containing the evidence title "Evidence Delete"
        When I click cancel
        And I go to the evidence page
        Then I view that piece of evidence "Evidence Delete"

    @Admin
    @Close
    Scenario: AC3: I can accept the delete prompt to delete or cancel
        When I go to the evidence page
        And I view that piece of evidence "Evidence Delete"
        Then I can see a delete icon for "Evidence Delete"
        Then I can click the delete Icon for "Evidence Delete"
        Then A model appears containing the evidence title "Evidence Delete"
        When I click Delete
        Then I cannot view that piece of evidence "Evidence Delete"
