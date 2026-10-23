# Task 6 Spring AI task summary

Task 6 adds GET `/api/tasks/summary`. It reads the current task list, sends task titles and completion states through Spring AI, and returns a short plain-English summary. It is a read-only operation: it does not create, update, complete, or delete tasks.

The source and automated tests are implemented. A live-provider response using the learner's private API key is still to be verified. The HTTP contract tests use a local provider simulator and do not represent real AI output.

## Files and responsibilities

| File | Role |
| --- | --- |
| pom.xml | Spring AI 2.0.1 BOM and OpenAI starter from Spring Initializr for Spring Boot 4.1.2 |
| controller/TaskSummaryController.java | Maps GET /api/tasks/summary and returns text/plain |
| service/TaskSummaryService.java | Reads tasks, supplies the prompt to an injected ChatClient, and handles the response |
| src/main/resources/application.properties | References the key and optional provider settings through environment variables |
| service/TaskSummaryServiceTest.java | Tests prompt data, counts, empty lists, limits, and failures without a live provider |
| controller/TaskSummaryHttpTest.java | Exercises the real Spring AI HTTP client against a local test provider |

Java files are under `src/main/java/sg/edu/ntu/taskflowapi` or the matching `src/test/java/sg/edu/ntu/taskflowapi` package. The Task POJO and HashMap repository remain the required assignment storage.

## 1 Review the request flow

Postman sends GET `/api/tasks/summary`. Spring selects the literal summary route, rather than treating summary as a task ID. The controller calls the summary service. That service calls `findAllTasks`, counts completed and pending records, and supplies each task's ID, title, and status to ChatClient. The model's text is returned with HTTP 200.

The constructor receives `ChatClient.Builder` through Spring dependency injection and builds the client. The prompt asks for at most three short sentences, supplies counts, treats titles as data, and prohibits invented tasks or deadlines. Task data is placed in a literal UserMessage so braces in a title are not interpreted as template placeholders.

This connects to Lessons 3.12 and 3.15 for Spring AI and prompting, and Lesson 3.14 for layering and constructor injection.

## 2 Keep the real key in WSL Ubuntu

Open the private shell configuration in your WSL Ubuntu terminal:

```bash
code ~/.bashrc
```

For an OpenAI account, add a line there using your own key:

```bash
export OPENAI_API_KEY='YOUR_REAL_OPENAI_KEY'
```

For the OpenRouter setup used in the earlier Spring AI practice, use these environment variables instead:

```bash
export OPENAI_API_KEY='YOUR_REAL_OPENROUTER_KEY'
export OPENAI_BASE_URL='https://openrouter.ai/api/v1'
export OPENAI_MODEL='openai/gpt-4o-mini'
```

These strings are placeholders. Replace the key only inside your private `~/.bashrc`, outside the repository. Use one provider configuration at a time and update existing exports rather than adding conflicting duplicates. The variable remains named OPENAI_API_KEY to match the assignment; its value must belong to the chosen provider. Keep real values out of chat, screenshots, Postman exports, and project files.

Save the shell file, then reload it:

```bash
source ~/.bashrc
```

Check presence without displaying the value:

```bash
if [ -n "$OPENAI_API_KEY" ]; then
    echo "API key is set"
else
    echo "API key is not set"
fi
```

The project contains the assignment's exact safe reference:

```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

Spring AI 2 uses `spring.ai.openai.chat.model` for the model setting. The configured base URL includes `/v1`; this version uses the official OpenAI SDK underneath. OpenRouter uses its own full compatible base URL shown above.

## 3 Build and start

From the project root in the same WSL Ubuntu terminal:

```bash
mvn clean verify
mvn spring-boot:run
```

The automated tests use explicit non-secret test placeholders and local model doubles. They do not make live model requests. Application startup for ordinary use now requires the real OPENAI_API_KEY environment variable. Leave the server terminal running while testing in Postman.

## 4 Test with two synthetic tasks

Start with a fresh application so other task data is not included. In Postman, send POST to `http://localhost:8080/api/tasks` using Body, raw, JSON:

```json
{"title":"Review assignment requirements","completed":false}
```

Create a second task:

```json
{"title":"Build the task API","completed":true}
```

Send GET to:

```text
http://localhost:8080/api/tasks/summary
```

Expected: HTTP 200, Content-Type text/plain, and a short summary describing one completed task and one pending task. Example meaning: the API has been built, while reviewing the requirements is pending. Exact wording varies by model; manually check the counts and task meanings.

Use the generated pending task ID with PUT `/api/tasks/{id}/complete`, then request the summary again. It should now describe two completed tasks and zero pending tasks. GET all tasks to confirm the summary itself did not alter records.

## 5 Run the dedicated Postman collection

Import `docs/postman/TaskFlow-AI.postman_collection.json` and the existing TaskFlow Local environment. Run the nine requests in order on a fresh server. The collection creates two synthetic tasks, requests two model summaries, marks the pending task complete, removes its two tasks, and checks the empty-list response. Its scripts verify HTTP status, response type, and nonempty output; the learner verifies the model's meaning.

The original 24-request collection remains the Tasks 1–5 regression collection. The AI collection is separate because its nonempty-summary requests use the selected provider account.

## 6 Understand empty lists and errors

| Situation | Behaviour |
| --- | --- |
| No tasks | 200 with a direct statement that nothing is pending or complete; no model call |
| One to fifty tasks | Send the current list to the configured model and return its text |
| More than fifty tasks | 413 with a readable limit message; no model call |
| Provider request fails | 502 with a readable configuration or availability message |
| Provider returns blank output | 502 rather than a fabricated summary |

The fifty-task limit is an implementation safeguard, not an assignment rule. The client request timeout is fifteen seconds and client retries are disabled so an error does not trigger several repeated requests. A generated summary can still be inaccurate; stored task records are the reference for review.

Capture the live response and Postman results after configuring your private environment. The assignment remains under learner review; no instructor or NTU Learn submission is made by these implementation steps.

## References

- [Spring AI getting started and compatibility](https://docs.spring.io/spring-ai/reference/getting-started.html)
- [Spring AI ChatClient](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [Spring AI OpenAI configuration](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html)
- [OpenRouter authentication and compatible base URL](https://openrouter.ai/docs/api_reference/authentication)
- [OpenRouter model identifier](https://openrouter.ai/openai/gpt-4o-mini)
