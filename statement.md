# Project Statement: Smart Library Management System (LMS)

## 1. Problem Statement
Academic institutions, community centers, and educational libraries often struggle with inefficient manual book logging, missing inventories, untracked book loans, and delayed returns. Manual book registers result in errors, duplicate records, poor visibility into inventory status, and difficulty in assessing overdue fines. 

To solve this problem, there is a clear need for an automated, lightweight, reliable, and user-friendly Library Management System that digitalizes catalog maintenance, patron registry, circulation transactions (issue/return), fine computations, and analytics reporting while ensuring persistent storage across sessions.

---

## 2. Scope of the Project
The **Smart Library Management System** provides an end-to-end software solution designed around core Object-Oriented Programming (OOP) principles in Java.

### In Scope:
- **Book Catalog Administration**: Maintaining complete inventory records (Book ID, Title, Author, Genre, Total Copies, Available Copies) with real-time stock adjustment and multi-criteria searching.
- **Member / Patron Registry**: Registering students/faculty with distinct member identifiers, contact information, and automated tracking of active borrows against institutional borrowing thresholds.
- **Circulation Management**: Seamlessly issuing and returning books, automatic calculation of due dates (14-day loan window), overdue tracking, and automated fine assessment.
- **Persistent Data Store**: Lightweight CSV-based file persistence ensuring all transactions, book inventory modifications, and user records survive system restarts without demanding external database setups.
- **System Analytics & Dashboard**: Instant aggregation of library inventory metrics, overdue items, member engagement, and fine totals.
- **Defensive Error Handling**: Domain-specific exception handling ensuring invalid transactions (e.g. out-of-stock books, limit-exceeded patrons, duplicate records) are trapped gracefully without system crashes.

### Out of Scope (Future Scope):
- Payment gateway integration for online fine clearance.
- RFID scanner hardware interfacing.
- Multi-user remote networked client-server synchronization.

---

## 3. Target Users
- **Librarians & Administrative Staff**: Primary operators performing inventory addition, catalog search, book issues, and returns.
- **College Students & Researchers**: Patrons looking up literature, monitoring due dates, and tracking borrow privileges.
- **Course Evaluators & Academic Reviewers**: Reviewing clean OOP design, design pattern adherence, exception handling, data structures, and automated verification suites.

---

## 4. High-Level Features
1. **Catalog Management**: Add, update, view, and search books by title, author, or genre.
2. **Member Management**: Register patrons and enforce borrow limit constraints (maximum 3 books concurrently).
3. **Circulation Engine**: Real-time checkout validation, copy decrement, return date stamping, copy restoration, and daily fine accrual.
4. **Analytics Summary**: Instant high-level dashboard displaying total titles, total copies, active loans, overdue accounts, and outstanding fines.
5. **Persistence Layer**: Automated synchronization to human-readable CSV flat files with self-healing seed initialization.
6. **Automated Validation Suite**: Built-in automated unit test runner validating edge cases, exceptions, and business logic invariants.
