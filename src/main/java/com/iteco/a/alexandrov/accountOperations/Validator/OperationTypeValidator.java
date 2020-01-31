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
        return Arrays.stream(AvailableOperations.values())
                .anyMatch(x -> x.getValue().equals(transactionType.toLowerCase()));
    }
}
