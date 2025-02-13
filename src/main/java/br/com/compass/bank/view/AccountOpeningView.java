package br.com.compass.bank.view;

import br.com.compass.bank.exception.account.AccountOpeningException;
import br.com.compass.bank.model.Account;
import br.com.compass.bank.model.AccountType;
import br.com.compass.bank.model.User;
import br.com.compass.bank.service.AuthService;
import br.com.compass.bank.validation.InputValidator;

import java.util.Optional;

/**
 * This class handles the account opening process for users.
 * <p>
 * The user is prompted to enter their CPF, name, phone number, account type, and password.
 * Each input is validated, and if the user provides valid data, an account is created.
 * If an error occurs during account creation, the user is informed.
 * </p>
 */
public class AccountOpeningView {

    private static final String view = """
        ╭──────────────────────────────────╮
        │       \u001B[34mRegistration Details\u001B[0m       │
        ╰──────────────────────────────────╯
          \u001B[32mCPF: %s\u001B[0m
          \u001B[33mName: %s\u001B[0m
          \u001B[34mPhone: %s\u001B[0m
          \u001B[35mAccount Type: %s\u001B[0m
        ╭─────────────────────────────────╮
        │           \u001B[31m0 - Cancel\u001B[0m            │
        ╰─────────────────────────────────╯
        >> %s:\s""";

    private static final String done = """
        ╭──────────────────────────────────╮
        │           \u001B[34mYour Account\u001B[0m           │
        ╰──────────────────────────────────╯
          \u001B[32mCPF: %s\u001B[0m
          \u001B[33mName: %s\u001B[0m
          \u001B[34mPhone: %s\u001B[0m
          \u001B[35mAccount Type: %s\u001B[0m
        """;

    /**
     * Main handler for the account opening view.
     * <p>
     * This method prompts the user to input their personal details, including CPF, name, phone number, account type, and password.
     * Each input is validated using the respective validators. If all inputs are valid, an account is created and registered.
     * If any validation fails or an error occurs during account creation, the process is halted and the user is informed.
     * </p>
     */
    public static void handle() {
        String cpf = ViewRenderer.readString(String.format(view, "?", "-", "-", "-", "CPF (e.g., 123.456.789-00)"));
        if ("0".equals(cpf)) {
            System.out.println("Account opening canceled.");
            ViewRenderer.returnToMenu();
            return;
        }

        Optional<Exception> cpfValidation = InputValidator.CPF.validate(cpf);
        while (cpfValidation.isPresent()) {
            System.out.println(cpfValidation.get().getMessage());
            cpf = ViewRenderer.readString("Enter your CPF (e.g., 123.456.789-00): ");
            cpfValidation = InputValidator.CPF.validate(cpf);
        }

        String name = ViewRenderer.readString(String.format(view, cpf, "?", "-", "-", "Name"));
        if ("0".equals(name)) {
            System.out.println("Account opening canceled.");
            ViewRenderer.returnToMenu();
            return;
        }

        Optional<Exception> nameValidation = InputValidator.NAME.validate(name);
        while (nameValidation.isPresent()) {
            System.out.println(nameValidation.get().getMessage());
            name = ViewRenderer.readString("Enter your name: ");
            nameValidation = InputValidator.NAME.validate(name);
        }

        String phone = ViewRenderer.readString(String.format(view, cpf, name, "?", "-", "Phone Number"));
        if ("0".equals(phone)) {
            System.out.println("Account opening canceled.");
            ViewRenderer.returnToMenu();
            return;
        }

        Optional<Exception> phoneValidation = InputValidator.PHONE.validate(phone);
        while (phoneValidation.isPresent()) {
            System.out.println(phoneValidation.get().getMessage());
            phone = ViewRenderer.readString("Enter your phone number: ");
            phoneValidation = InputValidator.PHONE.validate(phone);
        }

        String typeStr = ViewRenderer.readString(String.format(view, cpf, name, phone, "?", "Account Type"));
        if ("0".equals(typeStr)) {
            System.out.println("Account opening canceled.");
            ViewRenderer.returnToMenu();
            return;
        }

        Optional<AccountType> accountTypeValidation = AccountType.from(typeStr);
        while (accountTypeValidation.isEmpty()) {
            System.out.println("Invalid account type. Please choose from: Checking or Savings.");
            typeStr = ViewRenderer.readString("Enter your account type (Checking, Savings): ");
            accountTypeValidation = AccountType.from(typeStr);
        }
        AccountType type = accountTypeValidation.get();

        String password = ViewRenderer.readString(String.format(view, cpf, name, phone, type, "Password"));
        if ("0".equals(password)) {
            System.out.println("Account opening canceled.");
            ViewRenderer.returnToMenu();
            return;
        }

        Optional<Exception> passwordValidation = InputValidator.PASSWORD.validate(password);
        while (passwordValidation.isPresent()) {
            System.out.println(passwordValidation.get().getMessage());
            password = ViewRenderer.readString("Enter your password: ");
            passwordValidation = InputValidator.PASSWORD.validate(password);
        }

        User user = User.builder()
                .cpf(cpf)
                .phone(phone)
                .name(name)
                .build();

        Account account = Account.builder()
                .user(user)
                .password(password)
                .type(type)
                .build();

        try {
            AuthService.register(account);
            System.out.println("Account successfully created!");
            System.out.printf((done) + "%n", cpf, name, phone, type);
            ViewRenderer.returnToMenu();
        } catch (AccountOpeningException e) {
            System.out.println("Failed to create account: " + e.getMessage());
            ViewRenderer.returnToMenu();
        }
    }

}
