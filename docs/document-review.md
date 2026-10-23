# TaskFlow design rationale

## Assignment scope

The Module 3 specification defines a task-management API with a plain Task model, collection-based storage, a service, and six task endpoints. Task 6 adds a Spring AI summary endpoint. The implementation follows these requirements within the package `sg.edu.ntu.taskflowapi`.

## Layers and dependency direction

| Layer | Classes | Responsibility |
| --- | --- | --- |
| Model | Task | Holds task data using Lombok-generated accessors and constructors |
| Repository | TaskRepository | Owns the initialized HashMap, generated IDs, and storage methods |
| Service | TaskService; TaskSummaryService | Implements task operations, validation, and summary generation |
| Controller | TaskController; TaskSummaryController; ApiExceptionHandler | Maps requests, selects response codes, and formats errors |

Controllers receive services through constructors. TaskService receives TaskRepository through its constructor. TaskSummaryService receives TaskService and ChatClient.Builder. Spring supplies these dependencies; the model remains a plain Java object with no Spring annotations.

The application entry point sits above the four layer packages so component scanning covers the controllers, services, and repository. Direct source links are grouped by assignment task in the [README](../README.md#source-code-by-assignment-task).

## Storage decisions

HashMap keys are generated Long IDs. The collection is initialized when the repository is constructed. A counter allocates a new ID for each creation, independently of IDs supplied by clients.

Returning copies prevents changes to returned Java objects from silently changing stored tasks. List responses are sorted by ID. Synchronized repository methods protect individual map operations and ID allocation. Multi-step service operations have application-level behaviour; the collection does not provide database transactions.

An application or DevTools restart creates a fresh repository, empty storage, and a reset counter.

## Input and response decisions

| Situation | Behaviour |
| --- | --- |
| Task creation | 201, saved task, and Location header |
| Other successful task operations | 200, including DELETE as specified in the brief |
| Blank or missing title | 400 |
| Title longer than 200 characters after trimming | 400 |
| Invalid ID or malformed JSON | 400 |
| Unknown positive task ID | 404 |
| Repeated completion | completed remains true |

Title trimming, the title-length limit, and readable errors supplement the required endpoint behaviour. ApiExceptionHandler represents errors using Spring ProblemDetail.

The JSON configuration sets `spring.jackson.deserialization.fail-on-null-for-primitives=false`. This preserves the false default for an omitted or null completed value with the project's Spring Boot/Jackson version. PUT replaces title and completed together.

## Spring AI integration

Spring Boot 4.1.2 and Spring AI 2.0.1 are declared in [pom.xml](../pom.xml). The OpenAI starter supplies ChatClient.Builder. TaskSummaryService builds the client, reads task data through TaskService, and sends the current task list with completed and pending counts.

The provider key, base URL, and model are read from environment variables. The prompt uses a literal UserMessage so braces in task titles remain data. The system instructions request a short summary and treat titles as untrusted input.

Empty lists return a fixed factual message. A fifty-task limit bounds prompt size. The client timeout is fifteen seconds and retries are disabled. Failed provider requests and blank responses produce readable 502 errors. These safeguards are implementation choices.

## Verification approach

Service tests cover business behaviour. HTTP checks exercise routing and JSON binding. Postman collections cover the task API and summary flow. Summary unit tests use model doubles, while integration tests connect the Spring AI HTTP client to a local OpenAI-compatible test server. Reports and results are linked in [test results](evidence/verification.md).

## Sources

Requirements: `Module3_Assignment_mandatory (1) (2).docx`. Framework documentation is listed in the [README references](../README.md#references) and [Task 6 references](task6-ai-summary.md#references).
