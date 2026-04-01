package com.barber.api.validation;

import com.barber.api.validation.impl.NationKindValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NationKindValidation.class)
@Documented
public @interface NationKind {
  boolean allowNull() default false;

  String message() default "Nation kind invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
