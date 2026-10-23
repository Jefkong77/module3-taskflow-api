# TaskFlow local development and Postman procedures

## Environment

Use JDK 21 and Maven in WSL Ubuntu. The first dependency download requires internet access. Postman sends requests while the Spring Boot server is running.

## Clone and open

From a WSL Ubuntu terminal:

```bash
mkdir -p ~/projects/module3
cd ~/projects/module3
git clone https://github.com/Jefkong77/module3-taskflow-api.git
cd module3-taskflow-api
code .
```

For an existing clone, open its project folder instead. The VS Code Explorer should show pom.xml and src at the root.

Confirm the Java environment:

```bash
java -version
mvn -version
```

Both commands should identify Java 21. Application source is under `src/main/java/sg/edu/ntu/taskflowapi`. Configuration is under `src/main/resources`. The [source-code table](../README.md#source-code-by-assignment-task) links each class using the assignment's task numbers.

## Provider configuration

The application reads OPENAI_API_KEY from the shell environment. The [Task 6 provider configuration](task6-ai-summary.md#provider-configuration) documents the assignment's ~/.bashrc setup and the OpenAI and OpenRouter settings.

## Build and start

From the project root:

```bash
mvn clean verify
mvn spring-boot:run
```

The verify goal runs the automated tests and packages the application. The second command starts the server and keeps the terminal occupied. Leave it running during Postman requests; Ctrl+C stops it.

The generated Maven Wrapper provides the same goals through `./mvnw clean verify` and `./mvnw spring-boot:run` in WSL. In Windows PowerShell the wrapper is `.\mvnw.cmd`.

The default base URL is `http://localhost:8080`. A fresh application returns 200 and an empty array from GET `/api/tasks`.

For another local port:

```bash
PORT=8081 mvn spring-boot:run
```

Set the Postman baseUrl to `http://localhost:8081` for that run.

## Postman collection setup

1. Open Postman and select Import.
2. Import [TaskFlow.postman_collection.json](postman/TaskFlow.postman_collection.json).
3. Import [TaskFlow-Local.postman_environment.json](postman/TaskFlow-Local.postman_environment.json).
4. Select the TaskFlow Local environment and confirm baseUrl matches the running server.
5. Open the collection runner and run requests in order. The collection captures generated IDs and removes its two test records.

The CRUD collection contains 24 requests covering success responses, validation errors, missing IDs, completion, and stored state. The separate [AI collection](postman/TaskFlow-AI.postman_collection.json) contains nine requests; its procedure is in the [Task 6 guide](task6-ai-summary.md#postman-collection).

## Manual request example

Create a request with method POST and URL `http://localhost:8080/api/tasks`. Select Body, then raw, then JSON. Send:

```json
{"title":"Prepare the meeting agenda","completed":false}
```

The response is 201 and includes a generated ID. Use that returned ID for subsequent GET, PUT, completion, and DELETE requests. The [API contract](../README.md#api-contract) lists the paths and response codes.

The full URL combines the server address and port with the controller's `/api/tasks` mapping and any method-specific path.

## Test reports

The collection runner displays request statuses and assertion results. Postman run exports and request/response captures can be saved alongside the task inputs used for a run. [Recorded test results](evidence/verification.md) link the build, HTTP, and Newman reports.

The additional HTTP script runs from a second terminal while a fresh server is running:

```bash
python3 scripts/http_smoke.py
```

It writes `docs/evidence/http-checks.json`. It is a separate HTTP regression tool alongside the Postman collections.
