package com.heetch.exception;

public class InvalidLocationException extends RuntimeException {
    public InvalidLocationException() {
        super();
    }

    public InvalidLocationException(String message) {
        super(message);
    }
}
