Feature: Due reminders
  As a logged-in user
  I want to see overdue and soon-due tasks
  So that I do not miss deadlines

  Background:
    Given I am logged in as "alice"
    And today is "2025-02-10"

  Scenario Outline: Which tasks appear in the reminder list
    Given a task "T1" with due date "<due>" and status "<status>"
    When I open the reminders
    Then the reminder list <visibility> "T1"

    Examples:
      | due        | status      | visibility       |
      | 2025-02-09 | TODO        | contains         |
      | 2025-02-10 | TODO        | contains         |
      | 2025-02-11 | IN_PROGRESS | contains         |
      | 2025-02-12 | TODO        | does not contain |
      | 2025-02-09 | DONE        | does not contain |
      | 2025-02-09 | CANCELLED   | does not contain |

  Scenario: Overdue tasks are marked
    Given a task "T1" with due date "2025-02-09" and status "TODO"
    When I open the reminders
    Then "T1" is shown with the marker "!"

  Scenario: Task without due date never appears
    Given a task "T1" without a due date and status "TODO"
    When I open the reminders
    Then the reminder list does not contain "T1"

  Scenario: Nothing qualifies
    Given I have no task that is overdue or due within 24 hours
    When I open the reminders
    Then nothing is printed

  Scenario: Reminders are shown after login
    Given a task "T1" with due date "2025-02-10" and status "TODO"
    When I log out and log in again as "alice"
    Then the reminder list contains "T1"

  Scenario: Reminders do not modify data
    Given a task "T1" with due date "2025-02-09" and status "TODO"
    When I open the reminders
    Then tasks.dat is unchanged
