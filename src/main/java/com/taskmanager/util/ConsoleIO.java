package com.taskmanager.util;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleIO {
    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleIO(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public String readString(String prompt) {
        out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readOptionalString(String prompt) {
        out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    out.printf("Error: Please enter a number between %d and %d.%n", min, max);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                out.println("Error: Invalid input. Please enter a valid integer.");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return null;
            }

            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                out.println("Error: Invalid date format. Please use YYYY-MM-DD or leave blank.");
            }
        }
    }

    public void print(String message) {
        out.println(message);
    }

    public void printError(String message) {
        out.println("Error: " + message);
    }
}