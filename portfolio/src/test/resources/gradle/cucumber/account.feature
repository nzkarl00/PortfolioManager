Feature: User Profile Page

  @Close
  Scenario: a users profile page allows them to edit parts of thier profile
    Given User is logged in.
    Given There is evidence in the table
    When User navigates to "account".
    Then There are edit buttons for me.

  @Close
  Scenario: a users profile page allows them to edit parts of thier profile
    Given User is logged in.
    Given There is evidence in the table
    When User navigates to "account?id=1".
    Then There are not edit buttons for me.