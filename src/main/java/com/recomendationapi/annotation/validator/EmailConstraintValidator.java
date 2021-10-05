package com.recomendationapi.annotation.validator;

import com.recomendationapi.annotation.ValidEmail;
import com.recomendationapi.utils.EmailValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class EmailConstraintValidator implements ConstraintValidator<ValidEmail, String> {

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    return isBlank(email) || EmailValidator.validate(email);
  }
}
