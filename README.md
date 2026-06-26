# Spring Batch POC

A proof-of-concept project demonstrating **Spring Batch 6.0.4** with **Spring Boot 4.1** and **Java
25**.
It showcases two batch execution patterns: one-shot jobs and user-controlled stepped jobs, along
with scheduled triggers and an in-memory H2 database.

---

## Project Structure

```
src
├── main
│   ├── java/com/example/demo
│   │   ├── DemoApplication.java              # Spring Boot entry point
│   │   ├── config
│   │   │   ├── ScheduleConfig.java           # Enables @Scheduled tasks
│   │   │   └── batch
│   │   │       ├── BatchConfig.java           # Async job operator, thread pool, job registry
│   │   │       ├── PersonModificationJobConfig.java  # Job definitions (one-shot + 3 user-controlled jobs)
│   │   │       └── StepConfig.java            # Step beans (name, address, occupation modification)
│   │   ├── constant
│   │   │   ├── JobNames.java                  # Job name constants
│   │   │   └── JobParams.java                 # Job parameter keys
│   │   ├── controller
│   │   │   ├── DemoExceptionHandler.java      # Global error handling
│   │   │   ├── JobController.java             # REST API for batch job execution
│   │   │   └── PersonController.java          # REST API for person CRUD
│   │   ├── exception
│   │   │   ├── JobNotFoundException.java
│   │   │   ├── LastStepNotCompletedException.java
│   │   │   └── LastStepNotFoundException.java
│   │   ├── model
│   │   │   ├── dto
│   │   │   │   ├── ErrorMessage.java
│   │   │   │   ├── JobDto.java
│   │   │   │   ├── PersonDto.java
│   │   │   │   ├── PersonModifierRequest.java
│   │   │   │   └── SteppedJobDto.java
│   │   │   ├── entity
│   │   │   │   ├── Person.java                # Person JPA entity
│   │   │   │   ├── UserControlledJob.java      # Tracks a multi-step user-controlled job
│   │   │   │   └── UserControlledJobStep.java  # Tracks an individual step within a user-controlled job
│   │   │   └── enums
│   │   │       ├── JobStatus.java              # IN_PROGRESS / COMPLETED
│   │   │       ├── StepStatus.java             # IN_PROGRESS / COMPLETED / FAILED
│   │   │       └── StepType.java               # NAME / ADDRESS / OCCUPATION
│   │   ├── repository
│   │   │   ├── ContinuableJobRepository.java   # JPA repository for UserControlledJob
│   │   │   └── PersonRepository.java           # JPA repository for Person
│   │   └── service
│   │       ├── JobService.java                 # Core batch job orchestration logic
│   │       ├── PersonService.java              # Person CRUD service
│   │       ├── Scheduler.java                  # Scheduled one-shot jobs (daily at midnight)
│   │       └── tasklet
│   │           ├── AddressModifierTasklet.java  # Modifies person address
│   │           ├── NameModifierTasklet.java     # Modifies person name
│   │           └── OccupationModifierTasklet.java # Modifies person occupation
│   └── resources
│       ├── application.properties              # H2 in-memory DB, JPA, batch config
│       └── spring-batch-h2-schema.sql           # Spring Batch schema for H2
└── test
    └── java/com/example/demo/DemoApplicationTests.java
```

---

## Tech Stack

| Component    | Version / Library               |
|--------------|---------------------------------|
| Spring Boot  | 4.1.0                           |
| Java         | 25                              |
| Spring Batch | (via spring-boot-starter-batch) |
| Database     | H2 (in-memory)                  |
| JPA          | Hibernate / spring-data-jpa     |
| Build Tool   | Maven                           |
| REST API     | Spring WebMvc                   |

---

## Execution Patterns

### 1. One-Shot Job (`personModifierJob`)

Executes all three modifications **sequentially in a single batch job**:

1. Name modification
2. Address modification
3. Occupation modification

**Trigger via API:**

```bash
POST /job/person-modifier/run
Content-Type: application/json

{ "personId": 1 }
```

**Response:**

```json
{
  "jobId": 123,
  "type": "personModifierJob"
}
```

