package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class LibraryActions {

    private JFrame parent;

    public LibraryActions(JFrame parent) {
        this.parent = parent;
    }

    public void openAddBooksDialog() {
        JDialog addBooksDialog = new JDialog(parent, "Add Book", true);
        addBooksDialog.setLayout(new GridLayout(5, 2));
        addBooksDialog.setSize(400, 300);

        JLabel lblTitle = new JLabel("Title:");
        JTextField txtTitle = new JTextField();
        JLabel lblAuthor = new JLabel("Author:");
        JTextField txtAuthor = new JTextField();
        JCheckBox chkAvailable = new JCheckBox();
        chkAvailable.setSelected(true);

        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> {
            Book book = new Book();
            book.setTitle(txtTitle.getText());
            book.setAuthor(txtAuthor.getText());
            book.setAvailable(chkAvailable.isSelected());

            saveBookToDatabase(book);
            addBooksDialog.dispose();
        });

        addBooksDialog.add(lblTitle);
        addBooksDialog.add(txtTitle);
        addBooksDialog.add(lblAuthor);
        addBooksDialog.add(txtAuthor);
        addBooksDialog.add(chkAvailable);
        addBooksDialog.add(new JLabel());
        addBooksDialog.add(btnAdd);

        addBooksDialog.setLocationRelativeTo(parent);
        addBooksDialog.setVisible(true);
    }

    private void saveBookToDatabase(Book book) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (title, author, available) VALUES (?, ?, ?)")) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setBoolean(3, book.isAvailable());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(parent, "Book added successfully!");
            } else {
                JOptionPane.showMessageDialog(parent, "No book was added.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error adding book: " + e.getMessage());
        }
    }

    public void openViewBooksDialog() {
        JDialog viewBooksDialog = new JDialog(parent, "View Books", true);
        viewBooksDialog.setLayout(new BorderLayout());
        viewBooksDialog.setSize(500, 300);

        Vector<String> columns = new Vector<>();
        columns.add("ID");
        columns.add("Title");
        columns.add("Author");
        columns.add("Available");

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT ID, TITLE, AUTHOR, AVAILABLE FROM BOOKS")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("ID"));
                row.add(rs.getString("TITLE"));
                row.add(rs.getString("AUTHOR"));
                row.add(rs.getBoolean("AVAILABLE") ? "Yes" : "No");
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error fetching books: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(table);
        viewBooksDialog.add(scrollPane, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> viewBooksDialog.dispose());
        viewBooksDialog.add(btnClose, BorderLayout.SOUTH);

        viewBooksDialog.setLocationRelativeTo(parent);
        viewBooksDialog.setVisible(true);
    }

    public void openBorrowBookDialog() {
        JDialog borrowBookDialog = new JDialog(parent, "Borrow Book", true);
        borrowBookDialog.setLayout(new GridLayout(4, 2));
        borrowBookDialog.setSize(400, 300);

        JLabel lblBookID = new JLabel("Book ID:");
        JTextField txtBookID = new JTextField();
        JLabel lblBorrowerName = new JLabel("Borrower Name:");
        JTextField txtBorrowerName = new JTextField();
        JLabel lblBorrowDate = new JLabel("Borrow Date:");
        JTextField txtBorrowDate = new JTextField();

        JButton btnBorrow = new JButton("Borrow");
        btnBorrow.addActionListener(e -> {
            borrowBook(txtBookID.getText(), txtBorrowerName.getText());
            borrowBookDialog.dispose();
        });

        borrowBookDialog.add(lblBookID);
        borrowBookDialog.add(txtBookID);
        borrowBookDialog.add(lblBorrowerName);
        borrowBookDialog.add(txtBorrowerName);
        borrowBookDialog.add(lblBorrowDate);
        borrowBookDialog.add(txtBorrowDate);
        borrowBookDialog.add(new JLabel());
        borrowBookDialog.add(btnBorrow);

        borrowBookDialog.setLocationRelativeTo(parent);
        borrowBookDialog.setVisible(true);
    }

    private void borrowBook(String bookID, String borrowerName) {
        if (bookID.isEmpty() || borrowerName.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Please fill in the Book ID and Borrower Name fields.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(bookID);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Invalid Book ID format.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement("UPDATE BOOKS SET AVAILABLE = FALSE WHERE ID = ? AND AVAILABLE = TRUE");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO BORROWS (BOOK_ID, BORROWER_NAME, BORROW_DATE) VALUES (?, ?, CURRENT_DATE())"))
             {

            conn.setAutoCommit(false);

            updateStmt.setInt(1, id);
            int bookUpdateCount = updateStmt.executeUpdate();

            if (bookUpdateCount > 0) {
                insertStmt.setInt(1, id);
                insertStmt.setString(2, borrowerName);
                int borrowInsertCount = insertStmt.executeUpdate();

                if (borrowInsertCount > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(parent, "Book borrowed successfully!");
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(parent, "Failed to borrow book, book might not be available.");
                }
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(parent, "Book is not available for borrowing.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "SQL Error: " + e.getMessage());
        }
    }



    public void openReturnBookDialog() {
        JDialog returnBookDialog = new JDialog(parent, "Return Book", true);
        returnBookDialog.setLayout(new GridLayout(3, 2));
        returnBookDialog.setSize(300, 200);

        JLabel lblBookID = new JLabel("Book ID:");
        JTextField txtBookID = new JTextField();
        JButton btnReturn = new JButton("Return");

        btnReturn.addActionListener(e -> {
            try {
                int bookId = Integer.parseInt(txtBookID.getText());
                if (DatabaseManager.returnBook(bookId)) {
                    JOptionPane.showMessageDialog(returnBookDialog, "Book returned successfully.");
                } else {
                    JOptionPane.showMessageDialog(returnBookDialog, "Failed to return book.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(returnBookDialog, "Invalid book ID.");
            }
        });

        returnBookDialog.add(lblBookID);
        returnBookDialog.add(txtBookID);
        returnBookDialog.add(new JLabel());
        returnBookDialog.add(btnReturn);

        returnBookDialog.setLocationRelativeTo(parent);
        returnBookDialog.setVisible(true);
    }
    void logout() {System.exit(0);}
    public void openBorrowHistoryDialog() {
        JDialog borrowHistoryDialog = new JDialog(parent, "Borrow History", true);
        borrowHistoryDialog.setLayout(new BorderLayout());
        borrowHistoryDialog.setSize(600, 400);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Book ID");
        model.addColumn("Book Title");
        model.addColumn("Borrower Name");
        model.addColumn("Borrow Date");
        model.addColumn("Return Date");

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT B.BOOK_ID, BK.TITLE, B.BORROWER_NAME, B.BORROW_DATE, B.RETURN_DATE " +
                             "FROM BORROWS B INNER JOIN BOOKS BK ON B.BOOK_ID = BK.ID")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("BOOK_ID"),
                        rs.getString("TITLE"), // Könyv neve hozzáadva
                        rs.getString("BORROWER_NAME"),
                        rs.getDate("BORROW_DATE"),
                        rs.getDate("RETURN_DATE")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error fetching borrow history: " + e.getMessage());
        }

        borrowHistoryDialog.add(scrollPane, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> borrowHistoryDialog.dispose());
        borrowHistoryDialog.add(btnClose, BorderLayout.SOUTH);

        borrowHistoryDialog.setLocationRelativeTo(parent);
        borrowHistoryDialog.setVisible(true);
    }



    // További metódusok és logika itt, ha szükséges
}
