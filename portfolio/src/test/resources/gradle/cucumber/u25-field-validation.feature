#Feature: U25 - Linking a piece of evidence
#
    Scenario: AC5 - Some basic/logical validation is carried out on all fields
#
#    #Post a new piece of evidence
         When I enter the characters "ABC"
         Then I can post my evidence

         When I enter the characters "ABC123!@#"
         Then I can post my evidence

         When I enter the characters "ABC"
         Then I can post my evidence

         When I enter the characters "ABC123!@#"
         Then I can post my evidence
#
#    #I cannot post a piece of evidence without at least 3 characters, paraphrased from below
#    #It is strange if my title or description has only one character, for example, or all characters are punctuation/symbols or numbers, etc.
         When I enter the characters "****"
         Then I get an error

         When I enter the characters "1234"
         Then I get an error

         When I enter the characters "AB"
         Then I get an error

         When I enter the characters "1234"
         Then I get an error

         When I enter the characters "AB"
         Then I get an error

     #I can have diacritics and characters from other languages in my text fields.
         When I enter the characters "ABC123!@#"
#        #Acute, used in Latin
         When I enter the characters "◌́"
#        #Macron, used in Māori
         When I enter the characters "◌̄"
         Then I can post my evidence
