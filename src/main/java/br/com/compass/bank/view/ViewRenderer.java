package br.com.compass.bank.view;

import br.com.compass.bank.App;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ViewRenderer {

    private static final Scanner scanner = App.scanner;

    public static void retry(String message, Runnable method) {
        System.out.println(message);
        retry(method);
    }

    public static void retry(Runnable method) {
        int option = readInteger("""
        \u001B[0m╭─────────────────────────────────────────╮
        │ \u001B[34m               Try again?\u001B[0m               │
        ├─────────────────────────────────────────┤
        │   \u001B[32m1 - Yes!\u001B[0m                              │
        │   \u001B[33m2 - Return to the menu\u001B[0m                │
        ╰─────────────────────────────────────────╯
        >>\s""");

        switch (option) {
            case 1 -> method.run();
            case 2 -> App.menu();
            default -> retry(method);
        }
    }

    public static void returnToMenu() {
        System.out.print("""
        \u001B[0m╭─────────────────────────────────────────╮
        │         \u001B[33m0 - Return to the menu\u001B[0m          │
        ╰─────────────────────────────────────────╯
        >>\s""");

        scanner.next();
        scanner.nextLine();
        App.menu();
    }

    public static void returnTo(Runnable menu) {
        System.out.print("""
        \u001B[0m╭─────────────────────────────╮
        │         \u001B[33m0 - Return\u001B[0m          │
        ╰─────────────────────────────╯
        >>\s""");

        scanner.next();
        scanner.nextLine();
        menu.run();
    }

    public static int readInteger(String label) {
        int value;
        try {
            System.out.print(label);
            value = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid option! Please try again.");
            scanner.nextLine();
            value = readInteger("\n>> ");
        }

        return value;
    }

    public static double readDouble(String label) {
        System.out.print(label);
        String value = scanner.nextLine()
                .replace(',', '.');

        double result;
        try {
            result = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println("Invalid option! Please try again.");
            result = readDouble("\n>> ");
        }

        return result;
    }

    public static String readString(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    public static BigDecimal readBigDecimal(String label) {
        System.out.print(label);
        String value = scanner.nextLine().replace(',', '.');

        BigDecimal result;
        try {
            result = new BigDecimal(value);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount! Please try again.");
            result = readBigDecimal("\n>> ");
        }

        return result;
    }

}
