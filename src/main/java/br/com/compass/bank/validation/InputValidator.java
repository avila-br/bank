package br.com.compass.bank.validation;

import java.util.Optional;

public enum InputValidator {
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
    PHONE {
        @Override
        public Optional<Exception> validate(String input) {
            final String PHONE_PATTERN = "^\\+55 \\(\\d{2}\\) 9\\d{4}\\d{4}$|^\\+55\\d{11}$|^\\+55 \\(\\d{2}\\) 9\\d{4}-\\d{4}$|^55\\d{11}$";

            if (input == null || !input.matches(PHONE_PATTERN))
                return Optional.of(new IllegalArgumentException("Invalid phone number format. Correct formats: +55 XX 9XXXX-XXXX, +55(XX)9XXXX-XXXX, or 55XXXXXXXXXXX"));

            return Optional.empty();
        }
    },
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

    public abstract Optional<Exception> validate(String input);
}
