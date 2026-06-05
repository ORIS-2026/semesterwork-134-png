package com.tech.dimefresh.controller.handler;



import com.tech.dimefresh.exception.rest.RestServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleNoResource(NoResourceFoundException e) {
        log.debug("Static resource not found: {}", e.getResourcePath()); // или info, если нужно
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(RestServiceException.class)
    public final ResponseEntity<ExceptionMessage> handleServiceException(RestServiceException exception) {
        log.info(exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus())
                .body(ExceptionMessage.builder()
                        .exceptionName(exception.getClass().getSimpleName())
                        .message(exception.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ExceptionMessage onAllExceptions(Exception exception) {
        log.info("Exception: {}", exception.getMessage());
        return ExceptionMessage.builder()
                .message(exception.getMessage())
                .exceptionName(exception.getClass().getSimpleName())
                .build();
    }
}
