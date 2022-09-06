Feature: U32: Modifying pieces of evidence

  @Close
  Scenario: AC3 When I click on a skill tag, I can edit it.
  This is useful if I find that I made a spelling mistake.
  when this piece of evidence is saved, the tag will be changed globally for me
  (i.e., the spelling will be corrected on all my pieces of evidence and in associated headings).
    Given I am authenticated as a admin
    Given There is evidence in the table
    And I go to the evidence page with a project id
    And I open the piece of evidence
    And I click the edit button
    And I click the "skill" tag
    And I change the skill to "skill1"
    Then The edit list is updated


  @Close
  Scenario: AC4 I can remove any of the skill tags.
  This might be by providing an “x” on each tag button or some other means.
  Removing a tag only removes it from this piece of evidence.
  It does not delete the tag globally. I can also add other skill tags.
    Given I am authenticated as a admin
    Given There is evidence in the table
    And I go to the evidence page with a project id
    And I open the piece of evidence
    And I click the edit button
    And I click the delete "skill" button
    Then The delete list is updated

  @Close
  Scenario: AC4 I can remove any of the skill tags.
  This might be by providing an “x” on each tag button or some other means.
  Removing a tag only removes it from this piece of evidence.
  It does not delete the tag globally. I can also add other skill tags.
    Given I am authenticated as a admin
    Given There is evidence in the table
    And I go to the evidence page with a project id
    And I open the piece of evidence
    And I click the edit button
    When User inputs "new_skill" into the skill input textbox.
    Then The new list is updated