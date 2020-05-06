package com.example.taskernative.utils.exceptions;

public class StatusCodeException extends Exception {
    public StatusCodeException(){
    }
    public StatusCodeException(String message) {
        super(message);
    }
    public StatusCodeException(String message, Throwable cause) {
        super(message, cause);
    }

}
