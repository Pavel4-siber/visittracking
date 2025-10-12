package com.example.visittracking.exception.custom;

import com.example.visittracking.exception.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Pavel Zhurenkov
 */

public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String message, int status) {
        super(message, status);
    }
}
