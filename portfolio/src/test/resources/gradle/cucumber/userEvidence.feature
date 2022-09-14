Feature: U11 - Users linked to pieces of evidence
  @Close
  Scenario: AC3 When the evidence is saved, the users’ names and mine appear on the piece of evidence.
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And There is evidence in the table
    And I view that piece of evidence
    Then There will be me displayed as a author

  @Close
  Scenario: AC2 To add a user, as I start typing their name
  it attempts to autocomplete the name from the users who are in the list.
  Only users registered in the system can be added.
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I click the Add Evidence button
    And I type "ad" into the user entry
    Then There will be "admin" suggested

  @Close
  Scenario: AC3 When the evidence is saved, the users’ names and mine appear on the piece of evidence.
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And There is evidence in the table that contains the admin too
    And I view that piece of evidence "users"
    Then There will be me displayed as a author
    And There will be admin displayed as a contributor
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
