Feature: U25: Piece of evidence creation

  Scenario: AC6: I can see all the pieces of evidence that I created on my list of evidences page. They are organised chronologically.
    Given There is evidence in the table
    And I am authenticated as a admin
    When I go to the evidence page
    Then There will be the data for the evidence I created
