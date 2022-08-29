Feature: U25: Piece of evidence creation

  @Close
  Scenario: AC2: The date field has today’s date entered by default. I can change the date using a date widget.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    Then I can see the prefilled date is today's date

  @Close
  Scenario: AC2: The date field has today’s date entered by default. I can change the date using a date widget.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    Then I can see that the range of the date widget is filled in with the project date range

  @Close
  Scenario: AC2: The date field has today’s date entered by default. I can change the date using a date widget.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I try to update the widget date to a new date that is within the project
    Then I can see the widget date field is set to the updated new date

  @Close
  Scenario: AC 3: During creation, there is a small “?” (or similar) icon next to the date that, when I hover over it, gives me information about it
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    Then Hovering my mouse over the question mark icon beside the date picker will give me information about it

  @Close
  Scenario: AC6: I can see all the pieces of evidence that I created on my list of evidences page. They are organised chronologically.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I have filled out all mandatory title, description, and date fields to an evidence
    And I click the save button
    Then There will be the data for the evidence I created

  @Close
  Scenario: AC4: Saving and cancelling give expected results. The save button is only enabled once the mandatory fields are filled in.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When The all mandatory fields to an evidence are empty, I cannot click the save button

  @Close
  Scenario: AC4: Saving and cancelling give expected results. The save button is only enabled once the mandatory fields are filled in.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I click the cancel button
    Then I can see the evidence creation page extract and replace by a plus button

  @Close
  Scenario: AC4: save success message, Saving and cancelling give expected results. The save button is only enabled once the mandatory fields are filled in.
    Given I am authenticated as a admin
    And I go to the evidence page with a project id
    And I click the Add Evidence button
    When I have filled out all mandatory title, description, and date fields to an evidence
    And I click the save button
    Then I will see a message that this evidence has saved successfully

  @Close
  Scenario Outline: U9 AC10: As usual, any error messages are displayed as close (in time and proximity) to the error itself;
  i.e., ideally, I should not have to click save/submit to see all the errors.
    Given User is logged in.
    And I go to the evidence page with a project id
    And I click the Add Evidence button

    When User enters <title> into the title
    And User enters <desc> into the description
    Then Save button can be clicked

    Examples:
    | title | desc |
    |"Title"|"Description"|
    |"  Title"|"  Description"|
    |"中文"|"Россия"|

  @Close
  Scenario Outline: U9 AC10: As usual, any error messages are displayed as close (in time and proximity) to the error itself;
  i.e., ideally, I should not have to click save/submit to see all the errors.
    Given User is logged in.
    And I go to the evidence page with a project id
    And I click the Add Evidence button

    When User enters <title> into the title
    And User enters <desc> into the description
    Then Save button cannot be clicked

    Examples:
      | title | desc |
      |"990"|"  Description"|
      |"  (*(^*&"|"  Description"|
      |"  "|"  Description"|