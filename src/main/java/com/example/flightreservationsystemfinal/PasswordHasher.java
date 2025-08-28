package com.example.flightreservationsystemfinal;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Scanner;

public class PasswordHasher {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- BCrypt Password Hash Generator ---");
        System.out.print("Enter the password you want to hash: ");
        String plainPassword = scanner.nextLine();

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        System.out.println("\nPassword hashing complete!");
        System.out.println("Copy the following hash and update your database:");
        System.out.println("\n" + hashedPassword);

        scanner.close();
    }
}