package br.com.compass.bank.service;

import br.com.compass.bank.exception.account.AccountLoginException;
import br.com.compass.bank.exception.account.AccountOpeningException;
import br.com.compass.bank.model.Account;
import br.com.compass.bank.repository.AccountRepository;

import lombok.Getter;
import lombok.Setter;

/**
 * AuthService provides authentication and registration services for accounts.
 * It includes functionalities for registering new accounts and logging into existing ones.
 */
public class AuthService {

    /**
     * Context class manages the current authenticated account.
     * It allows storing and retrieving the currently logged-in account globally.
     */
    public static class Context {
        @Getter @Setter
        private static Account current;
    }

    /**
     * Registers a new account after performing validations and formatting.
     *
     * @param account the account to be registered.
     * @throws AccountOpeningException if any validation or registration process fails.
     */
    public static void register(Account account) {
        // Hashes the password before storing it in the database
        String hash = AccountService.hashPassword(account.getPassword());
        account.setPassword(hash);

        // Formats and validates the phone number
        AccountService.formatPhone(account.getUser().getPhone()).ifPresentOrElse (
                phone -> account.getUser().setPhone(phone),
                () -> { throw new AccountOpeningException("Invalid phone number format."); }
        );

        // Formats and validates the CPF
        AccountService.formatCpf(account.getUser().getCpf()).ifPresentOrElse (
                cpf -> account.getUser().setCpf(cpf),
                () -> { throw new AccountOpeningException("Invalid CPF format."); }
        );

        // Checks if an account with the same CPF already exists
        if (!AccountService.findByCpf(account.getUser().getCpf()).isEmpty())
            throw new AccountOpeningException("An account has already been registered with the provided CPF.");

        // Checks if an account with the same phone number already exists
        if (!AccountService.findByPhone(account.getUser().getPhone()).isEmpty())
            throw new AccountOpeningException("An account has already been registered with the provided phone number.");

        // Saves the account in the repository, handling any exceptions
        try {
            AccountRepository.save(account);
        } catch (Exception e) {
            throw new AccountOpeningException(e.getMessage());
        }
    }

    /**
     * Authenticates an account using its ID and password.
     *
     * @param id       the ID of the account to log into.
     * @param password the plain text password provided by the user.
     * @throws AccountLoginException if the account does not exist or if the password is incorrect.
     */
    public static void login(Long id, String password) {
        // Retrieves the account by ID, throws an exception if not found
        Account stored = AccountService.find(id)
                .orElseThrow(() -> new AccountLoginException("Account not found."));

        // Verifies the password, throws an exception if incorrect
        if (!AccountService.verifyPassword(password, stored.getPassword()))
            throw new AccountLoginException("Incorrect password.");

        // Sets the current authenticated account in the context
        Context.setCurrent(stored);
    }
}
