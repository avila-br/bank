package br.com.compass.bank.service;

import br.com.compass.bank.exception.transaction.TransactionException;
import br.com.compass.bank.model.Account;
import br.com.compass.bank.model.AccountType;
import br.com.compass.bank.repository.AccountRepository;
import br.com.compass.bank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * TransactionService handles deposit, withdrawal, and transfer operations for accounts.
 * Provides validation to ensure all transactions are performed securely and accurately.
 */
public class TransactionService {

    /**
     * Deposits a specified amount into the given account.
     *
     * @param to     the account to deposit the funds into.
     * @param amount the amount to be deposited.
     * @throws TransactionException if the account does not exist or the amount is invalid.
     */
    public static void deposit(Account to, BigDecimal amount) {
        // Check if the account exists
        if (AccountService.find(to.getId()).isEmpty())
            throw new TransactionException("The account with ID " + to.getId() + " does not exist.");

        // Validate the deposit amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new TransactionException("Deposit amount must be greater than zero.");

        try {
            // Update account balance and persist changes
            to.setBalance(to.getBalance().add(amount));
            AccountRepository.save(to);

            // Log the deposit in the transaction repository
            TransactionRepository.deposit(to, amount);
        } catch (Exception e) {
            throw new TransactionException("Error during deposit: " + e.getMessage());
        }
    }

    /**
     * Withdraws a specified amount from the given account.
     *
     * @param from   the account to withdraw the funds from.
     * @param amount the amount to be withdrawn.
     * @throws TransactionException if the account does not exist, the amount is invalid, or funds are insufficient.
     */
    public static void withdraw(Account from, BigDecimal amount) {
        // Check if the account exists
        if (AccountService.find(from.getId()).isEmpty())
            throw new TransactionException("The account with ID " + from.getId() + " does not exist.");

        // Validate the withdrawal amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new TransactionException("Withdraw amount must be greater than zero.");

        // Ensure sufficient funds are available
        if (from.getBalance().compareTo(amount) < 0)
            throw new TransactionException("Insufficient funds.");

        try {
            // Update account balance and persist changes
            from.setBalance(from.getBalance().subtract(amount));
            AccountRepository.save(from);

            // Log the withdrawal in the transaction repository
            TransactionRepository.withdraw(from, amount);
        } catch (Exception e) {
            throw new TransactionException("Error during withdraw: " + e.getMessage());
        }
    }

    /**
     * Transfers a specified amount from one account to another.
     *
     * @param from   the source account.
     * @param to     the destination account.
     * @param amount the amount to be transferred.
     * @throws TransactionException if any validation fails during the transfer process.
     */
    public static void transfer(Account from, Account to, BigDecimal amount) {
        // Validate the transfer amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new TransactionException("Transfer amount must be greater than zero.");

        // Ensure the source account is not of type "SAVINGS"
        if (from.getType().equals(AccountType.SAVINGS))
            throw new TransactionException("Savings accounts are not allowed to perform transfers.");

        // Check if the source account has sufficient funds
        if (from.getBalance().compareTo(amount) < 0)
            throw new TransactionException("Insufficient funds. Available balance: " + from.getBalance());

        // Ensure the destination account exists
        if (Objects.isNull(to))
            throw new TransactionException("Destination account does not exist.");

        // Prevent transfers to the same account
        if (from.equals(to))
            throw new TransactionException("Cannot transfer to the same account.");

        // Validate the existence of source and destination accounts
        if (AccountService.find(from.getId()).isEmpty())
            throw new TransactionException("Source account does not exist.");

        if (AccountService.find(to.getId()).isEmpty())
            throw new TransactionException("Destination account does not exist.");

        // Validate CPF rules for transferring between accounts with the same CPF
        if (from.getUser().getCpf().equals(to.getUser().getCpf()))
            if (!(from.getType().equals(AccountType.CHECKING) && to.getType().equals(AccountType.SAVINGS)))
                throw new TransactionException("Sender and receiver cannot have the same CPF unless transferring from a checking account to a savings account.");

        try {
            // Deduct the amount from the source account
            from.setBalance(from.getBalance().subtract(amount));
            AccountRepository.save(from);

            // Add the amount to the destination account
            to.setBalance(to.getBalance().add(amount));
            AccountRepository.save(to);

            // Log the transfer transaction
            TransactionRepository.transfer(from, to, amount);
        } catch (TransactionException e) {
            throw new TransactionException("Error during transfer: " + e.getMessage());
        }
    }

}
