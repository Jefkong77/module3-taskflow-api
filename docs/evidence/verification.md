# TaskFlow test results

Automated build: 24 October 2026, Singapore time. API and Postman collection runs: 23 October 2026, Singapore time.

## Build and automated tests

The Tasks 1–6 build passed `clean verify` with 24 tests, zero failures, zero errors, and zero skips. The build uses Java 21.0.13 Temurin, Spring Boot 4.1.2, Spring AI 2.0.1, and Maven 3.10.0 through the generated wrapper.

| Test suite | Passed | Report |
| --- | --- | --- |
| Task service | 10 | [TaskServiceTest](sg.edu.ntu.taskflowapi.service.TaskServiceTest.txt) |
| Application context | 1 | [TaskflowApiApplicationTests](sg.edu.ntu.taskflowapi.TaskflowApiApplicationTests.txt) |
| Task controller HTTP integration | 3 | [TaskControllerHttpTest](sg.edu.ntu.taskflowapi.controller.TaskControllerHttpTest.txt) |
| Summary service | 5 | [TaskSummaryServiceTest](sg.edu.ntu.taskflowapi.service.TaskSummaryServiceTest.txt) |
| Summary HTTP integration | 5 | [TaskSummaryHttpTest](sg.edu.ntu.taskflowapi.controller.TaskSummaryHttpTest.txt) |
| Total | 24 | [Maven build log](java-http-maven-verify.txt) |

Task-controller HTTP tests start an isolated Spring Boot server and use Java's HTTP client to exercise all six task endpoints, JSON binding, validation, generated IDs, repeated completion, deletion, and missing resources. They run within the Maven test suite. Summary unit tests use model doubles. Summary HTTP integration tests connect the Spring AI client to a local OpenAI-compatible test server with controlled responses. They exercise request serialization, response parsing, and HTTP error handling.

## API and Postman collection runs

| Check | Recorded result | Report |
| --- | --- | --- |
| Task API HTTP regression | 25 requests passed | [HTTP regression](task6-http-regression.json) |
| CRUD collection through Newman | 24 requests; 38 assertions; zero failures | [CRUD run](task6-crud-newman.txt) |
| AI collection through Newman with local test server | 9 requests; 15 assertions; zero failures | [AI run](task6-ai-newman.txt) |
| Summary provider-contract data | One done / one pending, then two done / zero pending | [Requests and simulated responses](task6-postman-summary.json) |

The collections ran against the packaged application. The AI run used the local OpenAI-compatible test server. Its response text is controlled test data. [Task 6 test details](task6-verification.md) describe the covered cases.

## Storage and packaging checks

The Tasks 1–5 baseline reports include these checks:

| Check | Recorded result | Report |
| --- | --- | --- |
| Original clean build | 11 tests passed | [Baseline build log](maven-verify.txt) |
| Original HTTP regression | 25 requests passed | [Baseline HTTP report](http-checks.json) |
| Original Newman run | 24 requests; 38 assertions; zero failures | [JSON report](postman-newman.json); [text report](postman-newman.txt) |
| Executable Jar startup | Four HTTP checks passed, including null completed | [Jar checks](jar-checks.json) |
| Application restart | Stored task followed by an empty list after restart | [Restart check](restart-check.json) |

These baseline checks establish the collection storage and packaging behaviour. The Tasks 1–6 build and regression results are recorded in the tables above. The baseline HTTP server used port 18080; the application's default port is 8080.

## Covered API behaviour

The reports cover POST 201, the five other task-operation success responses of 200, 400 for invalid input, 404 for unknown IDs, title trimming, generated IDs, and saved completion state. JSON tests cover omitted and null completed values binding to false.

Summary checks cover text/plain responses, unchanged task records, an empty list, the fifty-task limit, failed provider requests, and blank provider output. Test procedures are in [local development](../finish-today.md) and the [Task 6 guide](../task6-ai-summary.md).
