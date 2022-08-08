Feature: U27: Skills, Adding to evidence

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
    And The window is closed.

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
    And The window is closed.