Feature: U19: As an authenticated user, I can see a list of users in the system.

  @Close
  Scenario: AC1: Once I am logged in, I can browse to the page containing a list of all users.
    Given User is logged in.
    When User navigates to user table.
    Then User is shown a table containing all users.

  @Close
  Scenario: AC1: If I am not logged in, I cannot browse to the page containing a list of all users.
    Given User is not logged in.
    When User navigates to user table.
    Then User is shown an error page.

  @Close
  Scenario: AC2: The list is displayed in a table format with name, username, alias, and roles, as columns.
  The order of columns should make sense. In future, we will have more columns.
  The default sort is by name, alpha sorted and ascending.
    Given User is logged in.
    When User navigates to user table.
    Then User is shown a table containing all users details in scenario.
    Then The table is sorted by name alphabetically from A - Z.

