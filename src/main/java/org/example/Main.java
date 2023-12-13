package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DatabaseManager.initializeDB();
        Scanner scanner = new Scanner(System.in);
        javax.swing.SwingUtilities.invokeLater(() -> {
            LibraryGUI gui = new LibraryGUI();
            gui.show();
        });

        try (Connection connection = DatabaseManager.getConnection()) {
            BookService bookService = new BookService(connection);

            while (true) {
                displayMenu();
                int option = scanner.nextInt();
                scanner.nextLine(); // Buffer ürítése

                switch (option) {
                    case 1:
                        bookService.printAllBooks();
                        break;
                    case 2:
                        System.out.print("Enter book title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter author's name: ");
                        String author = scanner.nextLine();
                        bookService.addBook(title, author);
                        break;
                    case 3:
                        System.out.print("Enter the ID of the book you want to borrow: ");
                        int bookId = scanner.nextInt();
                        scanner.nextLine(); // Buffer ürítése
                        System.out.print("Enter your name: ");
                        String userName = scanner.nextLine();
                        bookService.borrowBook(bookId, userName);
                        break;
                    case 4:
                        System.out.print("Enter the ID of the book you want to return: ");
                        int returnBookId = scanner.nextInt();
                        scanner.nextLine(); // Buffer ürítése
                        bookService.returnBook(returnBookId);
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        return;
                    case 5:
                        bookService.printAllBorrowings();
                        break;
                    default:
                        System.out.println("Invalid command. Please try again.");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void displayMenu() {
        System.out.println("1. Print all books");
        System.out.println("2. Add a new book");
        System.out.println("3. Borrow a book");
        System.out.println("4. Return a book");
        System.out.println("5. Print all borrowings history");
        System.out.println("0. Exit");
    }
}