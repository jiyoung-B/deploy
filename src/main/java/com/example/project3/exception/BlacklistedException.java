package com.example.project3.exception;

public class BlacklistedException extends RuntimeException {
    public BlacklistedException(String message) {
        super(message);
    }
}
