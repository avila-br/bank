package br.com.compass.bank.model;

/**
 * Enum representing the different types of transactions in the system.
 * <p>
 * This enum is used to classify transactions into one of the following categories:
 * <ul>
 *     <li>{@link #DEPOSIT} - A deposit of money into an account.</li>
 *     <li>{@link #WITHDRAWAL} - A withdrawal of money from an account.</li>
 *     <li>{@link #TRANSFER} - A transfer of money between two accounts.</li>
 * </ul>
 * </p>
 */
public enum TransactionType {
    /**
     * Represents a deposit transaction, where money is added to an account.
     */
    DEPOSIT,

    /**
     * Represents a withdrawal transaction, where money is taken from an account.
     */
    WITHDRAWAL,

    /**
     * Represents a transfer transaction, where money is moved from one account to another.
     */
    TRANSFER
}
