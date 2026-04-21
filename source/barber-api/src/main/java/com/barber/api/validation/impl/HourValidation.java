package com.barber.api.validation.impl;

import com.barber.api.validation.Hour;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class HourValidation implements ConstraintValidator<Hour, String> {
  private boolean allowNull;
  private String pattern;

  @Override
  public void initialize(Hour constraintAnnotation) {
    allowNull = constraintAnnotation.allowNull();
    pattern = constraintAnnotation.pattern();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    return StringUtils.isBlank(value) ? allowNull : value.matches(pattern);
  }
}
