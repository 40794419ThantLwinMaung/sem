package com.napier.sem;

public class Greeter {
    private String message;

    // Constructor
    public Greeter(String message) {
        this.message = message;
    }

    // Method to display the message
    public void greet() {
        System.out.println(message);
    }
}
