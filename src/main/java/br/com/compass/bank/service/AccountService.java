package br.com.compass.bank.service;

import br.com.compass.bank.model.Account;
import br.com.compass.bank.repository.AccountRepository;
import br.com.compass.bank.repository.UserRepository;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The AccountService class provides methods to interact with accounts and perform various operations such as:
 * - Finding accounts by ID, CPF, or phone number.
 * - Formatting CPF and phone numbers into standard formats.
 * - Hashing and verifying passwords using BCrypt.
 */
public class AccountService {

    /**
     * Finds an account by its unique ID.
     *
     * @param id the ID of the account to find.
     * @return an {@link Optional} containing the found account, or an empty Optional if no account was found.
     */
    public static Optional<Account> find(Long id) {
        return Optional.ofNullable (
                AccountRepository.find(id)
        );
    }

    /**
     * Finds accounts associated with a user by their CPF (Cadastro de Pessoas Físicas).
     *
     * @param cpf the CPF to search for.
     * @return a list of accounts associated with the user, or an empty list if no accounts were found.
     */
    public static List<Account> findByCpf(String cpf) {
        return AccountService.formatCpf(cpf)
                .map(formattedCpf -> UserRepository.list().stream()
                        .filter(u -> u.getCpf().equals(formattedCpf))
                        .findFirst()
                        .map(user -> AccountRepository.findByUser(user.getId()))
                        .orElse(Collections.emptyList())
                )
                .orElse(Collections.emptyList());
    }

    /**
     * Finds accounts associated with a user by their phone number.
     *
     * @param phone the phone number to search for.
     * @return a list of accounts associated with the user, or an empty list if no accounts were found.
     */
    public static List<Account> findByPhone(String phone) {
        return AccountService.formatPhone(phone)
                .map(formattedPhone -> UserRepository.list().stream()
                        .filter(u -> u.getPhone().equals(formattedPhone))
                        .findFirst()
                        .map(user -> AccountRepository.findByUser(user.getId()))
                        .orElse(Collections.emptyList())
                )
                .orElse(Collections.emptyList());
    }

    /**
     * Formats a phone number into the Brazilian standard format.
     *
     * @param phone the raw phone number input.
     * @return an {@link Optional} containing the formatted phone number, or an empty Optional if the input is invalid.
     */
    public static Optional<String> formatPhone(String phone) {
        String cleaned = phone.replaceAll("[^0-9]", "");

        if (cleaned.startsWith("55"))
            cleaned = cleaned.substring(2);

        return (cleaned.length() == 11)
                ? Optional.of(String.format("+55 (%s) %s-%s",
                    cleaned.substring(0, 2),
                    cleaned.substring(2, 7),
                    cleaned.substring(7)
                ))
                : Optional.empty();
    }

    /**
     * Formats a CPF into the standard format (XXX.XXX.XXX-XX).
     *
     * @param cpf the raw CPF input.
     * @return an {@link Optional} containing the formatted CPF, or an empty Optional if the input is invalid.
     */
    public static Optional<String> formatCpf(String cpf) {
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

    /**
     * Hashes a password using the BCrypt algorithm.
     *
     * @param password the plain text password to hash.
     * @return the hashed password.
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifies a password against a hashed password.
     *
     * @param originalPassword the plain text password to verify.
     * @param hashedPassword   the hashed password to compare against.
     * @return true if the password matches the hash, false otherwise.
     */
    public static boolean verifyPassword(String originalPassword, String hashedPassword) {
        return BCrypt.checkpw(originalPassword, hashedPassword);
    }

}
