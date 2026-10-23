# TaskFlow Task Management API

Public repository: [Jefkong77/module3-taskflow-api](https://github.com/Jefkong77/module3-taskflow-api).

Module 3 mandatory assignment for NTU PACE. This Java 21 Spring Boot backend implements Tasks 1–5: a Lombok Task POJO, an initialized in-memory HashMap repository, a service, and six REST endpoints. Context and data analysis for Ms. Sanjela are in [the EDA](docs/EDA-and-context.md).

## Start and test

Prerequisites: JDK 21 and internet access for the first Maven dependency download. The generated Maven Wrapper is included. Spring Boot is **4.1.2**, selected from the stable Spring Initializr options on 23 October 2026. The wrapper uses Maven 3.10.0.

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
│   │   │   │   └── ApiExceptionHandler.java
│   │   │   ├── model/
│   │   │   │   └── Task.java
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java
│   │   │   └── service/
│   │   │       └── TaskService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/sg/edu/ntu/taskflowapi/
│           ├── TaskflowApiApplicationTests.java
│           └── service/
│               └── TaskServiceTest.java
├── docs/
│   ├── EDA-and-context.md
│   ├── document-review.md
│   ├── requirements-matrix.md
│   ├── finish-today.md
│   ├── sample-tasks.csv
│   ├── postman/
│   │   ├── TaskFlow.postman_collection.json
│   │   └── TaskFlow-Local.postman_environment.json
│   └── evidence/
├── scripts/
│   └── http_smoke.py
└── README.md
```

The root folder is `module3-taskflow-api` when cloned from GitHub; the earlier ZIP uses `taskflow-api`. Both contain the same Maven project. The `docs/evidence` folder contains the verification record and saved test results.

| Layer folder | Main class | Responsibility |
| --- | --- | --- |
| controller | TaskController | Receive HTTP requests and return responses |
| model | Task | Hold the task data |
| repository | TaskRepository | Generate IDs and store tasks in the HashMap |
| service | TaskService | Validate input and perform task operations |

`ApiExceptionHandler` also belongs in the controller folder because it turns exceptions into HTTP error responses. `TaskflowApiApplication` stays above the four folders so Spring can discover their components.

The repository uses a non-null HashMap with a counter for generated IDs. It returns copies and sorts lists by ID. Synchronized repository methods protect individual map operations and counter increments. Multi-step service operations are not database transactions.

## Verification and evidence

- Run `mvn clean verify` for service tests, Spring context startup, and JAR packaging.
- Import the collection and environment from `docs/postman` into Postman. Run the 24 requests in order. Each includes assertions; generated IDs are captured automatically and both synthetic tasks are deleted at the end.
- For additional HTTP verification, start a fresh server and run `python3 scripts/http_smoke.py` from a second terminal. It writes `docs/evidence/http-checks.json`.
- Read [actual verification status](docs/evidence/verification.md) for execution results and outstanding learner actions. Expected results in the guide are not automatically completed checks.
- Follow [finish today](docs/finish-today.md) for WSL setup, Postman, public GitHub publishing, and NTU Learn submission.
- Consult the [requirement matrix](docs/requirements-matrix.md) to map each assessed requirement to source and evidence.

## Scope and limitations

Tasks live only in this application's memory. A process restart or DevTools restart clears the map and resets the counter. The project starts empty; `docs/sample-tasks.csv` is an illustrative EDA fixture, not a seed import. There is no database, JPA, SQL, user authentication, frontend, or hosted API in this assignment baseline.

Optional Task 6 is **not implemented**. There is no Spring AI dependency, summary endpoint, or API key requirement. The brief states Tasks 1–5 are enough to pass. No optional feature is claimed as completed.

## References and assistance

The mandatory Word brief controls scope. The supplied `Module3_Backend_Development_Guide.md` provides supporting implementation guidance. Project scaffolding comes from [Spring Initializr](https://start.spring.io/); framework references are [Spring Boot](https://docs.spring.io/spring-boot/) and [Lombok](https://projectlombok.org/features/). Postman collection automation uses the [Newman runner](https://learning.postman.com/docs/collections/using-newman-cli/command-line-integration-with-newman/).

This package was prepared with ChatGPT assistance using the guide's examples, with additional documentation and executable verification. It is assisted reference work. The learner must review, understand, and adapt it in line with the assignment's instruction to write the code yourself and any instructor rules. No independent authorship or completed NTU Learn submission is asserted here.
