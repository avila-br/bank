package br.com.compass.bank.view;

import br.com.compass.bank.exception.account.AccountLoginException;
import br.com.compass.bank.model.Account;
import br.com.compass.bank.service.AccountService;
import br.com.compass.bank.service.AuthService;
import br.com.compass.bank.validation.InputValidator;

import java.util.List;
import java.util.Optional;

/**
 * This class handles the login process for users by allowing them to input their CPF and password.
 * <p>
 * It validates the CPF format, retrieves the accounts associated with the CPF, and allows the user to select an account.
 * The user is then prompted to enter a password. If the password is correct, the user is logged in, and the corresponding account view is displayed.
 * </p>
 */
public class AccountLoginView {

    private static final String view = """
        ╭──────────────────────────────────╮
        │           \u001B[34mLogin Screen\u001B[0m           │
        ╰──────────────────────────────────╯
          \u001B[32mCPF:\u001B[0m %s
          \u001B[33mPassword:\u001B[0m %s
        ╭─────────────────────────────────╮
        │           \u001B[31m0 - Cancel\u001B[0m            │
        ╰─────────────────────────────────╯
        >> %s:\s""";

    /**
     * Main handler for the account login view.
     * <p>
     * This method prompts the user to input their CPF and validates its format.
     * It then retrieves the list of accounts associated with the CPF and allows the user to select an account.
     * After selecting an account, the user is asked to input a password. If the password is correct, the login is successful,
     * and the corresponding account view is shown. If there are errors, the user is prompted to try again.
     * </p>
     */
    public static void handle() {
        String cpf = ViewRenderer.readString(String.format(view, "?", "-", "CPF (e.g., 123.456.789-00)"));
        if ("0".equals(cpf)) {
            System.out.println("Login canceled.");
            ViewRenderer.returnToMenu();
            return;
        }

        Optional<Exception> cpfValidation = InputValidator.CPF.validate(cpf);
        while (cpfValidation.isPresent()) {
            System.out.println(cpfValidation.get().getMessage());
            cpf = ViewRenderer.readString("Enter your CPF (e.g., 123.456.789-00): ");
            cpfValidation = InputValidator.CPF.validate(cpf);
        }

        List<Account> accounts = AccountService.findByCpf(cpf);
        boolean exists = AccountService.formatCpf(cpf).isPresent() && !accounts.isEmpty();
        if (!exists) {
            System.out.println("There's no registered accounts with provided CPF.");
            ViewRenderer.retry(AccountLoginView::handle);
            return;
        }

        Account account = select(accounts);
        while (true) {
            String password = ViewRenderer.readString(String.format(view, AccountService.formatCpf(cpf).get() + " (" + account.getType().toString() + ")", "?", "Password"));
            if ("0".equals(password)) {
                System.out.println("Login canceled.");
                ViewRenderer.returnToMenu();
                return;
            }

            try {
                AuthService.login(account.getId(), password);
                System.out.println("Login successful!");
                AccountView.handle();
                break;
            } catch (AccountLoginException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again or type '0' to cancel.\n");
            }
        }
    }

    /**
     * Renders a selection screen for the user to choose an account based on their CPF.
     *
     * @param accounts List of accounts linked to the CPF.
     * @return The chosen Account.
     */
    private static Account select(List<Account> accounts) {
        String template = """
        ╭─────────────────────────────────╮
        │        \u001B[34mSelect an Account\u001B[0m        │
        ╰─────────────────────────────────╯
          %s
        ╭─────────────────────────────────╮
        │           \u001B[31m0 - Cancel\u001B[0m            │
        ╰─────────────────────────────────╯
        >> %s:\s""";

        while (true) {
            String input = ViewRenderer.readString(getAccountsView(accounts, template));

            try {
                int choice = Integer.parseInt(input);

                if (choice == 0) {
                    System.out.println("Login canceled.");
                    ViewRenderer.returnToMenu();
                    throw new IllegalStateException();
                }

                if (choice > 0 && choice <= accounts.size())
                    return accounts.get(choice - 1);
                else
                    System.out.println("Invalid option. Please select a valid account.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Constructs the view of the available accounts for the user to select from.
     *
     * @param accounts List of accounts to display.
     * @param template The template to format the account list in the view.
     * @return The formatted string to be displayed to the user.
     */
    private static String getAccountsView(List<Account> accounts, String template) {
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            list.append(String.format(
                    "%d - \u001B[33m%s\u001B[0m%n",
                    i + 1,
                    account.getType().toString()
            ));
        }

        // Generate the full view with the account list
        return String.format(template, list.toString().trim(), "Account");
    }

}
