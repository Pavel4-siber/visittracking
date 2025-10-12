package com.example.visittracking.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author Pavel Zhurenkov
 */
@Getter
@Setter
@Builder
public class ApiError {
    private int status;
    private String message;
    private Map<String, String> details;
    private LocalDateTime timestamp;
}
