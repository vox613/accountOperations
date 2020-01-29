package com.iteco.a.alexandrov.accountOperations.Exceptions;

import com.iteco.a.alexandrov.accountOperations.Exceptions.CustomResponse.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {MyWalletException.class})
    public ResponseEntity<CustomErrorResponse> handleWalletException(MyWalletException ex) {
        logger.error(ex.toString());
        HttpStatus status = ex.getHttpStatus();
        CustomErrorResponse errorDetails = new CustomErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorDetails, status);
    }



    @ExceptionHandler(value = {MyTransactionException.class})
    public ResponseEntity<CustomErrorResponse> handleTransactionalException(MyTransactionException ex) {
        logger.error(ex.toString());
        HttpStatus status = ex.getHttpStatus();
        CustomErrorResponse errorDetails = new CustomErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorDetails, status);
    }


}


