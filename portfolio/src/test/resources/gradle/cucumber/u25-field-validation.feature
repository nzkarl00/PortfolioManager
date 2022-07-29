Feature: U7 - Linking a piece of evidence

    Scenario: AC7 - Some basic/logical validation is carried out on all fields

    #Post a new piece of evidence
        Given I am logged in
        And I am on the evidence page
        When I am entering into the title text field
        And I enter the characters "ABC"
        Then I can post my evidence
        And I get a success message

        Given I am logged in
        And I am on the evidence page
        When I am entering into the title text field
        And I enter the characters "ABC123!@#"
        Then I can post my evidence
        And I get a success message

        Given I am logged in
        And I am on the evidence page
        When I am entering into the description text field
        And I enter the characters "ABC"
        Then I can post my evidence
        And I get a success message

        Given I am logged in
        And I am on the evidence page
        When I am entering into the description text field
        And I enter the characters "ABC123!@#"
        Then I can post my evidence
        And I get a success message

    #I cannot post a piece of evidence without at least 3 characters, paraphrased from below
    #It is strange if my title or description has only one character, for example, or all characters are punctuation/symbols or numbers, etc.
        Given I am logged in
        And I am on the evidence page
        When I am entering into the title text field
        And I enter the characters "****"
        Then I cannot post my evidence
        And I get an error

        Given I am logged in
        And I am on the evidence page
        When I am entering into the title text field
        And I enter the characters "1234"
        Then I cannot post my evidence
        And I get an error

        Given I am logged in
        And I am on the evidence page
        When I am entering into the title text field
        And I enter the characters "AB"
        Then I cannot post my evidence
        And I get an error

        Given I am logged in
        And I am on the evidence page
        When I am entering into the description text field
        And I enter the characters "1234"
        Then I cannot post my evidence
        And I get an error

        Given I am logged in
        And I am on the evidence page
        When I am entering into the description text field
        And I enter the characters "AB"
        Then I cannot post my evidence
        And I get an error

    #I can have diacritics and characters from other languages in my text fields.
        Given I am logged in
        And I am on the evidence page
        When I am entering into the title text field
        And I enter the characters "ABC123!@#"
        #Acute, used in Latin
        And I enter the characters "◌́"
        #Macron, used in Māori
        And I enter the characters "◌̄"
        Then I can post my evidence
        And I get a success message


