package sc.snicky.springbootjwtauth.api.v1.admin.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sc.snicky.springbootjwtauth.api.v1.admin.dtos.responses.ErrorResponse;
import sc.snicky.springbootjwtauth.api.v1.admin.exceptions.ConflictException;
import sc.snicky.springbootjwtauth.api.v1.admin.exceptions.NotFoundException;

@ControllerAdvice(basePackages = "sc.snicky.springbootjwtauth.api.v1.admin.controllers")
public class AdminExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        "The requested resource was not found.",
                        ex.getMessage(),
                        404,
                        java.time.Instant.now()
                ));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "A conflict occurred with the current state of the resource.",
                        ex.getMessage(),
                        409,
                        java.time.Instant.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "An unexpected error occurred.",
                        "",
                        500,
                        java.time.Instant.now()
                ));
    }
}
