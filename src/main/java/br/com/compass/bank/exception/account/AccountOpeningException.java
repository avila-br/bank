package br.com.compass.bank.exception.account;

public class AccountOpeningException extends RuntimeException {
  public AccountOpeningException(String message) {
    super(message);
  }
}
