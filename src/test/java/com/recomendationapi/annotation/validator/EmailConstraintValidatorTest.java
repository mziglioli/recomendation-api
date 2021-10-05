package com.recomendationapi.annotation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailConstraintValidatorTest {

  private EmailConstraintValidator validator;

  @BeforeEach
  void init() {
    validator = new EmailConstraintValidator();
  }

  @Test
  @DisplayName("Test pass when valid email")
  void testValid() {
    assertTrue(validator.isValid("test@test.com", null));
  }

  @Test
  @DisplayName("Test pass when empty email")
  void testValid_WhenEmpty() {
    assertTrue(validator.isValid("", null));
  }

  @Test
  @DisplayName("Test pass when empty null")
  void testValid_WhenNull() {
    assertTrue(validator.isValid(null, null));
  }

  @Test
  @DisplayName("Test pass when empty space email")
  void testValid_WhenEmptySpace() {
    assertTrue(validator.isValid(" ", null));
  }

  @Test
  @DisplayName("Test NOT pass when NOT valid email")
  void testNotValid_WhenNotValidEmail() {
    assertFalse(validator.isValid("test.com", null));
  }
}
