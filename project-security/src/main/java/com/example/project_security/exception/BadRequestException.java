package com.example.project_security.exception;

/**
 * Eccezione per richieste non valide (400)
 */
class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}