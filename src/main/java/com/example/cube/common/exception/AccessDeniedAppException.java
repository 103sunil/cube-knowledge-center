package com.example.cube.common.exception;

public class AccessDeniedAppException extends RuntimeException {
    public AccessDeniedAppException(String message) {
        super(message);
    }
}
