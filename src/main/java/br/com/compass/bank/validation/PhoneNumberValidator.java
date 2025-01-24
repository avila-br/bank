package br.com.compass.bank.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile (
            "^\\+55 \\d{2} 9\\d{4}\\d{4}$|^\\+55\\d{11}$|^\\+55 \\(\\d{2}\\) 9\\d{4}\\d{4}$|^\\+55 \\(\\d{2}\\) 9\\d{4}-\\d{4}$|^55\\d{11}$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return PHONE_PATTERN.matcher(value).matches();
    }
}
