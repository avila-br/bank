package br.com.compass.bank.view;

import br.com.compass.bank.App;
import br.com.compass.bank.exception.transaction.TransactionException;
import br.com.compass.bank.model.Account;
import br.com.compass.bank.model.AccountType;
import br.com.compass.bank.model.Transaction;
import br.com.compass.bank.model.TransactionType;
import br.com.compass.bank.repository.TransactionRepository;
import br.com.compass.bank.service.AccountService;
import br.com.compass.bank.service.AuthService;
import br.com.compass.bank.service.TransactionService;
import br.com.compass.bank.validation.InputValidator;

import lombok.AccessLevel;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for handling the user interactions with their bank account. It provides methods
 * for performing various banking operations such as deposit, withdraw, checking balance, transferring money,
 * and viewing the bank statement. It also ensures that user input is validated before any operation is performed.
 * The class uses a menu-driven interface to interact with the user.
 */
public class AccountView {

    @Setter(AccessLevel.PRIVATE)
    private static Account user;

    /**
     * Displays the bank menu for the user and navigates between options.
     * The available options are: Deposit, Withdraw, Check Balance, Transfer, Bank Statement, and Exit.
     * Based on the selected option, the corresponding method is invoked.
     * This method acts as the entry point for handling user interactions within the account menu.
     */
    public static void handle() {
        AccountView.setUser(AuthService.Context.getCurrent());

        int option = ViewRenderer.readInteger("""
        ╭────────────────────────────────╮
        │           \u001B[34mBank Menu\u001B[0m            │
        ├────────────────────────────────┤
        │   \u001B[32m1 - Deposit\u001B[0m                  │
        │   \u001B[33m2 - Withdraw\u001B[0m                 │
        │   \u001B[36m3 - Check Balance\u001B[0m            │
        │   \u001B[35m4 - Transfer\u001B[0m                 │
        │   \u001B[37m5 - Bank Statement\u001B[0m           │
        │   \u001B[31m0 - Exit\u001B[0m                     │
        ╰────────────────────────────────╯
        >>\s""");

        switch (option) {
            case 1: deposit();
            case 2: withdraw();
            case 3: balance();
            case 4: transfer();
            case 5: statement();
            case 0: App.menu();
            default: ViewRenderer.retry(AccountView::handle);
        }
    }

