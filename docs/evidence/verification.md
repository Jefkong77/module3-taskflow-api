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

## Test methods and reports

Newman executed the Postman collection and its post-response test scripts. The collection is also available for interactive use in Postman; import instructions are in [the setup guide](../finish-today.md).

Both Maven startup and the executable JAR were checked. The clean build contains the Spring Boot launcher manifest and starts with `java -jar target/taskflow-api-0.0.1-SNAPSHOT.jar`. Its four HTTP checks include the explicit null completion case. Rebuilding creates the JAR in target. The setup procedure uses `mvn spring-boot:run`, as specified in the assignment and practised in class. These verification runs used the generated wrapper to run the same Maven goal.

Public GitHub repository: [Jefkong77/module3-taskflow-api](https://github.com/Jefkong77/module3-taskflow-api). The main branch contains source, tests, EDA, Postman collections, and test reports.

This record documents the Tasks 1–5 baseline. [Task 6 verification](task6-verification.md) records the subsequent 21-test build and Spring AI integration checks.
