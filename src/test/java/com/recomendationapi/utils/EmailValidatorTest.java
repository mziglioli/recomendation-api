package com.recomendationapi.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailValidatorTest {

  @Test
  @DisplayName("Email valid")
  void testValid() {
    assertTrue(EmailValidator.validate("test@test.com"));
  }

  @Test
  @DisplayName("Email not valid when null")
  void testNotValid_WhenNull() {
    assertFalse(EmailValidator.validate(null));
  }

  @Test
  @DisplayName("Email not valid when empty")
  void testNotValid_WhenEmpty() {
    assertFalse(EmailValidator.validate(""));
  }

  @Test
  @DisplayName("Email not valid when empty space")
  void testNotValid_WhenEmptySpace() {
    assertFalse(EmailValidator.validate(" "));
  }
}
