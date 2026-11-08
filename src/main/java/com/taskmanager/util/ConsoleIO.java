package com.taskmanager.util;

import java.io.PrintStream;
import java.io.InputStream;
import java.util.Scanner;

public class ConsoleIO {
    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleIO(InputStream in, PrintStream out) {
        this.scanner = in;
        this.out = out;
    } 
}
