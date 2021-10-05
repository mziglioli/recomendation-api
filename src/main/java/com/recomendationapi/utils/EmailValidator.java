package com.recomendationapi.utils;

import java.util.regex.Pattern;

public class EmailValidator {

  private EmailValidator() {}

  private static final String EMAIL_PATTERN =
      "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

  public static boolean validate(final String email) {
    return email != null && pattern.matcher(email).matches();
  }
}
