package com.example.visittracking.exception;

/**
 * @author Pavel Zhurenkov
 */
public abstract class ApplicationException extends RuntimeException {
    private final int httpStatus;

    protected ApplicationException(String message, int status) {
        super(message);
        this.httpStatus = status;
    }

    public int getHttpStatus() { return httpStatus; }
}
