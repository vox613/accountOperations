package com.iteco.a.alexandrov.accountOperations.Exceptions;

import org.springframework.http.HttpStatus;

public class MyWalletException extends Exception {
    private HttpStatus httpStatus;

    public MyWalletException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public MyWalletException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public MyWalletException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public MyWalletException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
