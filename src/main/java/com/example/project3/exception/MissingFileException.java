package com.example.project3.exception;

public class MissingFileException extends RuntimeException{

    public MissingFileException(String message) {
        super(message);
    }
}