package com.barber.api.validation;

import com.barber.api.constant.BarberConstant;
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
@Constraint(validatedBy = HourValidation.class)
@Documented
public @interface Hour {
  boolean allowNull() default false;

  String pattern() default BarberConstant.HOUR_PATTERN;

  String message() default "Hour invalid, format: HH:mm";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
