# Software Requirements Specification — Task Manager

**A single-user, framework-free Java SE application**

| Field | Value |
|-------|-------|
| **Project** | `task-manager` |
| **Document ID** | SRS-TM-001 |
| **Version** | 1.0 |
| **Status** | Draft for review |
| **Date** | October 20, 2026 |
| **Language** | Java (Standard Edition) |
| **Frameworks** | None (JDK standard library only) |

---

## Contents

- [1 Introduction](#1-introduction)
  - [1.1 Purpose](#11-purpose)
  - [1.2 Scope](#12-scope)
  - [1.3 Definitions, Acronyms and Abbreviations](#13-definitions-acronyms-and-abbreviations)
  - [1.4 References](#14-references)
  - [1.5 Document Overview](#15-document-overview)
- [2 Overall Description](#2-overall-description)
  - [2.1 Product Perspective](#21-product-perspective)
  - [2.2 Product Functions](#22-product-functions)
  - [2.3 User Characteristics](#23-user-characteristics)
  - [2.4 Operating Environment](#24-operating-environment)
  - [2.5 Design and Implementation Constraints](#25-design-and-implementation-constraints)
  - [2.6 Assumptions and Dependencies](#26-assumptions-and-dependencies)
- [3 Functional Requirements](#3-functional-requirements)
  - [3.1 Summary](#31-summary)
  - [3.2 Actors](#32-actors)
  - [3.3 Detailed Use Cases](#33-detailed-use-cases)
- [4 Non-Functional Requirements](#4-non-functional-requirements)
- [5 External Interface Requirements](#5-external-interface-requirements)
  - [5.1 User Interface](#51-user-interface)
  - [5.2 Hardware and Software Interfaces](#52-hardware-and-software-interfaces)
  - [5.3 Communication Interfaces](#53-communication-interfaces)
- [6 Data Requirements](#6-data-requirements)
  - [6.1 Domain Entities](#61-domain-entities)
  - [6.2 Persistence Format](#62-persistence-format)
  - [6.3 Data Integrity Rules](#63-data-integrity-rules)
- [7 System Architecture Overview](#7-system-architecture-overview)
- [8 Acceptance Criteria](#8-acceptance-criteria)

---

## 1 Introduction

### 1.1 Purpose

This document specifies the requirements for **Task Manager**, a single-user, console-oriented task management application written in Java Standard Edition. The application must be implemented without any framework or third-party library: only classes of the Java Class Library (`java.*`, `javax.*` from the JDK) may be used in production code.

### 1.2 Scope

The system allows one local user to register, log in, and manage a personal list of tasks. Tasks carry a title, description, due date, priority, status, category and an arbitrary set of tags. Tasks can be searched, filtered, sorted and reported on. All data is persisted locally in plain text files between sessions.

**In scope**

- Local user accounts (registration, login, logout).
- Create / read / update / delete of tasks.
- Task attributes: title, description, due date, priority, status, category, tags, timestamps.
- Status workflow: `TODO → IN_PROGRESS → DONE`, plus `CANCELLED`.
- Search, filter and sort of the task list.
- Category and tag management.
- Due-date reminders shown at start-up and on demand.
- Statistics (counts per status, completion rate, overdue list).
- File-based persistence with load at start-up and save on mutation.

**Out of scope**

- Client/server operation, networking, multi-user concurrency.
- Relational database or ORM of any kind.
- Web or mobile front ends; any GUI framework.
- Calendar synchronisation, e-mail/SMS notifications.
- File attachments, task sharing, sub-tasks, recurring tasks (reserved for a future version).

### 1.3 Definitions, Acronyms and Abbreviations

| Term | Definition |
|------|------------|
| **Task** | A unit of work owned by exactly one user. |
| **Category** | A user-defined grouping of tasks (exactly one per task). |
| **Tag** | A free-form label; a task may have zero or more tags. |
| **Priority** | Enumeration `LOW`, `MEDIUM`, `HIGH`, `URGENT`. |
| **Status** | Enumeration `TODO`, `IN_PROGRESS`, `DONE`, `CANCELLED`. |
| **Repository** | Interface abstracting persistence of a domain entity. |
| **Service** | Class holding business rules; called by the UI layer. |
| **FR / NFR** | Functional / Non-functional requirement. |
| **UC** | Use case. |
| **JDK** | Java Development Kit (version 17 or later). |



## 2 Overall Description

### 2.1 Product Perspective

Task Manager is a self-contained, stand-alone desktop application. It is organised in four layers, each depending only on the layer below:

1. **Presentation** — `ConsoleUI`: menus, input parsing, output formatting.
2. **Service** — `AuthService`, `TaskService`, `ReminderService`, `ReportService`: business rules and validation.
3. **Domain** — `User`, `Task`, `Category`, `Tag`, `Priority`, `Status`: plain Java objects, no persistence knowledge.
4. **Persistence** — `TaskRepository`, `UserRepository` (interfaces) and their file-based implementations (`FileTaskRepository`, `FileUserRepository`), built on the utility class `FileStorage`.

No layer may reference a layer above it. The repositories are hidden behind interfaces so that the storage mechanism can be replaced without touching the service or UI layers.

### 2.2 Product Functions

- **Account management:** register, log in, log out.
- **Task lifecycle:** create, list, view, edit, delete, change status.
- **Organisation:** categories and tags.
- **Retrieval:** keyword search, filter by status / priority / category / due date, sorting by due date / priority / creation date.
- **Awareness:** due-date reminders and overdue highlighting.
- **Insight:** statistics and reports.
- **Durability:** automatic load at start-up and save on every mutation.

### 2.3 User Characteristics

The system targets a single, technically literate user (the owner of the machine) who is comfortable with a terminal. No training is required beyond the on-screen menu and the built-in help command.

### 2.4 Operating Environment

- **Runtime:** Java SE Runtime Environment 17 or newer.
- **OS:** any platform with a JVM (Windows, Linux, macOS).
- **Display:** a text terminal with UTF-8 support.
- **Storage:** read/write access to an application data directory.

### 2.5 Design and Implementation Constraints

- **No frameworks and no third-party libraries.** Spring, Jakarta EE, Hibernate, JavaFX, JUnit (in production code), Gson, etc. are forbidden. Only the JDK class library may be used.
- **No database.** Persistence is plain-text files handled with `java.nio.file` / `java.io`.
- **No GUI framework.** The user interface is console-based. `javax.swing` is permitted only as an optional, clearly separated extension module.
- **Java 17 language level**, using records, switch expressions and the `java.time` API where appropriate.
- **Layered architecture** with dependency inversion at the persistence boundary (repository interfaces).
- Code must follow the **Sun/Oracle Java naming conventions** and be documented with **Javadoc**.

### 2.6 Assumptions and Dependencies

- Exactly one user profile is active at a time; concurrent access from two processes is not supported.
- Passwords are hashed with **SHA-256** over a per-user random salt; the plain password is never stored.
- The data directory is writable and not shared over a network drive.
- System clock is correct; due-date logic relies on `LocalDate.now()`.

---

## 3 Functional Requirements

### 3.1 Summary

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-01 | The system shall allow a new user to register with a unique username and a password of at least 8 characters. | High |
| FR-02 | The system shall authenticate a registered user by username and password and start a session. | High |
| FR-03 | The system shall allow the logged-in user to log out. | Medium |
| FR-04 | The system shall allow the user to create a task with title (mandatory), description, due date, priority, category and tags. | High |
| FR-05 | The system shall list all tasks of the current user. | High |
| FR-06 | The system shall allow the user to update any attribute of an existing task. | High |
| FR-07 | The system shall allow the user to delete a task after a confirmation prompt. | High |
| FR-08 | The system shall allow the user to change the status of a task (`TODO`, `IN_PROGRESS`, `DONE`, `CANCELLED`) and to mark it as done in one step. | High |
| FR-09 | The system shall support keyword search over title and description. | High |
| FR-10 | The system shall support filtering by status, priority, category and due date (overdue / due today / due this week). | High |
| FR-11 | The system shall support sorting by due date, priority, creation date or title, ascending or descending. | Medium |
| FR-12 | The system shall allow the user to create, rename and delete categories; each category name is unique per user. | Medium |
| FR-13 | The system shall allow the user to add and remove tags on a task and to list all tags in use. | Medium |
| FR-14 | The system shall display, at start-up and on demand, a reminder list of tasks that are overdue or due within the next 24 hours. | Medium |
| FR-15 | The system shall display statistics: task count per status, number of overdue tasks, and completion rate. | Medium |

### 3.2 Actors

| Actor | Description |
|-------|-------------|
| **User** (primary) | A registered person who has logged in and manipulates tasks, categories and tags. |
| **Scheduler / System Clock** (secondary) | Triggers the reminder check at start-up and at the beginning of each main-menu cycle. |
| **File System** (supporting) | Stores and returns the persisted data files; not a human actor. |

### 3.3 Detailed Use Cases

#### 3.3.1 UC-01 — Register Account

**Actors:** User.

**Preconditions:** No session is active.

**Main flow**

1. User selects Register.
2. System asks for username and password (twice).
3. System verifies that the username is unique, that both passwords match and that the password has ≥ 8 characters.
4. System generates a random salt, stores the salted hash and the user record.
5. System confirms the successful registration.

**Alternative flows**

- **3a.** Username already taken → error, return to step 2.
- **3b.** Passwords differ → error, return to step 2.
- **3c.** Password too short → error, return to step 2.

**Postconditions:** A new user record exists in `users.dat`.

#### 3.3.2 UC-02 — Log In

**Actors:** User, Scheduler.

**Preconditions:** At least one user is registered.

**Main flow**

1. User enters username and password.
2. System looks up the user, hashes the entered password with the stored salt and compares the result.
3. On success a session is created, the main menu is shown and the reminder check is triggered.

**Alternative flows**

- **2a.** Unknown username or wrong password → generic error "invalid credentials", no session is created.

**Postconditions:** A session exists; the main menu is displayed.

#### 3.3.3 UC-03 — Create Task

**Actors:** User.

**Preconditions:** The user is logged in.

**Main flow**

1. User selects Create task.
2. System prompts for title, description, due date, priority, category and tags.
3. User enters the data.
4. System validates the input.
5. System creates a `Task` with a generated id and status `TODO`.
6. `TaskService` delegates to `TaskRepository`.
7. `FileTaskRepository` writes the task atomically to `tasks.dat`.
8. System prints a confirmation with the new id.

**Alternative flows**

- **4a.** Validation fails → error message, re-prompt from step 2.
- **7a.** Write error → storage error message, the task remains in memory and the user may retry.

**Postconditions:** The task exists in memory and on disk.

#### 3.3.4 UC-04 — Search and Filter Tasks

**Actors:** User.

**Preconditions:** The user is logged in.

**Main flow**

1. User selects Search / Filter.
2. System asks for a keyword and/or filter criteria.
3. System applies the criteria to the user's tasks.
4. System prints the result as a numbered table.

**Alternative flows**

- **3a.** No task matches → message "no matching tasks".
- **3b.** Invalid date format → validation error, return to step 2.

**Postconditions:** The result list is displayed; no data is changed.

#### 3.3.5 UC-05 — Change Task Status

**Actors:** User.

**Preconditions:** The user is logged in and at least one task exists.

**Main flow**

1. User selects Change status and a task id.
2. System checks that a legal transition is requested.
3. System updates the status and, for `DONE`, sets `completedAt`.
4. System persists and confirms.

**Alternative flows**

- **2a.** Illegal transition (e.g. `DONE → TODO`) → error, no change.

**Postconditions:** Status and timestamp are updated on disk.

#### 3.3.6 UC-06 — Receive Due Reminder

**Actors:** Scheduler (trigger), User (observer).

**Preconditions:** The user is logged in.

**Main flow**

1. Scheduler triggers `ReminderService`.
2. Service collects tasks that are overdue or due within 24 hours and are not `DONE`/`CANCELLED`.
3. System prints the reminder list, overdue tasks marked with `!`.

**Alternative flows**

- **2a.** No task qualifies → nothing is printed.

**Postconditions:** Reminder list displayed; no data changed.

---

## 4 Non-Functional Requirements

| ID | Requirement | Category |
|----|-------------|----------|
| NFR-01 | Any menu command on a data set of 10 000 tasks shall return within **500 ms** on a standard laptop. | Performance |
| NFR-02 | Start-up with 10 000 tasks shall complete within **2 s**. | Performance |
| NFR-03 | A crash or power loss during a write shall never leave a partially written file; writes use a temp file followed by an atomic move. | Reliability |
| NFR-04 | Passwords are stored only as **SHA-256** hashes with a per-user random salt; no plain-text secret is ever written to disk. | Security |
| NFR-05 | The application shall run unchanged on Windows, Linux and macOS with JDK 17+. | Portability |
| NFR-06 | Every class shall have a single responsibility; the service layer shall be usable without the console layer (testability). | Maintainability |
| NFR-07 | Every user input shall be validated; no uncaught exception may terminate the program. | Robustness |
| NFR-08 | Console output shall use clear, consistent, column-aligned formatting; every error message shall state the cause and the expected input. | Usability |
| NFR-09 | Repository interfaces shall allow the file-based implementation to be replaced by another storage mechanism without changes in the service or UI layer. | Extensibility |

---

## 5 External Interface Requirements

### 5.1 User Interface

The interface is a text menu. A typical session looks as follows:

```text
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
Due date (YYYY-MM-DD, empty = none): 2025-02-01
Priority (LOW/MEDIUM/HIGH/URGENT): HIGH
Category (empty = none): University
Tags (comma separated): design,srs

>> Task T2001 created (status TODO).
```

### 5.2 Hardware and Software Interfaces

- JVM 17+ providing `java.base`.
- Standard input / standard output streams.
- Local file system for the three data files described in Section 6.

### 5.3 Communication Interfaces

None. The application performs no network communication.

---

## 6 Data Requirements

### 6.1 Domain Entities

- **User** — id, username (unique), passwordHash, salt, createdAt.
- **Task** — id, title, description, dueDate, priority, status, createdAt, completedAt, userId, categoryId, tags.
- **Category** — id, name (unique per user), color, userId.
- **Tag** — name (unique per user).

### 6.2 Persistence Format

Data is stored in three pipe-delimited UTF-8 text files in the application data directory. The first line of each file is a format version header.

**`users.dat`**

```text
# v1
U1001|alice|9f2c...|3b71...|2025-01-15
```

**`tasks.dat`**

```text
# v1
T2001|U1001|Write SRS|Chapter 1-8|2025-02-01|HIGH|TODO|C3001|design,srs|2025-01-16|
```

**`categories.dat`**

```text
# v1
C3001|U1001|University|#3366cc
```

Fields must not contain the delimiter `|`; it is escaped as `\p` on write and unescaped on read.

### 6.3 Data Integrity Rules

1. A task always belongs to exactly one existing user.
2. A task's category is either empty or an existing category of the same user.
3. `completedAt` is set if and only if the status is `DONE`.
4. Ids are unique and never reused within a file.
5. On load, malformed lines are skipped and reported, never silently accepted.

---

## 7 System Architecture Overview

The system is organised in four layers; each layer depends only on the layer below it.

```text
┌──────────────────────────────────────────────────────┐
│  presentation                                        │
│  ConsoleUI                                           │
├──────────────────────────────────────────────────────┤
│  service                                             │
│  AuthService   TaskService                           │
│  ReminderService   ReportService                     │
├──────────────────────────────────────────────────────┤
│  domain                                              │
│  User  Task  Category  Tag                           │
│  Priority  Status (enums)                            │
├──────────────────────────────────────────────────────┤
│  persistence                                         │
│  UserRepository  TaskRepository   (interfaces)       │
│  FileUserRepository  FileTaskRepository              │
│  FileStorage (utility)                               │
└──────────────────────────────────────────────────────┘
```

- **Presentation** depends on Service.
- **Service** depends on Domain and on the repository interfaces.
- **Domain** depends on nothing above it.
- **Persistence** implements the repository interfaces and depends only on Domain and on `FileStorage`.

---

## 8 Acceptance Criteria

The system is accepted when all of the following hold:

1. A new user can register, log in, log out and log in again in a fresh process; data survives the restart.
2. All CRUD operations on tasks work and are reflected on disk immediately.
3. Search, filter and sort return the expected subsets on a data set of at least 100 tasks.
4. Reminders list exactly the overdue and next-24-hours tasks.
5. Invalid input never crashes the program; a readable error is printed.
6. No class outside `java.*` / `javax.*` (JDK) is referenced in production code.
7. The class diagram matches the implemented packages.