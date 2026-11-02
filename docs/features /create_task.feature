Feature: Create task
  As a logged-in user
  I want to create a task
  So that I can track my work

  Background:
    Given I am logged in as "alice"

  Scenario: Create a task with all fields
    When I create a task with title "Write SRS", due date "2025-02-01", priority "HIGH", category "University" and tags "design,srs"
    Then I see "Task T2001 created (status TODO)"
    And tasks.dat contains a line for task "T2001" with status "TODO"

  Scenario: Create a task with only a title
    When I create a task with title "Buy milk" and leave all other fields empty
    Then the task is created with status "TODO"
    And the task has no due date, no category and no tags

  Scenario: Empty title is rejected
    When I create a task with title "   "
    Then I see the error "title is required"
    And I am asked to enter the task data again
    And tasks.dat is unchanged

  Scenario Outline: Title length boundary
    When I create a task whose title has <length> characters
    Then the task creation is <result>

    Examples:
      | length | result   |
      | 100    | accepted |
      | 101    | rejected |

  Scenario Outline: Invalid due date
    When I create a task with title "Report" and due date "<date>"
    Then I see an error mentioning the format "YYYY-MM-DD"
    And no task is saved

    Examples:
      | date       |
      | 2025-13-40 |
      | 01/02/2025 |
      | tomorrow   |

  Scenario: Invalid priority
    When I create a task with title "Report" and priority "CRITICAL"
    Then I see an error listing "LOW, MEDIUM, HIGH, URGENT"

  Scenario: Duplicate tags are stored once
    When I create a task with title "Report" and tags "design,design,srs"
    Then the task has exactly 2 tags

  Scenario: Delimiter in the title survives a restart
    When I create a task with title "A|B"
    And I restart the application and log in as "alice"
    Then the task title is shown as "A|B"

  Scenario: Disk write fails
    Given the data directory is not writable
    When I create a task with title "Report"
    Then I see a storage error message
    And the task remains in memory
    And I can retry saving it
