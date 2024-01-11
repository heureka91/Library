package org.example;

import javax.swing.*;
import java.awt.*;

public class LibraryGUI extends JFrame {

    private LibraryActions libraryActions;

    public LibraryGUI() {
        super("Librarian Section - JavaTpoint");
        setSize(400, 400);
        setLayout(new GridLayout(6, 1));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        libraryActions = new LibraryActions(this);

        // Initialize buttons
        JButton btnAddBooks = new JButton("Add Books");
        JButton btnViewBooks = new JButton("View Books");
        JButton btnBorrowBook = new JButton("Borrow Book");
        JButton btnReturnBook = new JButton("Return Book");
        JButton btnBorrowHistory = new JButton("Borrow History");
        JButton btnLogout = new JButton("Logout");

        // Add action listeners
        btnAddBooks.addActionListener(e -> libraryActions.openAddBooksDialog());
        btnViewBooks.addActionListener(e -> libraryActions.openViewBooksDialog());
        btnBorrowBook.addActionListener(e -> libraryActions.openBorrowBookDialog());
        btnReturnBook.addActionListener(e -> libraryActions.openReturnBookDialog());
        btnBorrowHistory.addActionListener(e -> libraryActions.openBorrowHistoryDialog());
        add(btnBorrowHistory);
        btnLogout.addActionListener(e -> libraryActions.logout());
        // Add Logout action listener if needed

        // Add buttons to the window
        add(btnAddBooks);
        add(btnViewBooks);
        add(btnBorrowBook);
        add(btnReturnBook);
        add(btnBorrowHistory);
        add(btnLogout);

        setLocationRelativeTo(null);
    }

    // main method if needed
}
