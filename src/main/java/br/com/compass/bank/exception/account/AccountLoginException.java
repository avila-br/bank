package br.com.compass.bank.exception.account;

/**
 * Custom exception class used to handle errors related to account login failures.
 * <p>
 * This exception is thrown when an account login operation fails, for example,
 * when the account is not found or the provided credentials are incorrect.
 * </p>
 */
public class AccountLoginException extends RuntimeException {

    /**
     * Constructor to create a new {@link AccountLoginException} with the specified error message.
     *
     * @param message the detail message to be associated with the exception.
     */
    public AccountLoginException(String message) {
        super(message);
    }
}
