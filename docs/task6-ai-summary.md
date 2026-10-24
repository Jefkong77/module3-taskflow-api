# Task 6 Spring AI summary endpoint

GET `/api/tasks/summary` returns a short plain-English account of completed and pending tasks. TaskSummaryController delegates to TaskSummaryService. The service reads the current tasks through TaskService and sends the titles, completion states, and counts to Spring AI ChatClient.

## Source files

| File | Responsibility |
| --- | --- |
| [TaskSummaryController.java](../src/main/java/sg/edu/ntu/taskflowapi/controller/TaskSummaryController.java) | Maps the summary route and returns text/plain |
| [TaskSummaryService.java](../src/main/java/sg/edu/ntu/taskflowapi/service/TaskSummaryService.java) | Builds the injected client, prepares the prompt, and handles results |
| [application.properties](../src/main/resources/application.properties) | Reads provider settings from environment variables |
| [pom.xml](../pom.xml) | Declares Spring AI 2.0.1 and its OpenAI starter |
| [TaskSummaryServiceTest.java](../src/test/java/sg/edu/ntu/taskflowapi/service/TaskSummaryServiceTest.java) | Tests prompts and service behaviour with model doubles |
| [TaskSummaryHttpTest.java](../src/test/java/sg/edu/ntu/taskflowapi/controller/TaskSummaryHttpTest.java) | Tests the Spring AI HTTP integration with a local test server |

## Request flow

The summary route is a literal mapping under `/api/tasks`. The controller calls summarizeTasks. The service obtains a task snapshot through findAllTasks, counts completed and pending tasks, and creates a literal UserMessage containing task IDs, titles, and states.

The constructor receives TaskService and ChatClient.Builder through Spring dependency injection. The system prompt asks for at most three short sentences, completed and pending counts, and a brief description of the work. It treats task titles as data and excludes invented tasks or deadlines. The returned text is trimmed before it is sent to the client. Stored tasks are unchanged.

## Provider configuration

The assignment specifies environment-based configuration in WSL Ubuntu's `~/.bashrc`. The application contains this reference:

```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

Open the shell configuration:

```bash
code ~/.bashrc
```

For OpenAI, use:

```bash
export OPENAI_API_KEY='YOUR_OPENAI_API_KEY'
export OPENAI_BASE_URL='https://api.openai.com/v1'
export OPENAI_MODEL='gpt-4o-mini'
```

For OpenRouter, use:

```bash
export OPENAI_API_KEY='YOUR_OPENROUTER_API_KEY'
export OPENAI_BASE_URL='https://openrouter.ai/api/v1'
export OPENAI_MODEL='openrouter/free'
```

The OpenRouter example uses the [Free Models Router](https://openrouter.ai/docs/guides/routing/routers/free-router), which selects an available free model for the request. Availability, response time, and free-model rate limits can vary.

The key strings above are placeholders. Use the configuration for one provider, with its corresponding credential. Store the credential in the shell configuration outside the project. Update existing exports when changing providers.

Save the file and reload the environment:

```bash
source ~/.bashrc
```

The optional OPENAI_BASE_URL and OPENAI_MODEL variables override the defaults in application.properties. This project uses `spring.ai.openai.chat.model` and a base URL containing `/v1`.

## Build and start

From the project root in the configured WSL Ubuntu terminal:

```bash
mvn clean verify
mvn spring-boot:run
```

The automated tests supply their own controlled test configuration. Runtime requests use the provider settings from the shell environment. The default server address is `http://localhost:8080`.

## Postman collection

Import [TaskFlow-AI.postman_collection.json](postman/TaskFlow-AI.postman_collection.json) and [TaskFlow-Local.postman_environment.json](postman/TaskFlow-Local.postman_environment.json). Select TaskFlow Local, confirm baseUrl matches the server, and run the nine requests in order against a fresh application.

The collection uses two synthetic input records: one pending and one completed. It requests a summary, completes the pending task, requests another summary, checks stored state, deletes both records, and checks the empty-list response.

| Stage | Expected behaviour |
| --- | --- |
| Two records with different completion states | Summary describes one completed and one pending task |
| Pending record marked complete | Summary describes two completed and zero pending tasks |
| GET all after summary | Task records retain their saved values |
| Both test records deleted | Summary reports an empty task list |

Postman scripts check status codes, content type, nonempty output, and stored state. Compare the returned text with the task titles and counts; model wording can vary.

## Manual summary example

Create two synthetic tasks with POST `/api/tasks` using Body, raw, JSON:

```json
{"title":"Prepare presentation slides","completed":false}
```

```json
{"title":"Book meeting room","completed":true}
```

Then send GET `http://localhost:8080/api/tasks/summary`. A successful response has status 200, Content-Type text/plain, and describes one completed task and one pending task. Use GET `/api/tasks` to inspect the source records.

## Empty lists and errors

| Situation | Behaviour |
| --- | --- |
| Empty list | 200 with a fixed factual message |
| One to fifty tasks | Send current task data to the configured model and return its summary |
| More than fifty tasks | 413 with a limit message |
| Failed provider request | 502 with a readable availability/configuration message |
| Blank provider output | 502 with an empty-summary message |

The fifty-task limit, fifteen-second timeout, and disabled retries are implementation safeguards. Provider diagnostics are excluded from HTTP error responses. The stored records provide the factual basis for interpreting generated text.

## Automated verification

Summary unit tests use model doubles. HTTP integration tests and the recorded AI collection run use a local OpenAI-compatible test server with controlled response text. [Task 6 test results](evidence/task6-verification.md) link the reports and captured request data. The same report includes an [interactive OpenRouter verification](evidence/task6-verification.md#live-provider-verification), with the returned summary and task records shown in Postman.

## References

- [Spring AI getting started](https://docs.spring.io/spring-ai/reference/getting-started.html)
- [Spring AI ChatClient](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [Spring AI OpenAI configuration](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html)
- [OpenRouter authentication](https://openrouter.ai/docs/api_reference/authentication)
- [OpenRouter Free Models Router](https://openrouter.ai/docs/guides/routing/routers/free-router)
