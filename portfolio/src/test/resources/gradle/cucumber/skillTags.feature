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
    And User inputs "skill" into the skill input textbox.
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
    And User inputs "skill fails because of ()*&^*&^$(*^(&^)" into the skill input textbox.
    Then There will not be a skill displayed.
    And An appropriate error message will be shown.

  @Close
  Scenario: AC7 Clicking on a tag takes me to a page with that tag as a heading
  and all pieces of evidence that contain this tag displayed in reverse chronological order.
  when the tag is converted to a heading, the letter’s case is maintained
  but all underscores are converted to spaces, e.g.
  the tag “Intra-team_communication” becomes “Intra-team communication” in the heading.
    Given User is logged in.
    And There is evidence in the table
    When User navigates to "evidence".
    And User selects the Quantitative skills option in the category dropdown
    And User clicks search button
    Then user is directed to a page where the heading is "Evidence from category: Quantitative Skills"

  @Close
  Scenario: AC7 Clicking on a tag takes me to a page with that tag as a heading
  and all pieces of evidence that contain this tag displayed in reverse chronological order.
  when the tag is converted to a heading, the letter’s case is maintained
  but all underscores are converted to spaces, e.g.
  the tag “Intra-team_communication” becomes “Intra-team communication” in the heading.
    Given User is logged in.
    Given There is evidence in the table
    When User navigates to "evidence".
    And User selects the "skill" option in the skills dropdown
    And User clicks search button
    Then user is directed to a page where the heading is "Evidence from skill tag: skill"

  @Close
  Scenario Outline: AC1: When creating a piece of evidence,
  I can add one or more skills by simply typing a tag (words with no spaces)
  into a textbox specifically for this purpose.
  Tags can contain both upper- and lower-case characters, numbers, hyphens, and underscores.
  You may allow as many characters as you deem might be necessary
  for a user to successfully use the Skills tag in languages (including English) without compromising the security of the system.
    Given User is logged in.
    When User navigates to "evidence?pi=1".
    And I click the Add Evidence button
    And User inputs <skill> into the skill input textbox.
    Then The skill <skill> will be displayed.

    Examples:
      | skill      |
      | "عَرَبِيّ"     |
      | "Россия"   |
      | "中文"      |
      | "château"  |

  @Close
  Scenario: AC8 All the tags that are currently on any of my pieces of evidence
  are displayed in a panel on the side of my pages.
  There should never be any duplicates of tags.
  There should also be a way of getting to a list of evidences that have no tag
  (e.g., a special/system tag called “No_skills”); clicking on this tag takes me
  to a page with pieces of evidence that have no tags on them.
  If a special/system tag is used, the user should not be able to create this tag separately.
    Given User is logged in.
    When User navigates to "evidence".
    And I click the Add Evidence button
    And I have filled out all mandatory title, description, and date fields to an evidence
    And User inputs "Programming" into the skill input textbox.
    And I click the save button
    And I click the Add Evidence button
    And I have filled out all mandatory title, description, and date fields to an evidence
    And User inputs "Programming" into the skill input textbox.
    And I click the save button
    Then The "Programming" skill only appears once

  # Navigating to No_skills
    Given User is logged in.
    And There is evidence in the table
    When User navigates to "evidence".
    And User selects the "skill" option in the skills side menu
    Then user is directed to a page where the heading is "Evidence from skill tag: skill"

    Given User is logged in.
    And There is evidence in the table
    When User navigates to "evidence".
    And User selects the "No_skills" option in the skills side menu
    Then user is directed to a page where the heading is "Evidence from skill tag: No_skills"

  # Unable to make the tag No_skills
    Given User is logged in.
    And User navigates to "evidence".
    And I click the Add Evidence button
    When User inputs "No_skills" into the skill input textbox.
    Then There will not be a skill displayed.
    And An appropriate error message will be shown.