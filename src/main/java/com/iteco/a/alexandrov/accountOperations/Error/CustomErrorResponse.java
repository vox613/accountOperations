package com.iteco.a.alexandrov.accountOperations.Error;

public class CustomErrorResponse {
    private String errorMessage;

    public CustomErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
