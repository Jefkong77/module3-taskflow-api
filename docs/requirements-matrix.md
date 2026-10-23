# Mandatory requirement matrix

The Word assignment is the authority. Supporting safeguards are labelled separately.

| Brief item | Implementation or evidence | Completion gate |
| --- | --- | --- |
| Maven Java 21 stable Spring Boot Jar | pom.xml and generated wrapper | Run ./mvnw -version and ./mvnw clean verify |
| Group sg.edu.ntu artifact taskflow-api | pom.xml | Inspect generated metadata |
| Spring Web Lombok DevTools | pom.xml | Inspect dependencies and compile |
| Task 1 four folders | src/main/java/sg/edu/ntu/taskflowapi/{controller,model,service,repository} | Source inspection |
| Task 2 plain POJO minimum three fields and Lombok | model/Task.java | No Spring annotations; constructor and JSON checks |
| Task 3 initialized ArrayList or HashMap | repository/TaskRepository.java | HashMap initialized at field declaration |
| Task 3 own IDs | repository/TaskRepository.java | Counter; two creates give distinct generated IDs |
| Task 3 @Repository | repository/TaskRepository.java | Annotation present |
| Task 4 @Service and constructor injection | service/TaskService.java | Constructor dependency; unit and context tests |
| Task 4 findAllTasks and findTaskById | service/TaskService.java | Service and HTTP checks |
| Task 4 createTask and updateTask | service/TaskService.java | Service and HTTP checks |
| Task 4 deleteTask | service/TaskService.java | Service and HTTP checks |
| Task 4 markTaskAsComplete sets true and saves | service/TaskService.java | Complete twice and read saved state |
| Task 5 @RestController and /api/tasks | controller/TaskController.java | Six route checks |
| Task 5 constructor injection | controller/TaskController.java | Service constructor dependency |
| Task 5 POST 201 all other successful routes 200 | controller/TaskController.java | Includes DELETE 200 |
| Test every endpoint in Postman | docs/postman/TaskFlow.postman_collection.json | Import and run in Postman; record result |
| Public GitHub repository | docs/finish-today.md | Verify public URL after push |
| Submit URL to NTU Learn | docs/finish-today.md | Save submission acknowledgement |
| Task 6 Spring AI summary | TaskSummaryController, TaskSummaryService, Spring AI starter and environment-key reference | Code and local-provider contract verified; live response pending |

Additional choices: title validation, readable 400/404 errors, deterministic ordering, defensive copies, and automated tests. These support the assignment without changing its model, in-memory storage, or required success codes.
