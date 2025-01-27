package br.com.compass.bank.exception;

public class AccountLoginException extends RuntimeException {
    public AccountLoginException(String message) {
        super(message);
    }
}
