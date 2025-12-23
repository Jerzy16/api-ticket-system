package com.fiberplus.main.common;

import java.util.List;

public class ApiResponse<T> {

    private int status;
    private String message;
    private String type;
    private T data;
    private List<String> errors;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.type = resolveType(status);
    }

    private String resolveType(int status) {
        if (status >= 200 && status < 300) return "success";
        if (status >= 400 && status < 500) return "error";
        return "exception";
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public T getData() { return data; }
    public List<String> getErrors() { return errors; }
}
