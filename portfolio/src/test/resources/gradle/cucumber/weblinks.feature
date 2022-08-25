Feature: U26: Web link in evidence

  @Close
  Scenario: AC3 Saving the piece of evidence works as expected.
  The web address is turned into a link.
  Clicking on this link opens that address in a new page/tab.
    Given User is logged in.
    Given There is evidence in the table
    When User navigates to "evidence?pi=1".
    And I view that piece of evidence "Test Evidence"
    And I click the weblink
    Then I am taken to wikipedia in a new tab

  @Close
  Scenario: AC4 When viewing the link on any page (once it is saved),
  the “http://” or  “https://” should not be displayed to make the link look cleaner.
  Instead, a small open/closed padlock should be displayed (similar to browsers).
  The padlocks can be optionally appropriately coloured to distinguish between them.
    Given User is logged in.
    Given There is evidence in the table
    When User navigates to "evidence?pi=1".
    And I view that piece of evidence "Test Evidence"
    Then Wikipedia link has a closed padlock
    And Fake Cern link has a open padlock