# School Record Management System

A desktop application for running a school's day-to-day records: students, teachers,
subjects, grades, attendance and fee payments, all behind a secure login. It is built with
JavaFX and PostgreSQL.

[![Build](https://github.com/ray100-art/SchoolRecordManagementSystem/actions/workflows/build.yml/badge.svg)](https://github.com/ray100-art/SchoolRecordManagementSystem/actions/workflows/build.yml) ![Java](https://img.shields.io/badge/Java-21-orange) ![JavaFX](https://img.shields.io/badge/JavaFX-21-blue) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791)

## Features

- **Secure login and registration.** Passwords are hashed with BCrypt (cost 12), and
  accounts carry an **Admin** or **Teacher** role.
- **Dashboard** giving an overview of the school.
- **Students, teachers and subjects.** Add, update, delete, search and list.
- **Grades.** Record and review results per student and subject.
- **Attendance.** Mark attendance and filter it by date.
- **Fees.** Record fee payments against students.
- **Self-initialising database.** All tables are created on first launch and a default
  admin account is seeded.

## Tech stack

| Layer | Tools |
|---|---|
| Language | Java 21 (Java Platform Module System) |
| UI | JavaFX 21, FXML, CSS, ControlsFX, BootstrapFX, Ikonli (Font Awesome 5) |
| Data | PostgreSQL via JDBC, DAO pattern |
| Security | jBCrypt |
| Testing | JUnit 5 |
| Build | Maven, `javafx-maven-plugin` |

## Architecture

```
src/main/java/com/example/schoolmanagementsystem/
├── MainApp.java     Application entry point
├── controller/      One controller per screen (FXML-bound)
├── dao/             Data access objects plus DatabaseManager (connections, schema, seeding)
└── model/           JavaFX property-based models
src/main/resources/.../fxml/   Screen layouts
src/main/resources/.../css/    Styling
```

Each screen follows **FXML view → controller → DAO → PostgreSQL**, so the UI, business logic
and SQL stay separate.

## Running locally

**Requirements:** JDK 21 or newer, Maven, and PostgreSQL with an empty `school_management`
database.

1. Point the app at your database. Host, port and database name are in
   `src/main/resources/db.properties`. Credentials come from the environment:
   ```bash
   export DB_USERNAME=postgres
   export DB_PASSWORD=your-postgres-password   # PowerShell: $env:DB_PASSWORD="..."
   ```
2. Run it:
   ```bash
   mvn clean javafx:run
   ```
3. Sign in with the seeded admin account, **`admin` / `Admin@2024`**, and change the
   password straight away.

## Tests

```bash
mvn test
```

`StudentDAOTest` covers the student data-access layer. It needs the database to be
reachable.

## Roadmap

- PDF report cards and CSV import/export (the iText and OpenCSV libraries are already
  included).
- An announcements screen (the table already exists).
