package com.connectx.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, Object> handleUserNotFoundException(UserNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", ex.getMessage());

        return response;
    }

    @ExceptionHandler(DuplicateUserException.class)
    public Map<String, Object> handleDuplicateUserException(DuplicateUserException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", ex.getMessage());

        return response;
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public Map<String, Object> handleGroupNotFoundException(GroupNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", ex.getMessage());

        return response;
    }

    @ExceptionHandler(DuplicateGroupException.class)
    public Map<String, Object> handleDuplicateGroupException(DuplicateGroupException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", ex.getMessage());

        return response;
    }

    @ExceptionHandler(GroupMemberAlreadyExistsException.class)
    public Map<String, Object> handleGroupMemberAlreadyExistsException(GroupMemberAlreadyExistsException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", ex.getMessage());

        return response;
    }

    @ExceptionHandler(GroupMemberNotFoundException.class)
    public Map<String, Object> handleGroupMemberNotFoundException(GroupMemberNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", ex.getMessage());

        return response;
    }

    @ExceptionHandler(UnauthorizedGroupActionException.class)
    public Map<String, Object> handleUnauthorizedGroupActionException(UnauthorizedGroupActionException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", ex.getMessage());

        return response;
    }

    @ExceptionHandler(MessageNotFoundException.class)
    public Map<String, Object> handleMessageNotFoundException(MessageNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", ex.getMessage());

        return response;
    }

    @ExceptionHandler(InvalidMessageException.class)
    public Map<String, Object> handleInvalidMessageException(InvalidMessageException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", ex.getMessage());

        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));

        return errors;
    }
}