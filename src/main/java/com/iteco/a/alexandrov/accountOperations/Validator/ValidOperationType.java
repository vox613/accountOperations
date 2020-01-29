package com.iteco.a.alexandrov.accountOperations.Validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, LOCAL_VARIABLE})
@Retention(RUNTIME)
@Constraint(validatedBy = OperationTypeValidator.class)
public @interface ValidOperationType {
    String message() default "Invalid operationType";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}