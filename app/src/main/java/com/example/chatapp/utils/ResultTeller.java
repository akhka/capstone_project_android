package com.example.chatapp.utils;

public class ResultTeller {

    private String message;
    private boolean success;

    public ResultTeller(boolean success, String message){
        this.success = success;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
