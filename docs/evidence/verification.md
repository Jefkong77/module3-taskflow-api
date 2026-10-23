# TaskFlow verification record

Execution date: 23 October 2026, Singapore time.

## Actual results

| Check | Result | Evidence |
| --- | --- | --- |
| Java compiler and runtime | Java 21.0.13 Temurin | Maven build and startup logs |
| Spring Boot | 4.1.2 stable generated project | pom.xml and runtime banner |
| Maven | 3.10.0 wrapper | Generated wrapper configuration |
| Service tests | 10 passed | maven-verify.txt and surefire summaries |
| Generated Spring context test | 1 passed | maven-verify.txt and surefire summaries |
| Maven clean verify | Passed 11 tests with no failures errors or skips | maven-verify.txt |
| Running HTTP API | 25 requests and response assertions passed | http-checks.json |
| Postman collection via Newman | 24 requests and 38 assertions passed with no failures | postman-newman.json and postman-newman.txt |
| Restart behaviour | A stored task became an empty list after process restart | restart-check.json |
| Executable JAR startup | Four HTTP checks passed including explicit null completion | jar-checks.json |
| Mandatory route success codes | POST 201 and all five other successful routes 200 | HTTP and Postman reports |
| Negative requests | 400 invalid input and 404 unknown IDs | HTTP and Postman reports |

The server was started with the assignment's Maven goal through the generated wrapper. Port 18080 was used for isolated verification; the supplied default and Postman environment use 8080. The reports correctly retain the actual test port. Change only baseUrl if using a different local port.

## Version compatibility correction

The first live HTTP run detected that omitting completed was rejected by the current Jackson configuration, despite the Java field being a primitive boolean. The final configuration explicitly treats omitted or null primitive values as their Java defaults. The corrected application passed the complete HTTP and Postman runs. No service test result was used as a substitute for checking HTTP JSON binding.

## Verification boundaries

Newman executed the actual Postman collection and its post-response test scripts. No desktop Postman session or Postman screenshots are claimed. The learner should import and inspect the same collection in Postman to follow the brief's testing instruction directly.

Both Maven startup and the final executable JAR were checked. The final clean build contains the Spring Boot launcher manifest and starts with `java -jar target/taskflow-api-0.0.1-SNAPSHOT.jar`. Its four HTTP checks include the explicit null completion case. Build output is omitted from the source submission ZIP; rebuilding creates the JAR. The primary learner procedure remains `./mvnw spring-boot:run`, as directed by the assignment.

Public GitHub repository: https://github.com/Jefkong77/module3-taskflow-api. Repository creation was completed after the user approved browser sign-in. The source, tests, EDA, Postman collection, and evidence are published on main. Publication is separate from the NTU Learn submission.

NTU Learn was not accessed and no submission acknowledgement was obtained. The assignment is not submitted yet. Finish the account actions in `../finish-today.md` after reviewing and adapting the implementation.

Optional Spring AI was not implemented or tested. No provider key or external model request was used.

## Final learner checklist

- [ ] Review and adapt the implementation and explain its four layers.
- [ ] Run the local build under Java 21 in VS Code WSL Ubuntu.
- [ ] Import and test the collection in desktop Postman and retain evidence.
- [x] Create the public repository and publish the prepared source and documentation.
- [x] Verify public visibility and anonymous access to the published repository.
- [ ] Submit its URL through NTU Learn and retain the acknowledgement.
