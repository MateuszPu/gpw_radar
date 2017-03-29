package com.gpw.radar.web.rest.errors.exceptions;

public class ResourceNotExistException extends RuntimeException {

    public ResourceNotExistException() {
        super("resource not found");
    }

    public ResourceNotExistException(String message) {
        super(message);
    }
}
