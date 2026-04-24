package com.barber.api.validation.impl;

import com.barber.api.validation.Date;
import com.barber.api.validation.Hour;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class DateValidation implements ConstraintValidator<Date, String> {
  private boolean allowNull;
  private String pattern;

  @Override
  public void initialize(Date constraintAnnotation) {
    allowNull = constraintAnnotation.allowNull();
    pattern = constraintAnnotation.pattern();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    return StringUtils.isBlank(value) ? allowNull : value.matches(pattern);
  }
}
