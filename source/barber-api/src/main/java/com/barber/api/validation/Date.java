package com.barber.api.validation;

import com.barber.api.constant.BarberConstant;
import com.barber.api.validation.impl.DateValidation;
import com.barber.api.validation.impl.HourValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidation.class)
@Documented
public @interface Date {
  boolean allowNull() default false;

  String pattern() default BarberConstant.DATE_PATTERN;

  String message() default "Date invalid, format: dd/MM/yyyy";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
