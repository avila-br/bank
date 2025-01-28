package br.com.compass.bank.exception.transaction;

/**
 * Custom exception class used to handle errors related to transactions.
 * <p>
 * This exception is thrown when a transaction fails due to various reasons,
 * such as insufficient funds, invalid transaction amount, or any other
 * transaction-specific issues.
 * </p>
 */
public class TransactionException extends RuntimeException {

    /**
     * Constructor to create a new {@link TransactionException} with the specified error message.
     *
     * @param message the detail message to be associated with the exception.
     */
    public TransactionException(String message) {
        super(message);
    }
}
