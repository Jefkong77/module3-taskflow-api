# Module 3 requirement matrix

Specification: `Module3_Assignment_mandatory (1) (2).docx`. Tasks 1–5 define the mandatory API. Task 6 is the additional Spring AI step.

| Requirement | Source | Implemented behaviour |
| --- | --- | --- |
| Maven, Java 21, Spring Boot, Jar packaging | [pom.xml](../pom.xml) | Java 21 Spring Boot Maven project with executable Jar packaging |
| Group sg.edu.ntu; artifact taskflow-api | [pom.xml](../pom.xml) | Project coordinates match the brief |
| Spring Web, Lombok, DevTools | [pom.xml](../pom.xml) | Required dependencies declared |
| Task 1: four layer folders | [Application package](../src/main/java/sg/edu/ntu/taskflowapi) | model, repository, service, controller |
| Task 2: plain Task POJO with Lombok | [Task.java](../src/main/java/sg/edu/ntu/taskflowapi/model/Task.java) | Long id, String title, boolean completed; Lombok data and constructor annotations; no Spring annotations |
| Task 3: initialized collection and @Repository | [TaskRepository.java](../src/main/java/sg/edu/ntu/taskflowapi/repository/TaskRepository.java) | Initialized HashMap with generated counter IDs |
| Task 4: @Service and constructor injection | [TaskService.java](../src/main/java/sg/edu/ntu/taskflowapi/service/TaskService.java) | Repository supplied through the constructor |
| Task 4: findAllTasks and findTaskById | [TaskService.java](../src/main/java/sg/edu/ntu/taskflowapi/service/TaskService.java) | List tasks and retrieve a task by ID |
| Task 4: createTask and updateTask | [TaskService.java](../src/main/java/sg/edu/ntu/taskflowapi/service/TaskService.java) | Validate input and save task data |
| Task 4: deleteTask | [TaskService.java](../src/main/java/sg/edu/ntu/taskflowapi/service/TaskService.java) | Delete an existing task |
| Task 4: markTaskAsComplete | [TaskService.java](../src/main/java/sg/edu/ntu/taskflowapi/service/TaskService.java) | Find the task, set completed to true, and save |
| Task 5: @RestController and constructor injection | [TaskController.java](../src/main/java/sg/edu/ntu/taskflowapi/controller/TaskController.java) | Service supplied through the constructor; base path /api/tasks |
| Task 5: six routes and success codes | [API contract](../README.md#api-contract) | Exact methods and paths; POST 201 and the other five successes 200 |
| Task 6: Spring AI dependency | [pom.xml](../pom.xml) | Spring AI BOM and OpenAI starter |
| Task 6: injected ChatClient and summary | [TaskSummaryService.java](../src/main/java/sg/edu/ntu/taskflowapi/service/TaskSummaryService.java) | Constructor-injected ChatClient.Builder creates the client; current tasks and counts form the prompt |
| Task 6: GET /api/tasks/summary | [TaskSummaryController.java](../src/main/java/sg/edu/ntu/taskflowapi/controller/TaskSummaryController.java) | Short plain-English response with text/plain content type |
| Task 6: environment-based key | [application.properties](../src/main/resources/application.properties) | Exact reference spring.ai.openai.api-key=${OPENAI_API_KEY}; shell setup in the Task 6 guide |
| Postman testing for every endpoint | [CRUD collection](postman/TaskFlow.postman_collection.json); [AI collection](postman/TaskFlow-AI.postman_collection.json) | Task operations, validation, completion, and summary flow |
| Public repository URL as delivery format | [GitHub repository](https://github.com/Jefkong77/module3-taskflow-api) | Source, documentation, Postman files, and reports in one repository |

The brief specifies the public GitHub repository URL as the item submitted through NTU Learn.

Additional implementation choices include title validation, readable HTTP errors, sorted list responses, defensive copies, and automated tests. [Test results](evidence/verification.md) record the executed checks; [design rationale](document-review.md) explains these choices.
