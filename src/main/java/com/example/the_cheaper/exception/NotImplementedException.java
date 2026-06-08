package com.example.the_cheaper.exception;

public class NotImplementedException extends RuntimeException {
    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException() {
        super("Chức năng này chưa được triển khai");
    }
}
