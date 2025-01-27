package br.com.compass.bank.service;

import br.com.compass.bank.exception.AccountLoginException;
import br.com.compass.bank.exception.AccountOpeningException;
import br.com.compass.bank.model.Account;
import br.com.compass.bank.repository.AccountRepository;

import lombok.Getter;
import lombok.Setter;

public class AuthService {

    public static class Context {
        @Getter @Setter
        private static Account current;
    }

    public static void register(Account account) {
        String hash = AccountService.hashPassword(account.getPassword());
        account.setPassword(hash);

        AccountService.formatPhone(account.getUser().getPhone()).ifPresentOrElse (
                phone -> account.getUser().setPhone(phone),
                () -> { throw new AccountOpeningException("Invalid phone number format."); }
        );

        AccountService.formatCpf(account.getUser().getCpf()).ifPresentOrElse (
                cpf -> account.getUser().setCpf(cpf),
                () -> { throw new AccountOpeningException("Invalid CPF format."); }
        );

        if (!AccountService.findByCpf(account.getUser().getCpf()).isEmpty())
            throw new AccountOpeningException("An account has already been registered with the provided CPF.");

        if (!AccountService.findByPhone(account.getUser().getPhone()).isEmpty())
            throw new AccountOpeningException("An account has already been registered with the provided phone number.");

        try {
            AccountRepository.save(account);
        } catch (Exception e) {
            throw new AccountOpeningException(e.getMessage());
        }
    }

    public static void login(Long id, String password) {
        Account stored = AccountService.find(id)
                .orElseThrow(() -> new AccountLoginException("Account not found."));

        if (!AccountService.verifyPassword(password, stored.getPassword()))
            throw new AccountLoginException("Incorrect password.");

        Context.setCurrent(stored);
    }
}
