package com.recomendationapi.annotation;

import com.recomendationapi.annotation.validator.EmailConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {

  String message() default "validator.invalid.email";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
