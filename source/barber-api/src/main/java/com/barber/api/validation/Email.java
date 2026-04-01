package com.barber.api.validation;

import com.barber.api.constant.BarberConstant;
import com.barber.api.validation.impl.EmailValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidation.class)
@Documented
public @interface Email {
  boolean allowNull() default false;

  String pattern() default BarberConstant.EMAIL_PATTERN;

  String message() default "Email invalid, example: test@example.com";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
