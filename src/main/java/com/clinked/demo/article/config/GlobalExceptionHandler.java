package com.clinked.demo.article.config;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationException(MethodArgumentNotValidException manve) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages/messages");
        Map<String, List<String>> errorsMap = new HashMap<>();
        errorsMap.put("errors", manve.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .map(resourceBundle::getString)
                .collect(Collectors.toList()));
        return new ResponseEntity<>(errorsMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationException(PropertyReferenceException pre) {
        Map<String, List<String>> errorsMap = new HashMap<>();
        errorsMap.put("errors", List.of(pre.getMessage()));
        return new ResponseEntity<>(errorsMap, HttpStatus.BAD_REQUEST);
    }
}
