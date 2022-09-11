Feature: U33: Adding evidence from repository

  @Close
  Scenario: creating or editing a piece of evidence, I have the option to search my group’s
  repository for commits that fulfil criteria stated above. If a repository is not defined in the group’s
  settings, it should not allow me to even start the process of adding commits
  (e.g., appropriate buttons although visible are disabled). Instead, a message clearly
  informs me of any issue(s) and ideally, how to correct it/them.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I select a group without a repository
    Then I can see a message showing I need to add a valid group

  @Close
  Scenario: creating or editing a piece of evidence, I have the option to search my group’s
  repository for commits that fulfil criteria stated above. If a repository is not defined in the group’s
  settings, it should not allow me to even start the process of adding commits
  (e.g., appropriate buttons although visible are disabled). Instead, a message clearly
  informs me of any issue(s) and ideally, how to correct it/them.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I select a group with a repository
    Then I can see the search form and search button

  @Close
  Scenario: creating or editing a piece of evidence, I have the option to search my group’s
  repository for commits that fulfil criteria stated above. If a repository is not defined in the group’s
  settings, it should not allow me to even start the process of adding commits
  (e.g., appropriate buttons although visible are disabled). Instead, a message clearly
  informs me of any issue(s) and ideally, how to correct it/them.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I select a group with a repository
    And I select the no group option
    Then I can see a message showing I need to add a valid group

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