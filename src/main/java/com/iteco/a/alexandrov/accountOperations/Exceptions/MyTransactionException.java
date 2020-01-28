package com.iteco.a.alexandrov.accountOperations.Exceptions;

import org.springframework.http.HttpStatus;

public class MyTransactionException extends Exception {
    private HttpStatus httpStatus;

    public MyTransactionException() {
        super();
    }

    public MyTransactionException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public MyTransactionException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public MyTransactionException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public MyTransactionException(Throwable cause, HttpStatus httpStatus) {
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
