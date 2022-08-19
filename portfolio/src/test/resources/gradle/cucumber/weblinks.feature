Feature: U26: Web link in evidence

  Scenario: AC3 Saving the piece of evidence works as expected.
  The web address is turned into a link.
  Clicking on this link opens that address in a new page/tab.
    Given User is logged in.
    Given There is evidence in the table
    When User navigates to "evidence?pi=1".
    And I open the piece of evidence
    And I click the weblink
    Then I am taken to wikipedia in a new tab
    And The window is closed.