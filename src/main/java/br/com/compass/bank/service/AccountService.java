package br.com.compass.bank.service;

import br.com.compass.bank.model.Account;
import br.com.compass.bank.repository.AccountRepository;
import br.com.compass.bank.repository.UserRepository;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AccountService {

    public static Optional<Account> find(Long id) {
        return Optional.ofNullable (
                AccountRepository.find(id)
        );
    }

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

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String originalPassword, String hashedPassword) {
        return BCrypt.checkpw(originalPassword, hashedPassword);
    }

}