    /**
     * Handles the deposit process by prompting the user for an amount and performing the deposit transaction.
     * It checks that the deposit amount is greater than zero and calls the appropriate service to execute the deposit.
     * If the deposit is successful, it confirms the operation to the user.
     */
    private static void deposit() {
        final String view = """
        ╭──────────────────────────────────╮
        │             \u001B[34mDeposit\u001B[0m              │
        ╰──────────────────────────────────╯
          \u001B[33mAmount:\u001B[0m %s
        ╭─────────────────────────────────╮
        │           \u001B[31m0 - Cancel\u001B[0m            │
        ╰─────────────────────────────────╯
        >> %s:\s""";

        BigDecimal amount = ViewRenderer.readBigDecimal(String.format(view, "?", "Amount"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Deposit amount must be greater than zero.");
            return;
        }

        try {
            TransactionService.deposit(user, amount);
            System.out.println("Successfully deposited!");
            ViewRenderer.returnTo(AccountView::handle);
        } catch (TransactionException e) {
            System.out.println("Unable to deposit: " + e.getMessage());
        }
    }

    /**
     * Handles the withdrawal process by prompting the user for an amount and performing the withdrawal transaction.
     * It checks that the withdrawal amount is greater than zero and calls the appropriate service to execute the withdrawal.
     * If the withdrawal is successful, it confirms the operation to the user.
     */
    private static void withdraw() {
        final String view = """
        ╭──────────────────────────────────╮
        │             \u001B[34mWithdraw\u001B[0m             │
        ╰──────────────────────────────────╯
          \u001B[33mAmount:\u001B[0m %s
        ╭──────────────────────────────────╮
        │            \u001B[31m0 - Cancel\u001B[0m            │
        ╰──────────────────────────────────╯
        >> %s:\s""";

        BigDecimal amount = ViewRenderer.readBigDecimal(String.format(view, "?", "Amount"));
        while (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Withdraw amount must be greater than zero.");
            amount = ViewRenderer.readBigDecimal(String.format(view, "?", "Amount"));
        }

        try {
            TransactionService.withdraw(user, amount);
            System.out.println("Successfully withdrew!");
            ViewRenderer.returnTo(AccountView::handle);
        } catch (TransactionException e) {
            System.out.println("Unable to withdraw: " + e.getMessage());
        }
    }

    /**
     * Displays the current balance of the user's account.
     * This method prints the balance in a user-friendly format to the console.
     */
    private static void balance() {
        final String done = """
        ╭─────────────────────────────╮
        │           \u001B[34mAccount\u001B[0m           │
        ╰─────────────────────────────╯
          \u001B[32mBalance: %s\u001B[0m
        """;

        System.out.printf((done) + "%n", user.getBalance());
        ViewRenderer.returnTo(AccountView::handle);
    }

    /**
     * Handles the transfer process by prompting the user for the recipient's account number or CPF and the amount to transfer.
     * It validates the input, checks for sufficient funds, and attempts the transfer. If the transfer is successful, it confirms the operation to the user.
     * If there is an error, such as insufficient funds or invalid account information, it will display an error message.
     */
    private static void transfer() {
        final String view = """
        ╭───────────────────────────────────╮
        │             \u001B[34mTransfer\u001B[0m              │
        ╰───────────────────────────────────╯
          \u001B[32mTo Account:\u001B[0m %s
          \u001B[33mAmount:\u001B[0m %s
        ╭──────────────────────────────────╮
        │            \u001B[31m0 - Cancel\u001B[0m            │
        ╰──────────────────────────────────╯
        >> %s:\s""";

        String target = ViewRenderer.readString(String.format(view, "?", "-", "Account Number (ID) or CPF (e.g., 123.456.789-00)"));
        if ("0".equals(target)) {
            System.out.println("Transfer canceled.");
            ViewRenderer.returnTo(AccountView::handle);
            return;
        }

        Optional<Exception> validation = InputValidator.CPF.validate(target);
        if (validation.isPresent()) {
            try {
                long id = Long.parseLong(target);
                Optional<Account> receiverById = AccountService.find(id);
                if (receiverById.isEmpty()) {
                    System.out.println("There's no registered accounts with the provided ID.");
                    ViewRenderer.returnTo(AccountView::handle);
                    return;
                }

                BigDecimal amount = ViewRenderer.readBigDecimal("Enter the amount to send: ");
                while (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Deposit amount must be greater than zero.");
                    amount = ViewRenderer.readBigDecimal("Enter the amount to deposit: ");
                }

                try {
                    TransactionService.transfer(user, receiverById.get(), amount);
                    System.out.println("Successfully transferred to account with ID: " + receiverById.get().getId());
                    ViewRenderer.returnTo(AccountView::handle);
                } catch (TransactionException e) {
                    System.out.println("Unable to withdraw: " + e.getMessage());
                    ViewRenderer.returnTo(AccountView::handle);
                }
                return;

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a valid Account Number (ID) or CPF.");
                ViewRenderer.returnTo(AccountView::handle);
                return;
            }
        }

        Optional<Account> receiver = AccountService.findByCpf(target).stream()
                .filter(account -> account.getType().equals(AccountType.CHECKING))
                .findFirst();

        if (receiver.isEmpty()) {
            System.out.println("There's no registered checking accounts with the provided CPF.");
            ViewRenderer.returnTo(AccountView::handle);
            return;
        }

        BigDecimal amount = ViewRenderer.readBigDecimal("Enter the amount to send: ");
        while (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Deposit amount must be greater than zero.");
            amount = ViewRenderer.readBigDecimal("Enter the amount to send: ");
        }

        try {
            TransactionService.transfer(user, receiver.get(), amount);
            System.out.println("Successfully transferred to checking account with CPF: " + receiver.get().getUser().getCpf());
            ViewRenderer.returnTo(AccountView::handle);
        } catch (TransactionException e) {
            System.out.println("Unable to withdraw: " + e.getMessage());
            ViewRenderer.returnTo(AccountView::handle);
        }
    }

    /**
     * Displays the user's bank statement by retrieving all transactions related to their account.
     * If no transactions are found, a message is displayed indicating that there is no data to show.
     */
    private static void statement() {
        List<Transaction> transactions = TransactionRepository.findByAccount(user);

        if (transactions.isEmpty()) {
            System.out.println("There's nothing to show here.");
            ViewRenderer.returnTo(AccountView::handle);
        }

        System.out.print("""
        ╭──────────────────────────────────────────────────╮
        │                  \u001B[34mBank Statement\u001B[0m                  │
        ╰──────────────────────────────────────────────────╯
        """);

        transactions.forEach(transaction -> {
            String type = transaction.getType().name();
            boolean received = transaction.getReceiver() != null && transaction.getReceiver().getId().equals(user.getId());

            System.out.println("╭──────────────────────────────────────────────────╮");
            System.out.println("  \u001B[32m" + (received ? "Received" : "\u001B[31mSent") + "\u001B[0m" + (received ? " " : "    ")
                    + "\u001B[33m" + type + "\u001B[0m" + (type.equals(TransactionType.WITHDRAWAL.name()) ? "    " : ("      ")) + "Date: " +
                    transaction.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            System.out.println("  Amount:" + (received ? "  " : " ") + (received
                    ? "\u001B[32m" + transaction.getAmount() + "\u001B[0m" : "\u001B[31m" + transaction.getAmount() + "\u001B[0m"));
            System.out.println("╰──────────────────────────────────────────────────╯");
        });

        System.out.println("""
        ╭──────────────────────────────────────────────────╮
        │                 \u001B[33mEnd of Statement\u001B[0m                 │
        ╰──────────────────────────────────────────────────╯
        """);
        ViewRenderer.returnTo(AccountView::handle);
    }

}
