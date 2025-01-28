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
                 System.out.println("Invalid option! Please try again.");
                 menu();
                 break;
        }
    }

}