### 2. User-Controlled (Stepped) Job

Each modification step is exposed as an **individual, independent Spring Batch job**. The steps are
executed one at a time, with manual progression controlled by the client.

**Step order:** Name → Address → Occupation

**Start a new user-controlled job:**

```bash
POST /job/user-controlled/start
Content-Type: application/json

{ "personId": 1 }
```

**Continue to the next step (after the current step completes):**

```bash
POST /job/user-controlled/continue
Content-Type: application/json

{ "jobId": 1 }
```

**Check job status:**

```bash
GET /job/user-controlled/status/{jobId}
```

**Response example (all steps completed):**

```json
{
  "jobId": 1,
  "status": "COMPLETED",
  "addressFinished": true,
  "nameFinished": true,
  "occupationFinished": true
}
```

---

## Scheduled Jobs

The `Scheduler` runs three one-shot jobs daily at midnight (`0 0 0 * * *`) for persons with IDs 1,
2, and 3, provided those persons exist in the database.

Scheduling is enabled via `@EnableScheduling` in `ScheduleConfig`.

---

## Getting Started

### Prerequisites

- Java 25+
- Maven 3.9+

### Build & Run

```bash
mvn clean install
mvn spring-boot:run
```

The application starts on `http://localhost:8080` with an H2 in-memory database.

### H2 Console

Once the app is running, access the H2 console at:

```
http://localhost:8080/h2-console
```

- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** _(empty)_

---

## Configuration Highlights (`application.properties`)

| Property                           | Value                                  | Description                      |
|------------------------------------|----------------------------------------|----------------------------------|
| `spring.datasource.url`            | `jdbc:h2:mem:testdb`                   | In-memory H2 database            |
| `spring.jpa.hibernate.ddl-auto`    | `update`                               | Auto-create JPA entity tables    |
| `spring.batch.job.enabled`         | `false`                                | Disable auto-start of batch jobs |
| `spring.h2.console.enabled`        | `true`                                 | Enable H2 web console            |
| `spring.sql.init.schema-locations` | `classpath:spring-batch-h2-schema.sql` | Spring Batch metadata schema     |

---

## API Endpoints

| Method | Endpoint                              | Description                              |
|--------|---------------------------------------|------------------------------------------|
| POST   | `/person`                             | Create a new person                      |
| POST   | `/job/person-modifier/run`            | Execute the one-shot person modifier job |
| POST   | `/job/user-controlled/start`          | Start a new user-controlled job          |
| POST   | `/job/user-controlled/continue`       | Advance to the next step                 |
| GET    | `/job/user-controlled/status/{jobId}` | Get the current job & step status        |

---

## Spring Batch schema & JobRepository

The project uses a **JDBC-backed `JobRepository`** (enabled via `@EnableJdbcJobRepository` in
`PersonModificationJobConfig`) to persist batch metadata into the H2 database, including job
instances, executions, step
executions, and job parameters.

### How JobRepository is used

| Usage location                     | Role                                                                                                                   |
|------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| `PersonModificationJobConfig.java` | `JobBuilder` receives `JobRepository` to persist job execution metadata for both the one-shot and user-controlled jobs |
| `StepConfig.java`                  | `StepBuilder` receives `JobRepository` to persist step execution metadata                                              |
| `BatchConfig.java`                 | `JobRepository` is wired into `TaskExecutorJobOperator` to enable async job launches with proper metadata tracking     |
| `JobService.java`                  | `JobRepository.getJobExecution()` is called directly to retrieve execution state when advancing user-controlled steps  |

### Schema initialization

The batch metadata tables are created on startup via `spring-batch-h2-schema.sql` (configured in
`application.properties`). This file contains the standard Spring Batch DDL for H2.

* Official documentation: https://docs.spring.io/spring-batch/reference/schema-appendix.html
* Database engine specific schema located in package org.springframework.batch.core, schema-*.sql
  files, no MSSQL though

## Notes

- This is a **proof of concept**, not intended for production use.
- Tasklets simulate processing with a 2-second `Thread.sleep()` before persisting changes.
