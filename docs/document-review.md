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
| Spring AI | Optional better-score step | Omitted from mandatory baseline |
| Submission | Public GitHub URL on NTU Learn | Final account actions remain explicit in checklist |
| EDA | Not requested | Add concise context and synthetic-data analysis |

## What was retained from the guide

The five mandatory implementation classes and service tests were used as an assisted starting point. The repository uses defensive copies, generated IDs, and sorted output. Validation and readable errors support testing. The controller and service use explicit constructors so the dependency flow is visible.

## What needed execution or care

The guide originally described Java examples that had not been compiled in a generated project. This package contains the generated project, executable test procedures, and an execution record in `evidence/verification.md`. That record distinguishes verified checks from remaining actions.

HTTP testing found a version-specific JSON issue: the generated Spring Boot 4.1.2 stack rejected a creation body that omitted the primitive completed flag. The final application sets `spring.jackson.deserialization.fail-on-null-for-primitives=false` to preserve the documented false default. The omission scenario is included in both the Python HTTP checks and the Postman collection. This is why compiling and service tests alone were not sufficient.

Postman procedures in a document are not evidence that requests were sent. The exported collection can be run directly; any Newman execution is labelled as the Postman command-line runner rather than desktop screenshots. The learner should also run and inspect requests in Postman, as the brief directs.

The guide's optional AI example must not be copied into the baseline without compatible dependencies and provider configuration. Omitting it keeps the mandatory project keyless. No optional summary behaviour is claimed.

The sample Git commands must use the actual GitHub owner and repository. Sample attribution to another tool should not be copied unless factually accurate. No GitHub URL should be described as submitted until NTU Learn confirms it.

## EDA boundary

There is no supplied empirical dataset and no requirement for Python notebooks, machine learning, or graphs. The EDA therefore states the business problem, user actions, schema, quality checks, lifecycle, and test coverage. Its six sample records are explicitly synthetic and are not loaded automatically into the API.

## Learner responsibility

The brief explicitly says to write the code yourself using what was learned in class. Use the assisted material for understanding and adaptation, follow Ms. Sanjela's rules, and be ready to explain the complete flow of each request. Automated verification establishes software behaviour; it does not establish independent authorship or replace the required submission.
