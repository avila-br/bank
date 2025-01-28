package br.com.compass.bank.service;

import br.com.compass.bank.exception.transaction.TransactionException;
import br.com.compass.bank.model.Account;
import br.com.compass.bank.model.AccountType;
import br.com.compass.bank.repository.AccountRepository;
import br.com.compass.bank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Objects;

public class TransactionService {

    public static void deposit(Account to, BigDecimal amount) {
        if (AccountService.find(to.getId()).isEmpty())
            throw new TransactionException("The account with ID " + to.getId() + " does not exist.");

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new TransactionException("Deposit amount must be greater than zero.");

        try {
            to.setBalance(to.getBalance().add(amount));
            AccountRepository.save(to);
            TransactionRepository.deposit(to, amount);
        } catch (Exception e) {
            throw new TransactionException("Error during deposit: " + e.getMessage());
        }
    }

    public static void withdraw(Account from, BigDecimal amount) {
        if (AccountService.find(from.getId()).isEmpty())
            throw new TransactionException("The account with ID " + from.getId() + " does not exist.");

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new TransactionException("Withdraw amount must be greater than zero.");

        if (from.getBalance().compareTo(amount) < 0)
            throw new TransactionException("Insufficient funds.");

        try {
            from.setBalance(from.getBalance().subtract(amount));
            AccountRepository.save(from);
            TransactionRepository.withdraw(from, amount);
        } catch (Exception e) {
            throw new TransactionException("Error during withdraw: " + e.getMessage());
        }
    }

    public static void transfer(Account from, Account to, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new TransactionException("Transfer amount must be greater than zero.");

        if (from.getType().equals(AccountType.SAVINGS))
            throw new TransactionException("Savings accounts are not allowed to perform transfers.");

        if (from.getBalance().compareTo(amount) < 0)
            throw new TransactionException("Insufficient funds. Available balance: " + from.getBalance());

        if (Objects.isNull(to))
            throw new TransactionException("Destination account does not exist.");

        if (from.equals(to))
            throw new TransactionException("Cannot transfer to the same account.");

        if (AccountService.find(from.getId()).isEmpty())
            throw new TransactionException("Source account does not exist.");

        if (AccountService.find(to.getId()).isEmpty())
            throw new TransactionException("Destination account does not exist.");

        if (from.getUser().getCpf().equals(to.getUser().getCpf()))
            if (!(from.getType().equals(AccountType.CHECKING) && to.getType().equals(AccountType.SAVINGS)))
                throw new TransactionException("Sender and receiver cannot have the same CPF unless transferring from a checking account to a savings account.");

        try {
            // Withdraw from source account
            from.setBalance(from.getBalance().subtract(amount));
            AccountRepository.save(from);

            // Deposit into destination account
            to.setBalance(to.getBalance().add(amount));
            AccountRepository.save(to);

            // Log the transfer transaction (optional step)
            TransactionRepository.transfer(from, to, amount);
        } catch (TransactionException e) {
            throw new TransactionException("Error during transfer: " + e.getMessage());
        }
    }

}
