# Task 6 verification record

Execution date: 23 October 2026, Singapore time.

## Implemented behaviour

GET /api/tasks/summary is registered as a literal route and returns text/plain. TaskSummaryService reads the stored tasks and uses an injected Spring AI ChatClient. Nonempty task lists are sent to the configured provider with completed and pending counts. The Task POJO, generated IDs, HashMap storage, and six original endpoint success codes are preserved.

Spring Initializr selected Spring AI 2.0.1 with Spring Boot 4.1.2. The application reads its provider credential through the assignment's environment reference: `spring.ai.openai.api-key=${OPENAI_API_KEY}`.

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

The Postman collections were executed by Newman against the packaged application with a local OpenAI-compatible test server. The reports record simulated provider responses for repeatable checks of the integration contract.

## Runtime configuration and Postman procedure

Provider configuration and step-by-step Postman instructions are in [Task 6 setup](../task6-ai-summary.md). The application reads OPENAI_API_KEY, OPENAI_BASE_URL, and OPENAI_MODEL from the shell environment. Credentials stay outside the repository.

For a provider run, create one pending task and one completed task, request the summary, and compare its meaning with GET /api/tasks. After marking the pending task complete, the next summary should describe two completed tasks and zero pending tasks. Exact wording varies by model.
