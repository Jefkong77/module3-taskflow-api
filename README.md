# TaskFlow Task Management API

Public repository: [Jefkong77/module3-taskflow-api](https://github.com/Jefkong77/module3-taskflow-api).

Module 3 mandatory assignment for NTU PACE. This Java 21 Spring Boot backend implements Tasks 1–5: a Lombok Task POJO, an initialized in-memory HashMap repository, a service, and six CRUD/completion endpoints. Task 6 adds a seventh endpoint for a Spring AI summary. Context and data analysis for Ms. Sanjela are in [the EDA](docs/EDA-and-context.md).

## Source code: read in this order

Click a filename to open its complete code on GitHub.

| Order | Layer or purpose | File | What it does |
| --- | --- | --- | --- |
| 1 | Model | [Task.java](src/main/java/sg/edu/ntu/taskflowapi/model/Task.java) | Holds id, title, and completed; Lombok supplies getters, setters, and constructors |
| 2 | Repository | [TaskRepository.java](src/main/java/sg/edu/ntu/taskflowapi/repository/TaskRepository.java) | Stores tasks in a HashMap and generates IDs |
| 3 | Service | [TaskService.java](src/main/java/sg/edu/ntu/taskflowapi/service/TaskService.java) | Validates input and implements the six task operations |
| 4 | Controller | [TaskController.java](src/main/java/sg/edu/ntu/taskflowapi/controller/TaskController.java) | Maps the six task endpoints to service methods |
| 5 | Explicit errors | [ApiExceptionHandler.java](src/main/java/sg/edu/ntu/taskflowapi/controller/ApiExceptionHandler.java) | Converts exceptions into readable HTTP error responses |
| 6 | Configuration | [application.properties](src/main/resources/application.properties) | Sets the application name, port, JSON behaviour, and Spring AI environment references |

Task 6 has two additional classes:

| Layer | File | What it does |
| --- | --- | --- |
| Service | [TaskSummaryService.java](src/main/java/sg/edu/ntu/taskflowapi/service/TaskSummaryService.java) | Reads tasks, prepares the prompt, and calls Spring AI ChatClient |
| Controller | [TaskSummaryController.java](src/main/java/sg/edu/ntu/taskflowapi/controller/TaskSummaryController.java) | Exposes GET /api/tasks/summary as plain text |

The application starts in [TaskflowApiApplication.java](src/main/java/sg/edu/ntu/taskflowapi/TaskflowApiApplication.java). Dependencies and build settings are in [pom.xml](pom.xml). Browse [all application source](src/main) and [automated tests](src/test/java/sg/edu/ntu/taskflowapi).

## Start and test

Prerequisites: JDK 21 and internet access for the first Maven dependency download. The generated Maven Wrapper is included. Spring Boot is **4.1.2**, selected from the stable Spring Initializr options on 23 October 2026. The wrapper uses Maven 3.10.0. Spring AI 2.0.1 and its OpenAI starter were selected from Spring Initializr for this Spring Boot version. Before ordinary application startup, configure your private provider environment using [the Task 6 steps](docs/task6-ai-summary.md). Automated tests do not require a real key.

From the project root in WSL Ubuntu, use the installed Maven commands practised in class and specified in the assignment:

```bash
mvn -version
mvn clean verify
mvn spring-boot:run
```

Confirm Maven reports Java 21. Keep the server terminal running while using Postman. Stop it with Ctrl+C.

The generated Maven Wrapper is an alternative if installed Maven is unavailable: use `./mvnw clean verify` and `./mvnw spring-boot:run`. The script is already executable in this GitHub repository. In Windows PowerShell, its equivalents are `.\mvnw.cmd clean verify` and `.\mvnw.cmd spring-boot:run`.

The base URL is `http://localhost:8080`. To use another port in WSL, run `PORT=8081 mvn spring-boot:run` and update the Postman baseUrl. Fresh storage returns `[]` from GET `/api/tasks`.

## Endpoints

| Method | Path | Purpose | Success |
| --- | --- | --- | --- |
| GET | /api/tasks | List all tasks | 200 |
| GET | /api/tasks/{id} | Get a task | 200 |
| POST | /api/tasks | Create a task | 201 |
| PUT | /api/tasks/{id} | Replace title and completed | 200 |
| DELETE | /api/tasks/{id} | Delete a task | 200 |
| PUT | /api/tasks/{id}/complete | Set completed to true and save | 200 |
| GET | /api/tasks/summary | Return a short plain-English AI summary | 200 |

POST and PUT bodies use raw JSON in Postman with Content-Type `application/json`:

```json
{"title":"Test every TaskFlow endpoint","completed":false}
```

Creation returns a generated ID and a Location header:

```json
{"id":1,"title":"Test every TaskFlow endpoint","completed":false}
```

Unknown positive IDs return 404. Invalid IDs, blank or missing titles, titles over 200 characters after trimming, and malformed JSON return 400 with readable problem details. The 200-character limit is an additional implementation choice. DELETE intentionally returns **200** to match the brief. Repeating completion keeps completed true. The URL ID governs updates and a client-supplied creation ID is ignored. An explicit Jackson deserialization setting treats omitted or null completed values as false; PUT should send both writable fields.

## Layered design

The HTTP request enters TaskController, which calls TaskService through constructor injection. TaskService validates data and calls TaskRepository through constructor injection. TaskRepository owns the map and ID counter. Task is a plain POJO using Lombok annotations; it has no Spring annotations.

```text
module3-taskflow-api/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── .gitattributes
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/sg/edu/ntu/taskflowapi/
│   │   │   ├── TaskflowApiApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── TaskController.java
│   │   │   │   ├── ApiExceptionHandler.java
│   │   │   │   └── TaskSummaryController.java
│   │   │   ├── model/
│   │   │   │   └── Task.java
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java
│   │   │   └── service/
│   │   │       ├── TaskService.java
│   │   │       └── TaskSummaryService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/sg/edu/ntu/taskflowapi/
│           ├── TaskflowApiApplicationTests.java
│           ├── controller/
│           │   └── TaskSummaryHttpTest.java
│           └── service/
│               ├── TaskServiceTest.java
│               └── TaskSummaryServiceTest.java
├── docs/
│   ├── EDA-and-context.md
│   ├── document-review.md
│   ├── requirements-matrix.md
│   ├── finish-today.md
│   ├── task6-ai-summary.md
│   ├── sample-tasks.csv
│   ├── postman/
│   │   ├── TaskFlow.postman_collection.json
│   │   ├── TaskFlow-Local.postman_environment.json
│   │   └── TaskFlow-AI.postman_collection.json
│   └── evidence/
├── scripts/
│   └── http_smoke.py
└── README.md
```

The root folder is `module3-taskflow-api` when cloned from GitHub. Open this folder to work with the current Maven project. The `docs/evidence` folder contains the verification record and saved test results.

| Layer folder | Main class | Responsibility |
| --- | --- | --- |
| controller | TaskController | Receive HTTP requests and return responses |
| model | Task | Hold the task data |
| repository | TaskRepository | Generate IDs and store tasks in the HashMap |
| service | TaskService | Validate input and perform task operations |

`TaskSummaryController` calls `TaskSummaryService`, which reads tasks through `TaskService` and uses an injected Spring AI ChatClient.

`ApiExceptionHandler` also belongs in the controller folder because it turns exceptions into HTTP error responses. `TaskflowApiApplication` stays above the four folders so Spring can discover their components.

The repository uses a non-null HashMap with a counter for generated IDs. It returns copies and sorts lists by ID. Synchronized repository methods protect individual map operations and counter increments. Multi-step service operations are not database transactions.

## Verification and evidence

- Run `mvn clean verify` for service tests, Spring context startup, and JAR packaging.
- Import the original collection and environment from `docs/postman` into Postman for the 24-request Tasks 1–5 regression run. For Task 6, use the separate nine-request `TaskFlow-AI.postman_collection.json` on a fresh server after setting your provider environment. Each collection cleans up its two synthetic tasks. Check the meaning of real model responses manually.
- For additional HTTP verification, start a fresh server and run `python3 scripts/http_smoke.py` from a second terminal. It writes `docs/evidence/http-checks.json`.
- Read the [Tasks 1–5 verification record](docs/evidence/verification.md) and [Task 6 verification record](docs/evidence/task6-verification.md) for test methods, execution results, and saved reports.
- Follow [finish today](docs/finish-today.md) for WSL setup, Postman, public GitHub publishing, and NTU Learn submission.
- Consult the [requirement matrix](docs/requirements-matrix.md) to map each assessed requirement to source and evidence.

## Storage and AI summary

TaskFlow stores tasks in an initialized HashMap, as required by the assignment. Tasks remain available while the application is running; restarting the application starts a fresh task list and resets the ID counter.

Task 6 reads the current list and uses Spring AI to produce a short summary of pending and completed work. It leaves task records unchanged. The sample CSV supports the EDA and is not loaded automatically.

## Task 6 configuration and verification

The repository contains the required safe configuration reference:

```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

Keep the real key in your private WSL Ubuntu `~/.bashrc`. [Task 6 setup and Postman steps](docs/task6-ai-summary.md) cover OpenAI and the OpenRouter configuration used in earlier practice. Continue using `mvn spring-boot:run` once the environment is configured.

Automated verification uses model doubles and a local OpenAI-compatible test server to check prompts, provider requests, response parsing, and error handling. See [the Task 6 verification record](docs/evidence/task6-verification.md) for results. The setup guide explains how to run the Postman collection with the configured provider and compare summary text with the stored task counts.

An empty task list returns a direct statement of fact without a model call. A list above fifty tasks returns 413. Failed provider requests or blank provider output return 502. The fifty-task limit and fifteen-second client timeout are demonstration safeguards rather than assignment requirements.

## References

The mandatory Word brief controls scope. Project scaffolding comes from [Spring Initializr](https://start.spring.io/); framework references are [Spring Boot](https://docs.spring.io/spring-boot/) and [Lombok](https://projectlombok.org/features/). Task 6 uses the official [Spring AI documentation](https://docs.spring.io/spring-ai/reference/). Postman collection automation uses the [Newman runner](https://learning.postman.com/docs/collections/using-newman-cli/command-line-integration-with-newman/).
