package com.barber.api.validation;

import com.barber.api.validation.impl.AccountStatusValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccountStatusValidation.class)
@Documented
public @interface AccountStatus {
    boolean allowNull() default false;
    String message() default "Account status invalid. Valid values is 1 (active), 0 (pending), -1 (lock), or -2 (delete)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}