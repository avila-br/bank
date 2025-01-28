package br.com.compass.bank.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Custom validator for Brazilian phone numbers.
 * Implements the {@link ConstraintValidator} interface to validate fields annotated with {@link PhoneNumber}.
 * <p>
 * The accepted formats for phone numbers include:
 * <ul>
 *     <li>+55 12 912345678</li>
 *     <li>+55 (12) 912345678</li>
 *     <li>+55 (12) 91234-5678</li>
 *     <li>+55 12912345678</li>
 *     <li>+5512912345678</li>
 * </ul>
 * <p>
 * The validation checks for the following:
 * <ul>
 *     <li>The phone number must start with the country code "+55" (Brazil).</li>
 *     <li>Optional spaces, parentheses, and dashes are allowed depending on the format.</li>
 *     <li>The phone number must contain 11 digits, starting with "9" for mobile numbers.</li>
 * </ul>
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    // Regular expression pattern to validate the phone number format
    private static final Pattern PHONE_PATTERN = Pattern.compile (
            "^\\+55 \\d{2} 9\\d{4}\\d{4}$" +          // Format: +55 12 912345678
            "|^\\+55\\d{11}$" +                       // Format: +5512912345678
            "|^\\+55 \\(\\d{2}\\) 9\\d{4}\\d{4}$" +   // Format: +55 (12) 912345678
            "|^\\+55 \\(\\d{2}\\) 9\\d{4}-\\d{4}$" +  // Format: +55 (12) 91234-5678
            "|^55\\d{11}$"                            // Format: 5512912345678 (without "+")
    );

    /**
     * Validates the provided phone number.
     *
     * @param value   The phone number to validate.
     * @param context The validation context (not used in this implementation).
     * @return {@code true} if the phone number matches the defined patterns, {@code false} otherwise.
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Return false if the value is null or empty
        if (value == null || value.isBlank()) {
            return false;
        }

        // Match the phone number against the regex pattern
        return PHONE_PATTERN.matcher(value).matches();
    }
}
