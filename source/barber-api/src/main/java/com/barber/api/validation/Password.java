package com.barber.api.validation;

import com.barber.api.constant.BarberConstant;
import com.barber.api.validation.impl.PasswordValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidation.class)
@Documented
public @interface Password {
    boolean allowNull() default false;

    String pattern() default BarberConstant.PASSWORD_PATTERN;

    String message() default "Password must be at least 6 characters, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
