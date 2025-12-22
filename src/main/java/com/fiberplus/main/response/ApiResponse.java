package com.fiberplus.main.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fiberplus.main.enums.ResponseType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiResponse<T> {
    private boolean success;
    private ResponseType type;
    private String message;
    private List<String> errors;
    private T data;
    private LocalDateTime timestamp;

    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
        this.errors = new ArrayList<>();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = true;
        res.type = ResponseType.SUCCESS;
        res.message = message;
        res.data = data;
        return res;
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = false;
        res.type = ResponseType.ERROR;
        res.message = message;
        res.errors = errors;
        return res;
    }

    public static <T> ApiResponse<T> validation(List<String> errors) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = false;
        res.type = ResponseType.VALIDATION;
        res.message = "Errores de validación";
        res.errors = errors;
        return res;
    }

    public static <T> ApiResponse<T> exception(String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = false;
        res.type = ResponseType.EXCEPTION;
        res.message = message;
        return res;
    }
}
