package br.com.compass.bank.exception.transaction;

public class TransactionException extends RuntimeException {
    public TransactionException(String message) {
        super(message);
    }
}
