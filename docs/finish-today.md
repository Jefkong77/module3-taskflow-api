# TaskFlow local setup, testing, and submission procedure

## Milestones

| Activity | Suggested time | Result |
| --- | --- | --- |
| Review context and understand four layers | 15 minutes | Explain one request from controller to repository |
| Read each source file | 30–45 minutes | Trace the implementation of Tasks 1–6 |
| Build locally in VS Code WSL Ubuntu | 10–20 minutes | Java 21 and successful Maven output |
| Test all routes in Postman | 20–30 minutes | Six task routes and the Task 6 summary route |
| Push and verify public GitHub URL | 10–15 minutes | Public repository containing complete source |
| Submit on NTU Learn | 5–10 minutes | Submission acknowledgement |

Allow extra time for dependency downloads. Follow [Task 6 setup](task6-ai-summary.md) for provider configuration and summary testing.

## 1 Open the project in WSL Ubuntu

The public repository is available at https://github.com/Jefkong77/module3-taskflow-api. In a WSL Ubuntu terminal, clone it and open the project in VS Code:

```bash
mkdir -p ~/projects/module3
cd ~/projects/module3
git clone https://github.com/Jefkong77/module3-taskflow-api.git
cd module3-taskflow-api
code .
java -version
mvn -version
```

Both Java and Maven must report Java 21. Use your installed Maven as in class; the generated Maven Wrapper remains available if the `mvn` command is unavailable. The first build needs internet access for dependencies. If the clone folder already exists, open it and inspect `git status` before pulling changes.

The Explorer should show `pom.xml`, `mvnw`, and `src` at the project root. Open the whole project folder in WSL Ubuntu, separately from the earlier simple-crm project.

## 2 Review the code and build

Use the [README source-code table](../README.md#source-code-read-in-this-order) for direct file links. Read `Task.java`, then `TaskRepository.java`, `TaskService.java`, and `TaskController.java`. Continue with `ApiExceptionHandler.java`, `application.properties`, and the two Task 6 classes. Java application files are under `src/main/java/sg/edu/ntu/taskflowapi`; configuration is under `src/main/resources`.

```bash
mvn clean verify
mvn spring-boot:run
```

The second command keeps running. Leave this terminal open while testing. Configure the private provider environment in `task6-ai-summary.md` before ordinary application startup. These are the installed Maven commands used in class and specified in the brief. If Maven is unavailable, the wrapper alternatives are `./mvnw clean verify` and `./mvnw spring-boot:run`. A fresh `GET http://localhost:8080/api/tasks` should return 200 and `[]`.

If 8080 is already used by another lesson:

```bash
PORT=8081 mvn spring-boot:run
```

Use 8081 in Postman too. Do not stop an unrelated process.

## 3 Test in Postman

1. Open Postman and select Import.
2. Import `docs/postman/TaskFlow.postman_collection.json`.
3. Import `docs/postman/TaskFlow-Local.postman_environment.json` and select TaskFlow Local.
4. Confirm `baseUrl` is `http://localhost:8080`, or your chosen port.
5. Run the collection in order with the local server running. The collection captures generated IDs automatically and cleans up its two tasks.
6. Check every status and test result. Retain the run results. For manual POST/PUT requests choose Body, raw, JSON.
7. Save Postman results for POST 201, list 200, read 200, update 200, complete 200, and DELETE 200. Follow the separate Task 6 guide for the summary requests.
8. Stop and restart the app. GET all should now return an empty array. This proves expected in-memory behaviour.

Example creation body:

```json
{"title":"Test the TaskFlow API","completed":false}
```

The URL is `http://localhost:8080` plus `@RequestMapping("/api/tasks")`, with any method path appended. It is defined in the controller and server configuration, not in Postman itself.

## 4 GitHub publication and later changes

The public repository has been created under Jefkong77 and the source and supporting documents are published at:

https://github.com/Jefkong77/module3-taskflow-api

The repository root contains README, pom.xml, the Maven wrapper, source, tests, EDA, Postman files, and verification evidence. Build output in `target` and downloaded tools are excluded.

To publish local changes from a clone, inspect and commit them using:

```bash
git status --short
git diff
git add .
git diff --cached --stat
git commit -m "Update TaskFlow implementation and test evidence"
git push
```

Commit when there are changes to record. Authenticate through the normal GitHub sign-in flow and keep access tokens outside project files. Use the configured Git author identity for the account.

## 5 Submit to NTU Learn

When ready to submit, open the Module 3 mandatory assignment submission, paste the public GitHub repository URL, submit, and retain the acknowledgement. The required address is the public repository URL.

The repository URL is the required submission. EDA is inside the repository as supporting context for Ms. Sanjela. No external hosting or video is required by the supplied brief.

## Explain the implementation

- Task is data; Lombok generates ordinary Java boilerplate.
- The repository owns the initialized HashMap and generates IDs.
- The service owns validation and calls the repository.
- The controller maps URLs and HTTP methods and calls the service.
- Constructor injection lets Spring supply dependencies and supports direct service tests.
- POST creates a resource and returns 201. The brief specifies 200 for the other successful operations, including DELETE.
- Completion sets true and saves; it does not toggle the flag.
- A restart clears data because no database is used.
