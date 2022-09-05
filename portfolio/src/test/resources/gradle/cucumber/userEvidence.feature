Feature: U11 - Users linked to pieces of evidenceU11 - Users linked to pieces of evidence
  @Close
  Scenario: AC3 When the evidence is saved, the users’ names and mine appear on the piece of evidence.
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And There is evidence in the table
    And I open the piece of evidence
    Then There will be me displayed as a author

  @Close
  Scenario: AC4 Each user’s name is a clickable link that takes me to that user’s profile page.
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And There is evidence in the table
    And I open the piece of evidence
    And I click on the author
    Then I am taken to author's page
