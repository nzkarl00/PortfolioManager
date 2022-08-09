Feature: U27: Skills, Adding to evidence

  Scenario: AC1: When creating a piece of evidence,
  I can add one or more skills by simply typing a tag (words with no spaces)
  into a textbox specifically for this purpose.
  Tags can contain both upper- and lower-case characters, numbers, hyphens, and underscores.
  You may allow as many characters as you deem might be necessary
  for a user to successfully use the Skills tag in languages (including English) without compromising the security of the system.
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I click the Add Evidence button
    When User inputs "skill" into the skill input textbox.
    Then There will be a skill displayed.
    And The window is closed.

  Scenario: AC1: When creating a piece of evidence,
  I can add one or more skills by simply typing a tag (words with no spaces)
  into a textbox specifically for this purpose.
  Tags can contain both upper- and lower-case characters, numbers, hyphens, and underscores.
  You may allow as many characters as you deem might be necessary
  for a user to successfully use the Skills tag in languages (including English) without compromising the security of the system.
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I click the Add Evidence button
    When User inputs "skill fails because of spaces" into the skill input textbox.
    Then There will not be a skill displayed.
    And An appropriate error message will be shown.
    And The window is closed.
