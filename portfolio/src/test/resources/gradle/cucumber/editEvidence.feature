Feature: U32 - Modifying pieces of evidence

  Scenario: AC1 - There is an icon (e.g., pencil icon) in the designated icon area (next to the delete icon) that is clickable.
  This icon is only visible to me (the owner).
  This is visible from any of my pages that contain my pieces of evidence.
    Given I am logged in
    When I navigate to a piece of evidence that I don't own
    When I click the edit icon

  Scenario: AC7 Others cannot edit my evidence
    Given I am logged in
    When I navigate to a piece of evidence that I don't own
    Then I cannot click the edit icon