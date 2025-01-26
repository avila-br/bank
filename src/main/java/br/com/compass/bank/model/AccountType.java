package br.com.compass.bank.model;

import java.util.Optional;

/**
 * Enum representing the type of account.
 */
public enum AccountType {
    CHECKING, // Representing a checking account
    SAVINGS,  // Representing a savings account
    BUSINESS;  // Representing a business account

    /**
     * Returns an AccountType based on the given string.
     *
     * @param type String representing the account type (case-insensitive).
     * @return AccountType corresponding to the string.
     * @throws IllegalArgumentException if the string does not match any account type.
     */
    public static Optional<AccountType> from(String type) {
        if (type == null) {
            return Optional.empty();
        }

        // Convert the string to lower case and check for the corresponding AccountType
        return switch (type.trim().toLowerCase()) {
            case "checking", "corrente" -> Optional.of(CHECKING);
            case "savings", "poupança" -> Optional.of(SAVINGS);
            case "business", "jurídica" -> Optional.of(BUSINESS);
            default -> Optional.empty();
        };
    }
}
