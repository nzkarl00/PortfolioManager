Feature: U32 - Modifying pieces of evidence

  @Close
  Scenario: AC1 - There is an icon (e.g., pencil icon) in the designated icon area (next to the delete icon) that is clickable.
  This icon is only visible to me (the owner).
  This is visible from any of my pages that contain my pieces of evidence.
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I click the Add Evidence button
    When I have filled out all mandatory title, description, and date fields to an evidence
    And I click the save button
    When User navigates to "evidence?pi=1".
    When I view that piece of evidence "Evidence One"
    When I click the edit icon

  @Close
  Scenario: AC7 Others cannot edit my evidence
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I click the Add Evidence button
    When I have filled out all mandatory title, description, and date fields to an evidence
    And I click the save button
    Given I am authenticated as a admin
    When User navigates to "evidence?ui=541".
    When I view that piece of evidence "Evidence One"
    Then I cannot click the edit icon