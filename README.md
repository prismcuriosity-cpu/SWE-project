# Student Portal — Development and Automation System

A self-contained **Student Portal** web application with automated UI testing,
built for the *Student Portal Development and Automation System* assignment.

Students can **register**, **log in**, and view their **dashboard**, **profile**
and **academic results**. On top of the application, a **Selenium WebDriver**
suite automates the full user journey — opening the portal, registering,
logging in, navigating the pages, and verifying that the correct student
information is displayed after authentication.

## Tech stack

| Concern            | Technology                                   |
|--------------------|----------------------------------------------|
| Language / build   | Java 21, Maven                               |
| Web framework      | Spring Boot 3.3 + Thymeleaf (server-rendered)|
| Database           | H2 (embedded; file-based in prod, in-memory in tests) |
| Data access        | Spring Data JPA / Hibernate                  |
| Authentication     | Spring Security (form login, BCrypt hashing) |
| UI automation      | Selenium WebDriver 4 + JUnit 5 (headless Chromium) |

## Features

- **Registration** — new students sign up with validated details; passwords are
  stored only as BCrypt hashes.
- **Login / logout** — session-based authentication via Spring Security.
- **Dashboard** — a personalised overview (program, course count, average marks).
- **Access Center** — a hub of quick links to the university's external portals
  (Student Portal, Notice Board, Class Routine, Blended Learning, Question Bank,
  CSE PMS, Hall Management), grouped into sections.
- **Profile** — the signed-in student's stored profile information.
- **Results** — the student's academic results with per-course grades and marks.
- Each student only ever sees **their own** data (resolved from the security principal).

## Project layout

```
src/main/java/com/portal/
├── StudentPortalApplication.java     # Spring Boot entry point
├── config/
│   ├── SecurityConfig.java           # Spring Security + BCrypt
│   └── DataSeeder.java               # seeds courses + a demo student
├── model/                            # Student, Course, Result entities
├── repository/                       # Spring Data JPA repositories
├── service/                          # StudentService, UserDetailsService
└── web/                              # AuthController, PortalController, form DTO
src/main/resources/
├── application.properties            # H2 + JPA + Thymeleaf config
├── templates/                        # index, register, login, dashboard, profile, results
└── static/css/style.css
src/test/java/com/portal/automation/
└── SeleniumPortalTest.java           # end-to-end Selenium automation
```

## Running the application

```bash
mvn spring-boot:run
```

Then open <http://localhost:8080>.

A **demo account** is seeded on first startup:

| Username | Password    |
|----------|-------------|
| `demo`   | `Passw0rd!` |

Or register your own account from the **Register** page.

## Running the Selenium automation

```bash
mvn test
```

The suite boots the application on a random port and drives a **headless
Chromium** browser through two scenarios:

1. **`registerLoginAndVerifyProfile`** — registers a brand-new student, logs in,
   and verifies the dashboard greeting and profile details (and that a new
   student has no results yet).
2. **`demoStudentSeesResults`** — logs in as the seeded demo student and verifies
   the dashboard stats, the results table (courses, titles, average) and logout.

### Browser / driver configuration

Chromium and a version-matched `chromedriver` are required. Their locations are
resolved from environment variables, with sensible defaults for this
environment:

| Variable          | Default                             | Purpose                  |
|-------------------|-------------------------------------|--------------------------|
| `CHROME_BIN`      | `/opt/pw-browsers/chromium`         | Chromium binary          |
| `CHROMEDRIVER_BIN`| `/opt/chromedriver141/chromedriver` | Matching chromedriver    |

If `CHROMEDRIVER_BIN` does not exist, Selenium Manager will attempt to download
a matching driver automatically. The chromedriver **major version must match**
the Chromium major version.

## Notes

- The production profile persists the H2 database to `./data/` (git-ignored); the
  test profile uses a throwaway in-memory database so runs are deterministic.
- Passwords are never stored or logged in plain text — only BCrypt hashes are
  persisted.
