package com.example.project_security.exception;

/**
 * Eccezione per operazioni non autorizzate (403)
 */
class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
