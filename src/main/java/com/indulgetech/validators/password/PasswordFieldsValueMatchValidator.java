package com.indulgetech.validators.password;

import org.springframework.beans.BeanWrapperImpl;

import java.util.Objects;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordFieldsValueMatchValidator implements ConstraintValidator<PasswordValueMatch, String> {
    String field;
    String confirmField;
    String message;

    @Override
    public void initialize(PasswordValueMatch constraint) {
        this.field = constraint.field();
        this.confirmField = constraint.confirmField();
        this.message = constraint.message();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        String fieldValue = (String) new BeanWrapperImpl(value).getPropertyValue(field);
        String confirmFieldValue = (String) new BeanWrapperImpl(value).getPropertyValue(confirmField);

        boolean isValid = false;
        if(fieldValue != null)
            isValid = Objects.equals(fieldValue, confirmFieldValue);
        if(!isValid){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field)
                    .addConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(confirmField)
                    .addConstraintViolation();

        }

        return isValid;
    }
}
