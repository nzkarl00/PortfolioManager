Feature: U27: Skills, Adding to evidence

  @Close
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

  @Close
  Scenario: AC1: When creating a piece of evidence,
  I can add one or more skills by simply typing a tag (words with no spaces)
  into a textbox specifically for this purpose.
  Tags can contain both upper- and lower-case characters, numbers, hyphens, and underscores.
  You may allow as many characters as you deem might be necessary
  for a user to successfully use the Skills tag in languages (including English) without compromising the security of the system.
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I click the Add Evidence button
    When User inputs "skill fails because of ()*&^*&^$(*^(&^)" into the skill input textbox.
    Then There will not be a skill displayed.
    And An appropriate error message will be shown.

  @Close
  Scenario: AC7 Clicking on a tag takes me to a page with that tag as a heading
  and all pieces of evidence that contain this tag displayed in reverse chronological order.
  when the tag is converted to a heading, the letter’s case is maintained
  but all underscores are converted to spaces, e.g.
  the tag “Intra-team_communication” becomes “Intra-team communication” in the heading.
    Given User is logged in.
    When User navigates to "evidence".
    When User selects the Quantitative skills option in the category dropdown
    And User clicks search button
    Then user is directed to a page where the heading is "Evidence from category: Quantitative Skills"

  @Close
  Scenario: AC7 Clicking on a tag takes me to a page with that tag as a heading
  and all pieces of evidence that contain this tag displayed in reverse chronological order.
  when the tag is converted to a heading, the letter’s case is maintained
  but all underscores are converted to spaces, e.g.
  the tag “Intra-team_communication” becomes “Intra-team communication” in the heading.
    Given User is logged in.
    When User navigates to "evidence".
    When User selects the "skill" option in the skills dropdown
    And User clicks search button
    Then user is directed to a page where the heading is "Evidence from skill tag: skill"

  @Close
  Scenario: AC8 All the tags that are currently on any of my pieces of evidence
  are displayed in a panel on the side of my pages
    Given User is logged in.
    Given There is evidence in the table
    When User navigates to "evidence".
    When User selects the "skill" option in the skills side menu
    Then user is directed to a page where the heading is "Evidence from skill tag: skill"
