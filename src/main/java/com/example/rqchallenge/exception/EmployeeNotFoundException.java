package com.example.rqchallenge;

public class EmployeeNotFoundException extends RuntimeException{
    private static final long serialVersionUID = -1091326937421997753L;

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
