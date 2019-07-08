package com.heetch.exception;

public class InvalidDriverIdException extends RuntimeException {
    public InvalidDriverIdException() {
        super();
    }

    public InvalidDriverIdException(String message) {
        super(message);
    }
}
