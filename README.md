# TaskFlow Task Management API

TaskFlow is a Java 21 Spring Boot API for creating, viewing, updating, completing, and deleting tasks. It implements the four layers and in-memory storage specified in the Module 3 assignment. Task 6 adds a Spring AI endpoint that summarises completed and pending work.

Repository: [Jefkong77/module3-taskflow-api](https://github.com/Jefkong77/module3-taskflow-api).

## Source code by assignment task

The task numbers match the Module 3 assignment. Files are organised by their model, repository, service, or controller layer.

| Task | Layer or purpose | Source file or folder | Responsibility |
| --- | --- | --- | --- |
| Task 1 | Folder structure | [Application package](src/main/java/sg/edu/ntu/taskflowapi) | Contains the four required folders: model, repository, service, controller |
| Task 2 | Model | [Task.java](src/main/java/sg/edu/ntu/taskflowapi/model/Task.java) | Holds id, title, and completed; Lombok generates getters, setters, and constructors |
| Task 3 | Repository | [TaskRepository.java](src/main/java/sg/edu/ntu/taskflowapi/repository/TaskRepository.java) | Stores tasks in an initialized HashMap and generates IDs |
| Task 4 | Service | [TaskService.java](src/main/java/sg/edu/ntu/taskflowapi/service/TaskService.java) | Validates input and implements the six task operations |
| Task 5 | Controller | [TaskController.java](src/main/java/sg/edu/ntu/taskflowapi/controller/TaskController.java) | Maps the six task endpoints to service methods |
| Task 6 | AI summary service | [TaskSummaryService.java](src/main/java/sg/edu/ntu/taskflowapi/service/TaskSummaryService.java) | Reads tasks, prepares the prompt, and calls ChatClient |
| Task 6 | AI summary controller | [TaskSummaryController.java](src/main/java/sg/edu/ntu/taskflowapi/controller/TaskSummaryController.java) | Exposes GET /api/tasks/summary as plain text |

Supporting files:

| Purpose | Source file | Responsibility |
| --- | --- | --- |
| Explicit errors | [ApiExceptionHandler.java](src/main/java/sg/edu/ntu/taskflowapi/controller/ApiExceptionHandler.java) | Converts exceptions into readable HTTP error responses |
| Configuration, including Task 6 | [application.properties](src/main/resources/application.properties) | Configures the application, JSON binding, and Spring AI provider |
| Application startup | [TaskflowApiApplication.java](src/main/java/sg/edu/ntu/taskflowapi/TaskflowApiApplication.java) | Starts Spring Boot and component scanning |
| Build and dependencies, including Task 6 | [pom.xml](pom.xml) | Declares the Java version, Spring Boot build, and Spring AI dependencies |

Browse [application source](src/main) and [test source](src/test/java/sg/edu/ntu/taskflowapi).

## Project structure

Task labels show where each assignment requirement is implemented. Task 5 is in the controller folder; Task 6 spans the service and controller folders, with provider settings in application.properties and Spring AI dependencies in pom.xml.

```text
module3-taskflow-api/
├── pom.xml                                     # Build and Task 6 dependencies
├── mvnw
├── mvnw.cmd
├── src/
│   ├── main/
│   │   ├── java/sg/edu/ntu/taskflowapi/
│   │   │   ├── TaskflowApiApplication.java
│   │   │   ├── model/                          # Task 1: layer folder
│   │   │   │   └── Task.java                   # Task 2: model
│   │   │   ├── repository/                     # Task 1: layer folder
│   │   │   │   └── TaskRepository.java         # Task 3: repository
│   │   │   ├── service/                        # Task 1: layer folder
│   │   │   │   ├── TaskService.java            # Task 4: task operations
│   │   │   │   └── TaskSummaryService.java     # Task 6: AI summary
│   │   │   └── controller/                     # Task 1: layer folder
│   │   │       ├── TaskController.java         # Task 5: six task endpoints
│   │   │       ├── ApiExceptionHandler.java    # Supporting HTTP errors
│   │   │       └── TaskSummaryController.java  # Task 6: summary endpoint
│   │   └── resources/
│   │       └── application.properties          # Application and Task 6 settings
│   └── test/java/sg/edu/ntu/taskflowapi/
│       ├── TaskflowApiApplicationTests.java
│       ├── service/
│       │   ├── TaskServiceTest.java
│       │   └── TaskSummaryServiceTest.java
│       └── controller/
│           └── TaskSummaryHttpTest.java
├── docs/
│   ├── EDA-and-context.md
│   ├── document-review.md
│   ├── requirements-matrix.md
│   ├── finish-today.md
│   ├── task6-ai-summary.md
│   ├── sample-tasks.csv
│   ├── postman/
│   └── evidence/
└── scripts/
    └── http_smoke.py
```

## Build and run

Requirements: JDK 21, Maven, and internet access for the first dependency download. The project uses Spring Boot 4.1.2 and Spring AI 2.0.1. Provider environment variables are described in [Task 6 configuration](docs/task6-ai-summary.md#provider-configuration).

From the project root in WSL Ubuntu:

```bash
mvn -version
mvn clean verify
mvn spring-boot:run
```

Maven should report Java 21. The application listens at `http://localhost:8080`; keep the terminal running while sending requests. Ctrl+C stops the application. The generated Maven Wrapper is also available as `./mvnw` in WSL or `.\mvnw.cmd` in PowerShell.

[Local development and Postman procedures](docs/finish-today.md) cover cloning, startup, ports, and collection imports.

## API contract

| Method | Path | Behaviour | Success |
| --- | --- | --- | --- |
| GET | /api/tasks | List tasks | 200 |
| GET | /api/tasks/{id} | Retrieve one task | 200 |
| POST | /api/tasks | Create a task with a generated ID | 201 |
| PUT | /api/tasks/{id} | Update title and completed | 200 |
| DELETE | /api/tasks/{id} | Delete a task | 200 |
| PUT | /api/tasks/{id}/complete | Set completed to true and save | 200 |
| GET | /api/tasks/summary | Summarise completed and pending work | 200 |

POST and PUT accept JSON. Example request body:

```json
{"title":"Prepare the meeting agenda","completed":false}
```

POST returns the saved task and a Location header. Its response body includes a generated ID:

```json
{"id":1,"title":"Prepare the meeting agenda","completed":false}
```

The URL ID identifies a task during updates. Creation ignores a supplied ID. Titles are trimmed and must contain 1–200 characters. Invalid IDs, invalid titles, and malformed JSON return 400; unknown positive IDs return 404. Error responses use Spring ProblemDetail. Omitted or null completed values bind to false; PUT replaces both writable fields.

## Storage and request flow

TaskController calls TaskService through constructor injection. TaskService validates requests and calls TaskRepository. The repository owns the initialized HashMap and ID counter. Task is a plain Java object with Lombok annotations.

Tasks are stored for the application session. An application or DevTools restart starts an empty collection and resets the counter. The repository returns copies and lists tasks by ID. Synchronized repository methods protect individual map operations and ID allocation.

The summary service reads the current task list and returns text without changing task records. It uses the configured model for nonempty lists of up to fifty tasks. Empty lists return a fixed factual message, lists above fifty return 413, and provider failures or blank results return 502.

## Documentation

| Document | Contents |
| --- | --- |
| [EDA and context](docs/EDA-and-context.md) | Problem, task schema, synthetic sample data, and lifecycle |
| [Design rationale](docs/document-review.md) | Layer responsibilities, storage choices, and JSON behaviour |
| [Requirement matrix](docs/requirements-matrix.md) | Assignment requirements mapped to code and API behaviour |
| [Local development](docs/finish-today.md) | WSL setup, Maven startup, and Postman procedures |
| [Task 6 summary](docs/task6-ai-summary.md) | ChatClient flow, provider configuration, and summary examples |
| [Test results](docs/evidence/verification.md) | Build, API regression, and Postman/Newman reports |
| [Task 6 test details](docs/evidence/task6-verification.md) | Prompt, provider-contract, and summary-error checks |

## Tests

`mvn clean verify` runs 21 tests: ten task-service tests, one application-context test, five summary-service tests, and five summary HTTP tests. Summary unit tests use model doubles; HTTP integration tests use Spring AI with a local OpenAI-compatible test server and controlled responses.

The [CRUD collection](docs/postman/TaskFlow.postman_collection.json) contains 24 requests. The [AI collection](docs/postman/TaskFlow-AI.postman_collection.json) contains nine requests. Both use the [TaskFlow Local environment](docs/postman/TaskFlow-Local.postman_environment.json). Recorded Newman runs passed 38 CRUD assertions and 15 summary assertions; the summary run used the local test server. See [test results](docs/evidence/verification.md) for the reports.

## References

The assignment specification is `Module3_Assignment_mandatory (1) (2).docx`.

- [Spring Initializr](https://start.spring.io/)
- [Spring Boot documentation](https://docs.spring.io/spring-boot/)
- [Lombok features](https://projectlombok.org/features/)
- [Spring AI documentation](https://docs.spring.io/spring-ai/reference/)
- [Postman collection runs](https://learning.postman.com/docs/collections/running-collections/intro-to-collection-runs/)
- [Newman command-line runner](https://learning.postman.com/docs/collections/using-newman-cli/command-line-integration-with-newman/)
