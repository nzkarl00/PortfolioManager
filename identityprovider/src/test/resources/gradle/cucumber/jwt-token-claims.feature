Feature: JWT Token should represent a user's properties, including the key claim role.
  Role should show the users highest role to which they are authenticated.

  Background: default user exists
    Given: default user exists

  Scenario: User is authenticated as a student
    Given: default users has role "student"
    When: user logs in
    Then: user token contains role "student"

  Scenario: User is authenticated as a student
    Given: default users has role "student"
    And: default users has role "teacher"
    When: user logs in
    Then: user token contains role "teacher"