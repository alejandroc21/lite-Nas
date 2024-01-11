package com.alejandroct.nas.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DirectoryNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDirectorynotFoundException(RuntimeException e){
        Map<String, String> response = new HashMap<>();
        response.put("ERROR", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
}
