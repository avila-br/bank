package br.com.compass.bank;

import br.com.compass.bank.view.AccountLoginView;
import br.com.compass.bank.view.AccountOpeningView;
import br.com.compass.bank.view.ViewRenderer;

import java.util.Scanner;

public class App {

    public static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        menu();
        scanner.close();
        System.out.println("Application closed");
    }

    public static void menu() {
        int option = ViewRenderer.readInteger("""
        ╭────────────────────────────────╮
        │           \u001B[34mMain Menu\u001B[0m            │
        ├────────────────────────────────┤
        │   \u001B[32m1 - Login\u001B[0m                    │
        │   \u001B[33m2 - Account Opening\u001B[0m          │
        │   \u001B[36m0 - Exit\u001B[0m                     │
        ╰────────────────────────────────╯
        >>\s""");

        switch (option) {
            case 1:
                AccountLoginView.handle();
                break;
            case 2:
                AccountOpeningView.handle();
                break;
            case 0:
                System.exit(0);
                break;
            default:
                // TODO
                // System.out.println("Invalid option! Please try again.");
                break;
        }
    }

    public static void bankMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("========= Bank Menu =========");
            System.out.println("|| 1. Deposit              ||");
            System.out.println("|| 2. Withdraw             ||");
            System.out.println("|| 3. Check Balance        ||");
            System.out.println("|| 4. Transfer             ||");
            System.out.println("|| 5. Bank Statement       ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    // ToDo...
                    System.out.println("Deposit.");
                    break;
                case 2:
                    // ToDo...
                    System.out.println("Withdraw.");
                    break;
                case 3:
                    // ToDo...
                    System.out.println("Check Balance.");
                    break;
                case 4:
                    // ToDo...
                    System.out.println("Transfer.");
                    break;
                case 5:
                    // ToDo...
                    System.out.println("Bank Statement.");
                    break;
                case 0:
                    // ToDo...
                    System.out.println("Exiting...");
                    running = false;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

}
