# Task 6 summary integration tests

Recorded execution: 23 October 2026, Singapore time.

## Test design

TaskSummaryServiceTest uses controlled model doubles to inspect prompt data and exercise service behaviour. TaskSummaryHttpTest runs the application on a random local port and connects the actual Spring AI HTTP client to an in-process OpenAI-compatible test server.

The HTTP server supplies controlled responses at `/v1/chat/completions`. This design makes request serialization, response parsing, and error cases repeatable.

## Executed checks

| Area | Case | Result |
| --- | --- | --- |
| Prompt data | Task IDs, titles, states, completed count, and pending count | Passed |
| Literal titles | Braces in titles remain task data | Passed |
| Successful response | GET /api/tasks/summary returns 200 and text/plain | Passed |
| Stored state | Summary leaves task records unchanged | Passed |
| Empty task list | 200 with a factual empty-list message | Passed |
| Size limit | More than fifty tasks returns 413 | Passed |
| Provider error | Provider failure becomes readable 502 | Passed |
| Blank output | Empty provider content becomes readable 502 | Passed |
| Error details | HTTP response excludes provider diagnostics | Passed |

Five summary-service tests and five summary HTTP tests passed. The complete Maven build passed 21 tests with zero failures, errors, or skips.

## Postman collection run through Newman

The nine-request AI collection ran against the packaged application with the local test server. All fifteen assertions passed. The collection created two synthetic tasks, requested a summary, completed the pending task, requested another summary, deleted both records, and checked the empty-list response.

The captured provider requests contained completed/pending counts of 1/1 followed by 2/0. The [saved summary report](task6-postman-summary.json) contains the request data and explicitly labelled simulated response text.

## Reports

| Report | Contents |
| --- | --- |
| [Summary service tests](sg.edu.ntu.taskflowapi.service.TaskSummaryServiceTest.txt) | Five service-test results |
| [Summary HTTP tests](sg.edu.ntu.taskflowapi.controller.TaskSummaryHttpTest.txt) | Five integration-test results |
| [Maven build](task6-maven-verify.txt) | Complete 21-test build and packaging output |
| [AI collection run](task6-ai-newman.txt) | Nine requests and fifteen assertions |
| [Provider-contract data](task6-postman-summary.json) | Captured task data and controlled responses |
| [CRUD regression](task6-crud-newman.txt) | Twenty-four requests and thirty-eight assertions |

Provider configuration and interactive Postman procedures are documented in the [Task 6 guide](../task6-ai-summary.md).
