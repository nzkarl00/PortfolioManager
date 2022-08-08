Feature: U25: Piece of evidence creation

  Scenario: AC6: I can see all the pieces of evidence that I created on my list of evidences page. They are organised chronologically.
    Given I am authenticated as a admin
    And There is evidence in the table
    When I go to the evidence page
    Then There will be the data for the evidence I created

  Scenario: AC4: Saving and cancelling give expected results. The save button is only enabled once the mandatory fields are filled in.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When The all mandatory fields to an evidence are empty, I cannot click the save button

    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I click the cancel button
    Then I can see the evidence creation page extract and replace by a plus button

    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I have filled out all mandatory title, description, and date fields to an evidence
    And I click the save button
    Then I will see a message that this evidence has saved successfully