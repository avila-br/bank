package br.com.compass.bank.view;

import br.com.compass.bank.App;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The `ViewRenderer` class is responsible for rendering user interfaces for interacting with the system.
 * It provides utility methods for reading user input, displaying menus, retrying operations, and returning to the previous menu.
 * The class uses the `Scanner` object for input collection and offers a set of functions for different data types like integers, doubles, strings, and BigDecimals.
 */
public class ViewRenderer {

    private static final Scanner scanner = App.scanner;

    /**
     * Displays a message and prompts the user to try again or return to the menu.
     * If the user chooses to retry, the provided method will be executed again.
     * If the user chooses to return to the menu, the main menu will be shown.
     *
     * @param message The message to display before retrying.
     * @param method The method to be retried.
     */
    public static void retry(String message, Runnable method) {
        System.out.println(message);
        retry(method);
    }

    /**
     * Prompts the user to try again or return to the menu after an operation.
     * If the user selects option 1, the provided method is executed again.
     * If option 2 is selected, the main menu is shown.
     *
     * @param method The method to be retried.
     */
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

    /**
     * Displays an option to the user to return to the main menu.
     * Upon receiving any input, the main menu will be shown.
     */
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

    /**
     * Prompts the user to press any key to return to the previous menu, then invokes the provided menu method.
     *
     * @param menu The method to navigate back to.
     */
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

    /**
     * Reads an integer value from the user input. If the input is invalid (not an integer), the method will prompt the user again.
     *
     * @param label The label displayed to prompt the user for input.
     * @return The integer value entered by the user.
     */
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

    /**
     * Reads a double value from the user input. If the input is invalid (not a valid double), the method will prompt the user again.
     *
     * @param label The label displayed to prompt the user for input.
     * @return The double value entered by the user.
     */
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

    /**
     * Reads a string value from the user input. The input is trimmed to remove any leading or trailing spaces.
     *
     * @param label The label displayed to prompt the user for input.
     * @return The string value entered by the user.
     */
    public static String readString(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    /**
     * Reads a BigDecimal value from the user input. If the input is invalid (not a valid number), the method will prompt the user again.
     * The method also ensures that commas are replaced with periods for decimal separation.
     *
     * @param label The label displayed to prompt the user for input.
     * @return The BigDecimal value entered by the user.
     */
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
