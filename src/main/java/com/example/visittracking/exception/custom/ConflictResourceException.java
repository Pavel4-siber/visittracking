package com.example.visittracking.exception.custom;

import com.example.visittracking.exception.ApplicationException;

/**
 * @author Pavel Zhurenkov
 */
public class ConflictResourceException extends ApplicationException {
    public ConflictResourceException(String message, int status) {
        super(message, status);
    }
}
