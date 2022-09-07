Feature: U33: Adding evidence from repository

  @Close
  Scenario: The idea is that the commits would be in some way related to each other
  (this does not need to be checked by the system). For this story I can search for commits by
  specifying at least one of the criteria.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I open the Add Commit Form
    Then I can't click the add commit button

  @Close
  Scenario: The idea is that the commits would be in some way related to each other
  (this does not need to be checked by the system). For this story I can search for commits by
  specifying at least one of the criteria.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I open the Add Commit Form
    And I type "test" in the username box
    Then I can click the add commit button

  @Close
  Scenario: The idea is that the commits would be in some way related to each other
  (this does not need to be checked by the system). For this story I can search for commits by
  specifying at least one of the criteria.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I open the Add Commit Form
    And I type "test" in the hash box
    Then I can click the add commit button

  @Close
  Scenario: The idea is that the commits would be in some way related to each other
  (this does not need to be checked by the system). For this story I can search for commits by
  specifying at least one of the criteria.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I open the Add Commit Form
    And I type "test" in the username box
    And I type "test" in the hash box
    Then I can click the add commit button

  @Close
  Scenario: The idea is that the commits would be in some way related to each other
  (this does not need to be checked by the system). For this story I can search for commits by
  specifying at least one of the criteria.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I open the Add Commit Form
    And I type "test" in the username box
    And I delete text in the username box
    Then I can't click the add commit button