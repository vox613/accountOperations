package com.iteco.a.alexandrov.accountOperations.Validator;


import com.iteco.a.alexandrov.accountOperations.Enum.AvailableOperations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class OperationTypeValidator implements ConstraintValidator<ValidOperationType, String> {

    @Override
    public void initialize(ValidOperationType constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return checkCorrectTransactionOperation(value);
    }


    private boolean checkCorrectTransactionOperation(String transactionType) {
        if (Arrays.stream(AvailableOperations.values()).noneMatch(x -> x.getValue().equals(transactionType.toLowerCase()))) {
//            throw new MyTransactionException("Uncorrected operation!", HttpStatus.UNPROCESSABLE_ENTITY);
            return false;
        }
        return true;
    }
}
