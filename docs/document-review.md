# Review of the supplied Module 3 documents

## Main conclusion

The mandatory Word brief specifies TaskFlow, an in-memory task API. The supplied Markdown guide correctly puts this assignment ahead of the separate India Opportunity Navigator extension. The guide is useful implementation support; its broader roadmap is not part of the assessed scope.

## Requirements and decisions

| Topic | Mandatory brief | Decision for this package |
| --- | --- | --- |
| Scope | Task management backend | Implement TaskFlow only |
| Stack | Maven Java 21 stable Spring Boot Jar | Generated using Spring Initializr stable 4.1.2 |
| Layers | controller model service repository | Exact package and four required folders |
| Data | Plain Task POJO and Lombok | id title completed only |
| Storage | ArrayList or HashMap and generated IDs | Initialized HashMap and counter |
| Service | Six named operations and constructor injection | Exact six names implemented |
| HTTP | Six specified routes | Exact paths and methods preserved |
| Success codes | 201 create and 200 for the rest | DELETE returns 200 |
| Testing | Test every endpoint in Postman | Runnable Postman collection plus service and HTTP checks |
| Spring AI | Optional better-score step | Task 6 summary endpoint, provider configuration, and automated integration checks |
| Submission | Public GitHub URL on NTU Learn | Public repository URL and submission procedure documented |
| EDA | Not requested | Add concise context and synthetic-data analysis |

## What was retained from the guide

The implementation follows the required model, repository, service, and controller structure. The repository uses defensive copies, generated IDs, and sorted output. Validation and readable errors support testing. The controller and service use explicit constructors so the dependency flow is visible.

## What needed execution or care

The generated project includes executable test procedures and execution records in `evidence/verification.md` and `evidence/task6-verification.md`.

HTTP testing found a version-specific JSON issue: the generated Spring Boot 4.1.2 stack rejected a creation body that omitted the primitive completed flag. The final application sets `spring.jackson.deserialization.fail-on-null-for-primitives=false` to preserve the documented false default. The omission scenario is included in both the Python HTTP checks and the Postman collection. This is why compiling and service tests alone were not sufficient.

The exported collections support interactive Postman runs and automated Newman runs. Saved Newman reports record request counts, assertions, and failures; the setup guide explains importing the same collections into Postman.

Task 6 uses the compatible Spring AI BOM and OpenAI starter selected by Spring Initializr. Its ChatClient reads provider configuration from environment variables. Automated tests use model doubles and a local OpenAI-compatible HTTP test server for repeatable prompt, request, response, and error checks. Provider setup and Postman procedures are documented separately in `task6-ai-summary.md`.

## EDA boundary

There is no supplied empirical dataset and no requirement for Python notebooks, machine learning, or graphs. The EDA therefore states the business problem, user actions, schema, quality checks, lifecycle, and test coverage. Its six sample records are explicitly synthetic and are not loaded automatically into the API.
