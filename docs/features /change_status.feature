Feature: Change task status
  As a logged-in user
  I want to change the status of a task
  So that I can show my progress

  Background:
    Given I am logged in as "alice"

  Scenario Outline: Legal transitions
    Given a task "T2001" with status "<from>"
    When I change the status of "T2001" to "<to>"
    Then the status of "T2001" is "<to>"
    And the change is saved in tasks.dat

    Examples:
      | from        | to          |
      | TODO        | IN_PROGRESS |
      | TODO        | DONE        |
      | TODO        | CANCELLED   |
      | IN_PROGRESS | DONE        |
      | IN_PROGRESS | CANCELLED   |

  Scenario: Marking done sets completedAt
    Given a task "T2001" with status "IN_PROGRESS"
    When I mark "T2001" as done
    Then the status of "T2001" is "DONE"
    And "T2001" has a completedAt timestamp

  Scenario: Cancelled task has no completedAt
    Given a task "T2001" with status "TODO"
    When I change the status of "T2001" to "CANCELLED"
    Then "T2001" has no completedAt timestamp

  Scenario Outline: Illegal transitions from a terminal status
    Given a task "T2001" with status "<from>"
    When I change the status of "T2001" to "<to>"
    Then I see an error about an illegal status transition
    And the status of "T2001" is still "<from>"
    And tasks.dat is unchanged

    Examples:
      | from      | to          |
      | DONE      | TODO        |
      | DONE      | IN_PROGRESS |
      | CANCELLED | IN_PROGRESS |

  Scenario: Same status requested
    Given a task "T2001" with status "TODO"
    When I change the status of "T2001" to "TODO"
    Then I see the message "status unchanged"

  Scenario: Unknown status value
    Given a task "T2001" with status "TODO"
    When I change the status of "T2001" to "FINISHED"
    Then I see an error listing "TODO, IN_PROGRESS, DONE, CANCELLED"

  Scenario: Unknown task id
    When I change the status of "T9999" to "DONE"
    Then I see the error "task not found"

  Scenario: Disk write fails during status change
    Given a task "T2001" with status "TODO"
    And the data directory is not writable
    When I mark "T2001" as done
    Then I see a storage error message
    And the status of "T2001" is still "TODO"
