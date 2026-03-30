package com.appvault.validation;

import com.appvault.dto.UserRegistrationDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegistrationDto> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(UserRegistrationDto dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null) return true;
        return dto.getPassword().equals(dto.getConfirmPassword());
    }
}
