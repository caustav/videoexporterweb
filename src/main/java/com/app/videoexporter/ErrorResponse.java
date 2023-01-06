package com.app.videoexporter;

public class ErrorResponse {
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private int value;
    private String message;

    public ErrorResponse(int value, String message) {
        this.value = value;
        this.message = message;
    }
}
