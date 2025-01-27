package br.com.compass.bank.exception;

public class AccountOpeningException extends RuntimeException {
  public AccountOpeningException(String message) {
    super(message);
  }
}
