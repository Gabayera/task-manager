# Task Manager

A single-user, console-oriented task management application written in pure **Java SE 17+** — no frameworks, no third-party libraries, no database. Everything is built on the JDK standard library and persisted to plain-text files.

> **Project:** `task-manager` · **Version:** 1.0 · **Spec:** SRS-TM-001  
> **Status:** Draft for review  
> **Team:** Matin Habibi · Amir Hossein Yazdanparast · Pedram Merikh

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack & Constraints](#tech-stack--constraints)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Domain Model](#domain-model)
- [Getting Started](#getting-started)
- [Assignment 6 — Reminder Feature](#assignment-6--reminder-feature)
- [Usage](#usage)
- [Data Persistence](#data-persistence)
- [Non-Functional Requirements](#non-functional-requirements)
- [Acceptance Criteria](#acceptance-criteria)
- [Out of Scope](#out-of-scope)
- [Roadmap](#roadmap)
- [Changelog](CHANGELOG.md)
- [References](#references)
- [Authors](#authors)
- [License](#license)

---

## Overview

Task Manager lets a single local user register, log in, and manage a personal list of tasks. Tasks carry a title, description, due date, priority, status, category, and an arbitrary set of tags. Tasks can be searched, filtered, sorted, and reported on. All data is persisted locally in plain-text files between sessions.

The application is organised in four strictly layered tiers, with the persistence mechanism hidden behind repository interfaces so it can be replaced without touching the service or UI layers.

---

## Features

### Account Management
- Register with a unique username and a password of at least **8 characters**
- Log in / log out with salted **SHA-256** password hashing
- Passwords are never stored in plain text

### Task Lifecycle (CRUD)
- Create, list, view, update, and delete tasks
- Task attributes: title (mandatory), description, due date, priority, status, category, tags, timestamps
- Delete requires a confirmation prompt

### Status Workflow
```
TODO ──► IN_PROGRESS ──► DONE
  │            │
  └────────────┴──────► CANCELLED
```
- `completedAt` is set **iff** status is `DONE`
- Mark-as-done in a single step is supported

### Search, Filter & Sort
- Keyword search over **title** and **description**
- Filter by status, priority, category, and due date (overdue / due today / due this week)
- Sort by due date, priority, creation date, or title — ascending or descending

### Organisation
- Categories: create, rename, delete (unique name per user)
- Tags: add/remove on a task; list all tags in use

### Awareness & Insight
- **Reminders** at start-up and on demand: overdue tasks and tasks due within 24 hours (excluding `DONE`/`CANCELLED`), overdue items marked with `!`
- **Statistics**: task count per status, number of overdue tasks, completion rate

### Durability
- Automatic load at start-up
- Save on every mutation using a temp-file + atomic move (crash-safe)

---

## Tech Stack & Constraints

| Area | Choice |
|------|--------|
| Language | Java 17 (records, switch expressions, `java.time`) |
| Frameworks | **None** — JDK class library only |
| Persistence | Plain-text files via `java.nio.file` / `java.io` |
| UI | Console (standard input/output) |
| Database | None |
| Build tool | Plain `javac` / `java` (no external dependencies) |

**Hard constraints**

- No Spring, Jakarta EE, Hibernate, JavaFX, Gson, JUnit (in production), etc.
- No networking, no client/server, no multi-user concurrency.
- No GUI (console only; `javax.swing` allowed only as an optional, isolated extension).
- Sun/Oracle naming conventions and Javadoc on every public class.
- Each layer may depend **only** on the layer below it.

---

## Architecture

Four layers, each depending only on the layer beneath it:

```
┌──────────────────────────────────────────────┐
│ Presentation   ConsoleUI                     │  menus, input parsing, formatting
├──────────────────────────────────────────────┤
│ Service        AuthService  TaskService      │  business rules & validation
│                ReminderService  ReportService│
├──────────────────────────────────────────────┤
│ Domain         User  Task  Category  Tag     │  plain Java objects
│                Priority  Status (enums)      │  (no persistence knowledge)
├──────────────────────────────────────────────┤
│ Persistence    UserRepository  TaskRepository│  interfaces
│                FileUserRepository            │  file-based implementations
│                FileTaskRepository            │
│                FileStorage (utility)         │
└──────────────────────────────────────────────┘
```

- **Dependency rule:** no layer may reference a layer above it.
- **Dependency inversion:** repositories are interfaces; services depend on abstractions, not on `FileStorage`.

---

## Project Structure

```
task-manager/
├── src/
│   └── main/java/com/taskmanager/
│       ├── ui/            # ConsoleUI
│       ├── service/       # AuthService, TaskService, ReminderService, ReportService
│       ├── domain/        # User, Task, Category, Tag, Priority, Status
│       └── persistence/   # UserRepository, TaskRepository,
│                          # FileUserRepository, FileTaskRepository, FileStorage
├── docs/
│   └── diagrams/          # use case, activity, class, ER diagrams (Appendices A–D)
├── data/                  # created at runtime
│   ├── users.dat
│   ├── tasks.dat
│   └── categories.dat
├── README.md
└── LICENSE
```

*(Package names are indicative — the layer boundaries are the contract, not the exact package naming.)*

---

## Domain Model

| Entity | Fields |
|--------|--------|
| **User** | id, username (unique), passwordHash, salt, createdAt |
| **Task** | id, title, description, dueDate, priority, status, createdAt, completedAt, userId, categoryId, tags |
| **Category** | id, name (unique per user), color, userId |
| **Tag** | name (unique per user) |
| **Priority** | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| **Status** | `TODO`, `IN_PROGRESS`, `DONE`, `CANCELLED` |

**Integrity rules**

1. A task always belongs to exactly one existing user.
2. A task's category is either empty or an existing category of the same user.
3. `completedAt` is set if and only if the status is `DONE`.
4. Ids are unique and never reused within a file.
5. On load, malformed lines are skipped **and reported** — never silently accepted.

---

## Getting Started

### Prerequisites

- **JDK 17 or newer** (`java -version` should report 17+)
- Any OS with a JVM (Windows, Linux, macOS)
- A UTF-8 capable terminal
- Read/write access to an application data directory

### Build

The scripts compile production code to `out/main` and test code to `out/test`.

```bash
./build.sh          # Linux / macOS
build.bat           # Windows
```

### Run

The full console application (`ConsoleUI` / `Main`) is planned for a later part and is **not wired up yet** (`run.sh` only prints a notice).
The feature delivered for Assignment 6 can be run on its own. See [Assignment 6 — Reminder Feature](#assignment-6--reminder-feature).

### Run the unit tests

```bash
./test.sh           # Linux / macOS
test.bat            # Windows
```

`test.sh` builds the project and runs every test class registered in `src/test/java/com/taskmanager/AllTests.java`.
Each test prints `PASS`/`FAIL`, then a summary line such as `70 passed, 0 failed, 70 total`. The exit code is non-zero if any test fails.

To run a single test class:

```bash
./build.sh
java -cp out/main:out/test com.taskmanager.testframework.TestRunner com.taskmanager.service.ReminderServiceTest
# Windows: java -cp "out\main;out\test" com.taskmanager.testframework.TestRunner com.taskmanager.service.ReminderServiceTest
```

> **Why not JUnit/pytest?** The SRS (Section 2.5) allows only the JDK class library. The project therefore ships a small JUnit-style harness in `src/test/java/com/taskmanager/testframework/` (`TestCase`, `Assert`, `TestRunner`). Test methods are public, take no arguments, and start with `test`. `setUp()`/`tearDown()` run around every test.

---

## Assignment 6 — Reminder Feature

**Feature:** due-date reminders. **Requirement:** FR-14, use case UC-06 (Receive Due Reminder). **Scenarios:** [`docs/features /reminders.feature`](docs/features%20/reminders.feature).

### What it does

`ReminderService.getReminders(userId)` returns the user's tasks that are

- **not** `DONE` or `CANCELLED`, **and**
- have a due date that is **overdue, today, or tomorrow** (the "next 24 hours"; due dates are calendar dates).

The list is sorted by due date, oldest first. `isOverdue(task)` tells the UI which entries to mark with `!`.
The service only reads data and never changes tasks or files. "Today" comes from an injectable `java.time.Clock`, so the tests can use a fixed date.

| File | Purpose |
|------|---------|
| `src/main/java/com/taskmanager/service/ReminderService.java` | Feature implementation (service layer) |
| `src/main/java/com/taskmanager/demo/ReminderDemo.java` | Stand-alone demo that prints the reminder list |
| `src/test/java/com/taskmanager/service/ReminderServiceTest.java` | Unit tests |
| `run-reminders.sh` / `run-reminders.bat` | Build and run the demo |

### Run the feature

```bash
./run-reminders.sh      # Linux / macOS
run-reminders.bat       # Windows
```

The demo creates sample tasks in a temporary data directory using the real `TaskService` and `FileTaskRepository`, then prints:

```
Reminders (overdue or due within 24 hours, today = 2026-11-25):
! T9001  Pay library fine         due 2026-11-23  [HIGH]
  T1001  Submit assignment 6      due 2026-11-25  [URGENT]
  T1002  Prepare presentation     due 2026-11-26  [HIGH]
```

The task due in 5 days and the `DONE` task are not listed.

### Unit tests (`ReminderServiceTest`)

The tests use a fixed clock (today = 2025-02-10, as in `reminders.feature`) and an in-memory `TaskRepository`, so they do not depend on the real date or on the file system.

| Test | Kind | Checks |
|------|------|--------|
| `testGetRemindersReturnsOverdueAndDueSoonTasks` | valid | Overdue, today, and tomorrow tasks are returned in order. Tasks due later, `DONE`/`CANCELLED` tasks, tasks without a due date, and other users' tasks are excluded. |
| `testIsOverdueOnlyForPastDueDates` | valid | Only past due dates get the `!` marker. |
| `testGetRemindersReturnsEmptyListWhenNothingIsDue` | valid | "Nothing qualifies" returns an empty list. |
| `testGetRemindersDoesNotModifyData` | valid | Reminders never write to the repository. |
| `testGetRemindersRejectsBlankUserId` | **error** | A `null`/blank user id throws `ValidationException`. |
| `testConstructorRejectsNullRepository` | **error** | The service cannot be created without a repository. |

Run only these tests:

```bash
./build.sh && java -cp out/main:out/test com.taskmanager.testframework.TestRunner com.taskmanager.service.ReminderServiceTest
```

See [`CHANGELOG.md`](CHANGELOG.md) for the full list of changes.

---

## Usage

A typical session:

```
TASK MANAGER - alice (3 tasks due)

 1) List tasks          6) Search / filter
 2) Create task         7) Categories
 3) Show task           8) Tags
 4) Update task         9) Statistics
 5) Delete task        10) Reminders
                       11) Log out
                        0) Help

> 2
Title: Write SRS
Due date (YYYY-MM-DD, empty = none): 2026-02-01
Priority (LOW/MEDIUM/HIGH/URGENT): HIGH
Category (empty = none): University
Tags (comma separated): design,srs

>> Task T2001 created (status TODO).
```

**Notes**
- Commands are selected by number from the menu; `0` prints help.
- The reminder check runs at start-up and at the beginning of each main-menu cycle.
- Invalid input never terminates the program — an error message states the cause and the expected input.

---

## Data Persistence

Data lives in three pipe-delimited UTF-8 text files. The first line of each file is a **format version header**.

**`users.dat`**
```
# v1
U1001|alice|9f2c...|3b71...|2025-01-15
```

**`tasks.dat`**
```
# v1
T2001|U1001|Write SRS|Chapter 1-8|2025-02-01|HIGH|TODO|C3001|design,srs|2025-01-16|
```

**`categories.dat`**
```
# v1
C3001|U1001|University|#3366cc
```

**Escaping:** the delimiter `|` is escaped as `\p` on write and unescaped on read.

**Write safety:** every write goes to a temporary file followed by an **atomic move**, so a crash or power loss never leaves a partially written file.

---

## Non-Functional Requirements

| ID | Requirement | Category |
|----|-------------|----------|
| NFR-01 | Any menu command on 10,000 tasks returns within **500 ms** | Performance |
| NFR-02 | Start-up with 10,000 tasks completes within **2 s** | Performance |
| NFR-03 | Writes are crash-safe (temp file + atomic move) | Reliability |
| NFR-04 | Passwords stored only as salted SHA-256 hashes | Security |
| NFR-05 | Runs unchanged on Windows, Linux, macOS with JDK 17+ | Portability |
| NFR-06 | Single responsibility per class; service layer usable without the console | Maintainability |
| NFR-07 | All input validated; no uncaught exception terminates the program | Robustness |
| NFR-08 | Clear, column-aligned output; errors state cause and expected input | Usability |
| NFR-09 | Repository interfaces allow swapping the storage mechanism | Extensibility |

---

## Acceptance Criteria

The system is accepted when **all** of the following hold:

1. A new user can register, log in, log out, and log in again in a fresh process — data survives the restart.
2. All CRUD operations on tasks work and are reflected on disk immediately.
3. Search, filter, and sort return the expected subsets on a data set of ≥ 100 tasks.
4. Reminders list exactly the overdue and next-24-hour tasks.
5. Invalid input never crashes the program; a readable error is printed.
6. No class outside `java.*` / `javax.*` (JDK) is referenced in production code.
7. The class diagram (Appendix C) matches the implemented packages.

---

## Out of Scope

- Client/server operation, networking, multi-user concurrency
- Relational database or ORM of any kind
- Web or mobile front ends; any GUI framework
- Calendar synchronisation, e-mail/SMS notifications
- File attachments, task sharing, sub-tasks, recurring tasks *(reserved for a future version)*

---

## Roadmap

- [ ] Optional, clearly separated `javax.swing` extension module
- [ ] Alternative storage implementation (e.g. in-memory or JSON) behind the existing repository interfaces
- [ ] Recurring tasks, sub-tasks, file attachments

---

## References

- IEEE Std 830–1998 — *Recommended Practice for Software Requirements Specifications*
- ISO/IEC 25010:2011 — *SQuaRE*
- UML 2.5 specification (for the diagrams in Appendix A–D)
- Design diagrams: `docs/diagrams/`
  - **Appendix A** — Use Case Diagram
  - **Appendix B** — Activity Diagram: Create Task
  - **Appendix C** — Class Diagram (Domain / Presentation & Service / Persistence)
  - **Appendix D** — Entity-Relationship Diagram

---

## Authors

- **Matin Habibi**
- **Amir Hossein Yazdanparast**
- **Pedram Merrikh**

---

## License

This project is released under the **MIT License** — see [`LICENSE`](LICENSE) for details.
