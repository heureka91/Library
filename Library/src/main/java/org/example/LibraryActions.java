package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LibraryActions {
    private IDatabaseManager dbManager;
    private JFrame parent;

    // Konstruktor a grafikus felülethez és az adatbázis-kezelőhöz
    public LibraryActions(JFrame parent, IDatabaseManager dbManager) {
        this.parent = parent;
        this.dbManager = dbManager;
    }

    public void openAddBooksDialog() {
        JDialog addBooksDialog = createDialog("Add Book", new GridLayout(5, 2), 400, 300);
        JTextField txtTitle = new JTextField();
        JTextField txtAuthor = new JTextField();
        JCheckBox chkAvailable = new JCheckBox("Available", true);
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> addBook(txtTitle, txtAuthor, chkAvailable, addBooksDialog));
        addComponentsToDialog(addBooksDialog, new JLabel("Title:"), txtTitle, new JLabel("Author:"), txtAuthor, new JLabel("Available:"), chkAvailable, null, btnAdd);
        showDialog(addBooksDialog);
    }

    public void openViewBooksDialog() {
        JDialog viewBooksDialog = createDialog("View Books", new BorderLayout(), 500, 300);
        String[] columnNames = {"ID", "Title", "Author", "Available"};
        JTable table = new JTable(getBooksDataForTable(), columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        viewBooksDialog.add(scrollPane, BorderLayout.CENTER);
        JButton btnClose = createCloseButton(viewBooksDialog);
        viewBooksDialog.add(btnClose, BorderLayout.SOUTH);
        showDialog(viewBooksDialog);
    }

    public void openBorrowBookDialog() {
        JDialog borrowBookDialog = createDialog("Borrow Book", new GridLayout(4, 2), 400, 300);
        JTextField txtBookID = new JTextField();
        JTextField txtBorrowerName = new JTextField();
        JButton btnBorrow = new JButton("Borrow");
        btnBorrow.addActionListener(e -> borrowBook(txtBookID, txtBorrowerName, borrowBookDialog));
        addComponentsToDialog(borrowBookDialog, new JLabel("Book ID:"), txtBookID, new JLabel("Borrower Name:"), txtBorrowerName, null, btnBorrow);
        showDialog(borrowBookDialog);
    }

    public void openReturnBookDialog() {
        JDialog returnBookDialog = createDialog("Return Book", new GridLayout(3, 2), 300, 200);
        JTextField txtBookID = new JTextField();
        JButton btnReturn = new JButton("Return");
        btnReturn.addActionListener(e -> returnBook(txtBookID, returnBookDialog));
        addComponentsToDialog(returnBookDialog, new JLabel("Book ID:"), txtBookID, null, btnReturn);
        showDialog(returnBookDialog);
    }

    public void openBorrowHistoryDialog() {
        JDialog borrowHistoryDialog = createDialog("Borrow History", new BorderLayout(), 600, 400);
        String[] columnNames = {"Book ID", "Book Title", "Borrower Name", "Borrow Date", "Return Date"};
        JTable table = new JTable(getBorrowHistoryDataForTable(), columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        borrowHistoryDialog.add(scrollPane, BorderLayout.CENTER);
        JButton btnClose = createCloseButton(borrowHistoryDialog);
        borrowHistoryDialog.add(btnClose, BorderLayout.SOUTH);
        showDialog(borrowHistoryDialog);
    }
    public void logout() {
        System.exit(0);
    }


    // Helper methods
    private JDialog createDialog(String title, LayoutManager layout, int width, int height) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setLayout(layout);
        dialog.setSize(width, height);
        return dialog;
    }

    private void showDialog(JDialog dialog) {
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private JButton createCloseButton(JDialog dialog) {
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        return btnClose;
    }

    private void addComponentsToDialog(JDialog dialog, JComponent... components) {
        for (JComponent component : components) {
            if (component != null) {
                dialog.add(component);
            } else {
                dialog.add(new JLabel());
            }
        }
    }

    private Object[][] getBooksDataForTable() {
        List<Book> books = dbManager.getBooks();
        Object[][] data = new Object[books.size()][4];
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            data[i][0] = book.getId();
            data[i][1] = book.getTitle();
            data[i][2] = book.getAuthor();
            data[i][3] = book.isAvailable() ? "Yes" : "No";
        }
        return data;
    }

    private Object[][] getBorrowHistoryDataForTable() {
        List<BorrowRecord> history = dbManager.getBorrowingHistory();
        Object[][] data = new Object[history.size()][5];
        for (int i = 0; i < history.size(); i++) {
            BorrowRecord record = history.get(i);
            data[i][0] = record.getBookId();
            data[i][1] = record.getBookTitle();
            data[i][2] = record.getBorrowerName();
            data[i][3] = record.getBorrowDate().toString();
            data[i][4] = record.getReturnDate() != null ? record.getReturnDate().toString() : "Not Returned";
        }
        return data;
    }

    private void addBook(JTextField txtTitle, JTextField txtAuthor, JCheckBox chkAvailable, JDialog dialog) {
        Book book = new Book();
        book.setTitle(txtTitle.getText());
        book.setAuthor(txtAuthor.getText());
        book.setAvailable(chkAvailable.isSelected());
        boolean success = dbManager.addBook(book);
        if (success) {
            JOptionPane.showMessageDialog(dialog, "Book added successfully!");
        } else {
            JOptionPane.showMessageDialog(dialog, "Failed to add book.");
        }
        dialog.dispose();
    }

    private void borrowBook(JTextField txtBookID, JTextField txtBorrowerName, JDialog dialog) {
        try {
            int bookId = Integer.parseInt(txtBookID.getText());
            boolean success = dbManager.borrowBook(bookId, txtBorrowerName.getText());
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Book borrowed successfully.");
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to borrow book.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Invalid book ID.");
        }
        dialog.dispose();
    }

    private void returnBook(JTextField txtBookID, JDialog dialog) {
        try {
            int bookId = Integer.parseInt(txtBookID.getText());
            boolean success = dbManager.returnBook(bookId);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Book returned successfully.");
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to return book.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Invalid book ID.");
        }
        dialog.dispose();
    }
}
