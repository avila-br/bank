package br.com.compass.bank.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for validating Brazilian phone numbers.
 * <p>
 * This annotation can be applied to fields, methods, or parameters to validate their values using
 * the {@link PhoneNumberValidator} class.
 * <p>
 * It is integrated with the Bean Validation API and allows for customizable validation messages,
 * grouping, and payloads.
 */
@Constraint(validatedBy = PhoneNumberValidator.class) // Specifies the validator class to use
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER}) // Can be used on fields, methods, or parameters
@Retention(RetentionPolicy.RUNTIME) // Retained at runtime for validation purposes
public @interface PhoneNumber {

    /**
     * The default error message returned when validation fails.
     *
     * @return A string containing the error message.
     */
    String message() default "Invalid phone number format.";

    /**
     * Allows specifying validation groups for more complex validation scenarios.
     *
     * @return An array of classes representing validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Used to carry additional metadata about the validation constraint.
     *
     * @return An array of {@link Payload} subclasses.
     */
    Class<? extends Payload>[] payload() default {};
}
