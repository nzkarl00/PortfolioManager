Feature: U12: deleting your own evidence

  @Close
  Scenario: AC1: Clickable Icon on My Own Evidence

    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I click the Add Evidence button
    And I fill out all mandatory fields for delete
    And I click the save button
    When User navigates to "evidence?pi=1".
    When I view that piece of evidence
    Then I can see a delete icon

  @Close
  Scenario: AC1: Clickable Icon on My Own Evidence
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I click the Add Evidence button
    And I fill out all mandatory fields for delete
    And I click the save button
    And User navigates to "evidence?pi=1".
    And I view that piece of evidence
    Then I can see a delete icon
    Then I can click the delete Icon

  @Close
  Scenario: AC2: Clicking Icon shows a prompt
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I view that piece of evidence "Evidence Delete"
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
