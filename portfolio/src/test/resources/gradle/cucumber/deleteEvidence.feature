Feature: U12: deleting your own evidence

  @Admin
  @Close
  Scenario: AC1: Clickable Icon on My Own Evidence
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I have filled out all mandatory title, description, and date fields to an evidence
    And I click the save button
    Then I will see a message that this evidence has saved successfully
    When I go to the evidence page
    And I view that piece of evidence "Evidence One"
    Then I can see a delete icon

  @Admin
  @Close
  Scenario: AC1: Clickable Icon on My Own Evidence
    When I go to the evidence page
    And I view that piece of evidence "Evidence One"
    Then I can see a delete icon
    Then I can click the delete Icon


  @Admin
  @Close
  Scenario: AC2: Clicking Icon shows a prompt
    When I go to the evidence page
    And I view that piece of evidence "Evidence One"
    Then I can see a delete icon
    And I can click the delete Icon
    Then A model appears containing the evidence title


  @Admin
  @Close
  Scenario: AC3: I can accept the delete prompt to delete or cancel
    When I go to the evidence page
    And I view that piece of evidence "Evidence One"
    Then I can see a delete icon
    And I can click the delete Icon
    Then A model appears containing the evidence title
    When I click cancel
    Then I view that piece of evidence "Evidence One"

  @Admin
  @Close
  Scenario: AC3: I can accept the delete prompt to delete or cancel
    When I go to the evidence page
    And I view that piece of evidence "Evidence One"
    Then I can see a delete icon
    And I can click the delete Icon
    Then A model appears containing the evidence title
    When I click Delete
    Then I cannot view that piece of evidence "Evidence One"
