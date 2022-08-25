Feature: U12: deleting your own evidence

  @Close
  Scenario: AC1: Clickable Icon on My Own Evidence
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I click the Add Evidence button
    When I have filled out all mandatory title, description, and date fields to an evidence
    And I click the save button
    And I go to the evidence page with a project id
    When I view that piece of evidence
    Then I can see a delete icon

  @Close
  Scenario: AC1: Clickable Icon on My Own Evidence
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I view that piece of evidence
    Then I can see a delete icon
    Then I can click the delete Icon

  @Close
  Scenario: AC1: Clickable Icon on My Own Evidence
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    When I view that piece of evidence that is not mine
    Then I cannot see a delete icon

  @Close
  Scenario: AC2: Clicking Icon shows a prompt
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I view that piece of evidence
    Then I can see a delete icon
    And I can click the delete Icon
    Then A model appears containing the evidence title

  @Close
  Scenario: AC3: I can accept the delete prompt to delete or cancel
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I view that piece of evidence
    Then I can see a delete icon
    And I can click the delete Icon
    Then A model appears containing the evidence title
    When I click cancel
    Then I view that piece of evidence

