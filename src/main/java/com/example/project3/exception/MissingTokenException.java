package com.example.project3.exception;

public class MissingTokenException extends RuntimeException{

    public MissingTokenException(String message) {
        super(message);
    }
}
