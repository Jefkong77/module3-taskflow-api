package sg.edu.ntu.taskflowapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleStatus(
            ResponseStatusException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                exception.getStatusCode(), exception.getReason());
        return ResponseEntity.status(exception.getStatusCode()).body(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadableBody(
            HttpMessageNotReadableException exception) {
        return badRequest("Request body must contain valid task JSON");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPathValue(
            MethodArgumentTypeMismatchException exception) {
        return badRequest("Task ID must be a positive integer");
    }

    private ResponseEntity<ProblemDetail> badRequest(String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, detail);
        return ResponseEntity.badRequest().body(problem);
    }
}
