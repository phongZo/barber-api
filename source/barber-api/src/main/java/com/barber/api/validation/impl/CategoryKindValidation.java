package com.barber.api.validation.impl;

import com.barber.api.constant.BarberConstant;
import com.barber.api.validation.CategoryKind;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CategoryKindValidation implements ConstraintValidator<CategoryKind, Integer> {
  private boolean allowNull;

  @Override
  public void initialize(CategoryKind constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : BarberConstant.CATEGORY_KINDS.contains(value);
  }
}
