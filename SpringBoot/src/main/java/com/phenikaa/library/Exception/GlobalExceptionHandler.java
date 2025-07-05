package com.phenikaa.library.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        logger.error("Validation error: {}", ex.getMessage(), ex);
        
        StandardResponse response = new StandardResponse();
        response.setSuccess(false);
        response.setTimestamp(LocalDateTime.now());
        
        Map<String, String> error = new HashMap<>();
        error.put("code", "VALIDATION_ERROR");
        error.put("message", ex.getMessage());
        response.setError(error);
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        logger.warn("Resource not found: {}", ex.getMessage());
        
        StandardResponse response = new StandardResponse();
        response.setSuccess(false);
        response.setTimestamp(LocalDateTime.now());
        
        Map<String, String> error = new HashMap<>();
        error.put("code", "RESOURCE_NOT_FOUND");
        error.put("message", ex.getMessage());
        response.setError(error);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<StandardResponse> handleBusinessLogicException(
            BusinessLogicException ex, WebRequest request) {
        logger.warn("Business logic violation: {}", ex.getMessage());
        
        StandardResponse response = new StandardResponse();
        response.setSuccess(false);
        response.setTimestamp(LocalDateTime.now());
        
        Map<String, String> error = new HashMap<>();
        error.put("code", "BUSINESS_LOGIC_ERROR");
        error.put("message", ex.getMessage());
        response.setError(error);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        logger.error("Runtime error: {}", ex.getMessage(), ex);
        
        StandardResponse response = new StandardResponse();
        response.setSuccess(false);
        response.setTimestamp(LocalDateTime.now());
        
        Map<String, String> error = new HashMap<>();
        error.put("code", "RUNTIME_ERROR");
        error.put("message", ex.getMessage());
        response.setError(error);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        StandardResponse response = new StandardResponse();
        response.setSuccess(false);
        response.setTimestamp(LocalDateTime.now());
        
        Map<String, String> error = new HashMap<>();
        error.put("code", "INTERNAL_SERVER_ERROR");
        error.put("message", "Có lỗi xảy ra trong hệ thống. Vui lòng thử lại sau.");
        response.setError(error);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    public static class StandardResponse {
        private boolean success;
        private LocalDateTime timestamp;
        private Map<String, String> error;
        private Object data;
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Map<String, String> getError() { return error; }
        public void setError(Map<String, String> error) { this.error = error; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}