package br.com.compass.bank.exception.account;

public class AccountLoginException extends RuntimeException {
    public AccountLoginException(String message) {
        super(message);
    }
}
