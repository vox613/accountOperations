package com.iteco.a.alexandrov.accountOperations.Exceptions.CustomResponse;

public class CustomErrorResponse {
    private String message;

    public CustomErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
