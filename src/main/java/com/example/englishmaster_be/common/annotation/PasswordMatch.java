package com.example.englishmaster_be.common.annotation;


import com.example.englishmaster_be.common.constraint.PasswordConstraint;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = PasswordConstraint.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PasswordMatch {

    String message();

    String passwordFieldName();

    String confirmPasswordFieldName();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
