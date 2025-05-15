package com.example.englishmaster_be.common.validator;

import com.example.englishmaster_be.common.annotation.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import java.lang.reflect.Field;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordValidator implements ConstraintValidator<PasswordMatch, Object> {

    String passwordFieldName;

    String confirmPasswordFieldName;

    String message;


    @Override
    public void initialize(PasswordMatch passwordMatch) {

        passwordFieldName = passwordMatch.passwordFieldName();
        confirmPasswordFieldName = passwordMatch.confirmPasswordFieldName();
        message = passwordMatch.message();
    }

    @SneakyThrows
    @Override
    public boolean isValid(Object objectClass, ConstraintValidatorContext constraintValidatorContext) {

        Class<?> clazz = objectClass.getClass();

        Field passwordField = clazz.getDeclaredField(this.passwordFieldName);
        Field confirmPasswordField = clazz.getDeclaredField(this.confirmPasswordFieldName);

        if(!passwordField.getType().equals(String.class) || !confirmPasswordField.getType().equals(String.class))
            throw new RuntimeException("Fields must be type of string");

        passwordField.setAccessible(true);
        confirmPasswordField.setAccessible(true);

        Object passwordFieldValue = passwordField.get(objectClass);
        Object confirmPasswordFieldValue = confirmPasswordField.get(objectClass);

        if(passwordFieldValue == null || confirmPasswordFieldValue == null)
            return true;

        boolean valid = passwordFieldValue.equals(confirmPasswordFieldValue);

        if(!valid){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(this.confirmPasswordFieldName)
                    .addConstraintViolation();
        }

        return valid;
    }
}
