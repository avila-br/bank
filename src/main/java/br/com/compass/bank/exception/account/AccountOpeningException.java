package br.com.compass.bank.exception.account;

/**
 * Custom exception class used to handle errors related to account opening failures.
 * <p>
 * This exception is thrown when an account opening operation fails, for example,
 * when invalid data is provided or if an account with the same details already exists.
 * </p>
 */
public class AccountOpeningException extends RuntimeException {

  /**
   * Constructor to create a new {@link AccountOpeningException} with the specified error message.
   *
   * @param message the detail message to be associated with the exception.
   */
  public AccountOpeningException(String message) {
    super(message);
  }
}
