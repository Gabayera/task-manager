Feature: Register account
  As a new user
  I want to register with a username and password
  So that I can keep my own task list

  Background:
    Given no session is active
    And the user "alice" does not exist

  Scenario: Successful registration
    When I register with username "alice" and password "secret123" confirmed as "secret123"
    Then I see a registration success message
    And users.dat contains a record for "alice"
    And the stored password hash is not equal to "secret123"

  Scenario: Username already taken
    Given a user "alice" is already registered
    When I register with username "alice" and password "secret123" confirmed as "secret123"
    Then I see the error "username already taken"
    And no second record for "alice" is added to users.dat

  Scenario: Passwords do not match
    When I register with username "alice" and password "secret123" confirmed as "secret124"
    Then I see the error "passwords do not match"
    And users.dat has no record for "alice"

  Scenario Outline: Password length boundary
    When I register with username "alice" and password "<password>" confirmed as "<password>"
    Then the registration result is "<result>"

    Examples:
      | password | result   |
      | abcdefg  | rejected |
      | abcdefgh | accepted |

  Scenario: Empty username
    When I register with username "" and password "secret123" confirmed as "secret123"
    Then I see a validation error about the username
    And the application keeps running

  Scenario: Same password gives different hashes for different users
    Given a user "alice" is registered with password "secret123"
    When I register with username "bob" and password "secret123" confirmed as "secret123"
    Then the stored hash of "bob" differs from the stored hash of "alice"
