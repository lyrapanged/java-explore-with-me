package ru.practicum.explore.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static final Map<String, String> STATUS_BAD_REQUEST = Map.of("status", "BAD_REQUEST",
            "timestamp", LocalDateTime.now().toString());

    private static final Map<String, String> STATUS_CONFLICT = Map.of("status", "CONFLICT",
            "timestamp", LocalDateTime.now().toString());

    private static final Map<String, String> STATUS_NOT_FOUND = Map.of("status", "NOT_FOUND",
            "timestamp", LocalDateTime.now().toString());

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleWrongDateException(final WrongDateException e) {
        log.debug("Error: {}", e.getMessage());
        return STATUS_CONFLICT;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleIllegalStateException(final IllegalStateException e) {
        log.debug("Error: {}", e.getMessage());
        return STATUS_CONFLICT;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.debug("Error: {}", e.getMessage());
        return STATUS_NOT_FOUND;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadValidationException(final BadValidationException e) {
        log.debug("Error: {}", e.getMessage());
        return STATUS_BAD_REQUEST;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.debug("Error: {}", e.getMessage());
        return STATUS_CONFLICT;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleRequestEventException(final RequestEventException e) {
        log.debug("Error: {}", e.getMessage());
        return STATUS_CONFLICT;
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentNotValidException.class,})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.debug("Error: {}", e.getMessage());
        return STATUS_BAD_REQUEST;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(final ConstraintViolationException e) {
        log.debug("Error: {}", e.getMessage());
        return STATUS_BAD_REQUEST;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowableException(Throwable exception) {
        Map<String, String> result = Map.of("Internal Server Error: ", exception.getMessage());
        log.warn(String.valueOf(result), exception);
        return result;
    }

}
