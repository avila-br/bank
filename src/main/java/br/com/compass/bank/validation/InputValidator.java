package br.com.compass.bank.validation;

import java.util.Optional;

/**
 * Enum for validating various types of user inputs such as CPF, name, phone number, and password.
 * Each enum constant implements its own validation logic via the abstract `validate` method.
 * <p>
 * Validation results are returned as an {@link Optional} containing an {@link Exception} if validation fails,
 * or an empty Optional if the input is valid.
 */
public enum InputValidator {

    /**
     * Validator for CPF (Cadastro de Pessoas Físicas - Brazilian Taxpayer Registry).
     * <p>
     * Validation rules:
     * <ul>
     *     <li>Must follow the format "123.456.789-00" or "12345678900".</li>
     *     <li>Cannot be composed of repeated digits.</li>
     * </ul>
     */
    CPF {
        @Override
        public Optional<Exception> validate(String input) {
            if (input == null || !input.matches("([0-9]{3}[.]?[0-9]{3}[.]?[0-9]{3}-[0-9]{2})|([0-9]{11})"))
                return Optional.of(new IllegalArgumentException("Invalid CPF format. Correct format: 123.456.789-00 or 12345678900"));

            for (String invalid : new String[]{"00000000000", "11111111111", "22222222222", "33333333333", "44444444444",
                    "55555555555", "66666666666", "77777777777", "88888888888", "99999999999"})
                if (input.equals(invalid)) return Optional.of(new IllegalArgumentException("CPF cannot be composed of repeated digits."));

            return Optional.empty();
        }
    },

    /**
     * Validator for names.
     * <p>
     * Validation rules:
     * <ul>
     *     <li>Cannot be null or empty.</li>
     *     <li>Must only contain letters and spaces.</li>
     *     <li>Must be between 2 and 50 characters long.</li>
     * </ul>
     */
    NAME {
        @Override
        public Optional<Exception> validate(String input) {
            if (input == null || input.trim().isEmpty())
                return Optional.of(new IllegalArgumentException("Name cannot be empty."));
            if (!input.matches("^[A-Za-zÀ-ÿ\\s]{2,50}$"))
                return Optional.of(new IllegalArgumentException("Name must contain only letters and spaces, and be between 2 and 50 characters."));

            return Optional.empty();
        }
    },

    /**
     * Validator for phone numbers.
     * <p>
     * Validation rules:
     * <ul>
     *     <li>Must follow one of the acceptable formats:
     *         <ul>
     *             <li>+55 (XX) 9XXXX-XXXX</li>
     *             <li>+55XX9XXXXXXXX</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    PHONE {
        @Override
        public Optional<Exception> validate(String input) {
            final String PHONE_PATTERN = "^\\+55 \\(\\d{2}\\) 9\\d{4}\\d{4}$|^\\+55\\d{11}$|^\\+55 \\(\\d{2}\\) 9\\d{4}-\\d{4}$|^55\\d{11}$";

            if (input == null || !input.matches(PHONE_PATTERN))
                return Optional.of(new IllegalArgumentException("Invalid phone number format. Correct formats: +55 (XX) 9XXXX-XXXX, or +55XX9XXXXXXXX"));

            return Optional.empty();
        }
    },

    /**
     * Validator for passwords.
     * <p>
     * Validation rules:
     * <ul>
     *     <li>Must be at least 8 characters long.</li>
     *     <li>Must include at least one letter and one number.</li>
     * </ul>
     */
    PASSWORD {
        @Override
        public Optional<Exception> validate(String input) {
            if (input == null || input.length() < 8)
                return Optional.of(new IllegalArgumentException("Password must be at least 8 characters long."));
            if (!input.matches(".*[a-zA-Z].*") || !input.matches(".*\\d.*"))
                return Optional.of(new IllegalArgumentException("Password must include both letters and numbers."));

            return Optional.empty();
        }
    };

    /**
     * Abstract method for validating input values.
     *
     * @param input The input value to validate.
     * @return An {@link Optional} containing an {@link Exception} if validation fails, or an empty Optional if valid.
     */
    public abstract Optional<Exception> validate(String input);
}
