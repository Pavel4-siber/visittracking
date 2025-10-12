package com.example.visittracking.exception.custom;

import com.example.visittracking.exception.ApplicationException;

/**
 * @author Pavel Zhurenkov
 */
public class DateTimeNotValidException extends ApplicationException {
    public DateTimeNotValidException(String message, int status) {
        super(message, status);
    }
}
