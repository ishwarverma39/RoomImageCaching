package com.livtech.mobileirontest.network;

public class ApiResponse<T> {
    private int status;
    private String message;
    private Object errors;
    private T data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getErrors() {
        return errors;
    }

    public T getData() {
        return data;
    }
}
