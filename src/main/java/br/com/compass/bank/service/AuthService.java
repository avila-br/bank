package br.com.compass.bank.service;

import br.com.compass.bank.internal.DatabaseConnection;
import br.com.compass.bank.model.Account;
import br.com.compass.bank.repository.AccountRepository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {

    public static Optional<Account> register(Account account) {
        account.setPassword(AuthService.hashPassword(account.getPassword()));

        AuthService.formatPhone(account.getUser().getPhone()).ifPresentOrElse (
                phone -> account.getUser().setPhone(phone),
                () -> { throw new IllegalArgumentException("Invalid phone number format."); }
        );

        AuthService.formatCpf(account.getUser().getCpf()).ifPresentOrElse (
                cpf -> account.getUser().setCpf(cpf),
                () -> { throw new IllegalArgumentException("Invalid CPF format."); }
        );

        try (Session session = DatabaseConnection.getFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(account.getUser());
            AccountRepository.save(account);
            transaction.commit();
        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.of(account);
    }

    private static Optional<String> formatPhone(String phone) {
        String cleaned = phone.replaceAll("[^0-9]", "");

        if (cleaned.startsWith("55"))
            cleaned = cleaned.substring(2);

        return (cleaned.length() == 11)
                ? Optional.of(String.format("+55 (%s) %s-%s",
                        cleaned.substring(2, 4),
                        cleaned.substring(4, 9),
                        cleaned.substring(9)
                ))
                : Optional.empty();
    }

    private static Optional<String> formatCpf(String cpf) {
        String cleaned = cpf.replaceAll("[^0-9]", "");

        return (cleaned.length() == 11)
                ? Optional.of(String.format("%s.%s.%s-%s",
                    cleaned.substring(0, 3),
                    cleaned.substring(3, 6),
                    cleaned.substring(6, 9),
                    cleaned.substring(9)
                ))
                : Optional.empty();
    }

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean verifyPassword(String originalPassword, String hashedPassword) {
        return BCrypt.checkpw(originalPassword, hashedPassword);
    }
}
