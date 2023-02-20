package ru.practicum.explore.exception;

public class BadValidationException extends RuntimeException {

    public BadValidationException(String message) {
        super(message);
    }
}
