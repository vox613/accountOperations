package com.iteco.a.alexandrov.accountOperations.Errors;

import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;

public class CustomErrorResponse {
    private String errorMessage;

    public CustomErrorResponse(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
