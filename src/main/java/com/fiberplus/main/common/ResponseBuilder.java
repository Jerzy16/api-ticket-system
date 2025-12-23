package com.fiberplus.main.common;

import java.util.List;

import org.springframework.http.ResponseEntity;

public class ResponseBuilder {

    public static <T> ResponseEntity<ApiResponse<T>> build(
            int status, String message, T data
    ) {
        return ResponseEntity
                .status(status)
                .body(new ApiResponse<>(status, message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return build(200, message, data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return build(201, message, data);
    }

    public static ResponseEntity<ApiResponse<Void>> deleted(String message) {
        return build(200, message, null);
    }
    
    public static ResponseEntity<ApiResponse<Void>> error(
            int status, String message, List<String> errors
    ) {
        ApiResponse<Void> response = new ApiResponse<>(status, message, null);
        response.setErrors(errors);
        return ResponseEntity.status(status).body(response);
    }
}
