package com.example.visittracking;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Pavel Zhurenkov
 */
@Getter
@Setter
public class ErrorResponse {

    private String message;
    private long timestamp;
    private Map<String, String> errors;

    public ErrorResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ErrorResponse(String message, Map<String, String> errors) {
        this();
        this.message = message;
        this.errors = errors;
    }
}
