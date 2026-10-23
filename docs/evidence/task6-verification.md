# Task 6 verification record

Execution date: 23 October 2026, Singapore time.

## Implemented behaviour

GET /api/tasks/summary is registered as a literal route and returns text/plain. TaskSummaryService reads the stored tasks and uses an injected Spring AI ChatClient. Nonempty task lists are sent to the configured provider with completed and pending counts. The Task POJO, generated IDs, HashMap storage, and six original endpoint success codes are preserved.

Spring Initializr selected Spring AI 2.0.1 with Spring Boot 4.1.2. The production configuration contains the exact assignment placeholder `spring.ai.openai.api-key=${OPENAI_API_KEY}`; no real key is present in the project.

## Executed checks

| Check | Result | Evidence |
| --- | --- | --- |
| Full Maven clean verify | 21 passed, no failures, errors, or skips | task6-maven-verify.txt |
| Summary service tests | 5 passed | TaskSummaryServiceTest summary |
| Summary HTTP tests through the real Spring AI client | 5 passed against a local provider simulator | TaskSummaryHttpTest summary |
| Original API HTTP regression | 25 requests passed | task6-http-regression.json |
| Original Postman collection | 24 requests, 38 assertions, no failures | task6-crud-newman.txt |
| Task 6 Postman collection | 9 requests, 15 assertions, no failures against the simulator | task6-ai-newman.txt |
| Provider request data | Two requests carried 1 done / 1 pending, then 2 done / 0 pending | task6-postman-summary.json |
| Missing or blank provider result | Readable HTTP 502, no provider diagnostic leakage | Summary service and HTTP tests |
| Empty list and size limit | 200 empty-list fact and 413 above fifty tasks, without model calls | Summary service and HTTP tests |

The 21 tests comprise ten existing task-service tests, one context test, five summary-service tests, and five summary HTTP tests. Context and HTTP tests use explicitly non-secret test placeholders. The summary HTTP tests exercise actual Spring AI request serialization and response parsing through a local OpenAI-compatible endpoint.

The Postman collections were executed by Newman against the packaged application. The simulated summary responses are labelled as local provider test output. They are not evidence of a real LLM completing the summary.

## Outstanding live verification

No real provider credential was available in the implementation workspace, and no live model was called. The learner must set the key in their private WSL Ubuntu ~/.bashrc, start the application, and run the Task 6 Postman collection against the chosen provider. Check that the summary correctly identifies the actual pending and completed tasks; exact wording can vary.

The OpenRouter configuration is documented in ../task6-ai-summary.md. The API key variable name remains OPENAI_API_KEY as required by the brief, with the provider base URL and model selected through separate environment variables.

No instructor or NTU Learn submission has been made. The work remains under the learner's review.
