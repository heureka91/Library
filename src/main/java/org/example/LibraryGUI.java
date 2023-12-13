package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LibraryGUI {
    private JFrame frame;
    private JTable table;

    public LibraryGUI() {
        initialize();
        loadBorrowedBooksData();
    }

    private void initialize() {
        frame = new JFrame("Library Management System");
        frame.setBounds(100, 100, 800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        table = new JTable();
        table.setModel(new DefaultTableModel(new Object[]{"Book ID", "Title", "Borrowed From", "Borrowed Until"}, 0));
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    void loadBorrowedBooksData() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing data

        String query = "SELECT B.BOOK_ID, BK.TITLE, B.BORROW_DATE, B.RETURN_DATE FROM BORROWINGS B INNER JOIN BOOKS BK ON B.BOOK_ID = BK.ID";

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int bookId = resultSet.getInt("BOOK_ID");
                String title = resultSet.getString("TITLE");
                Timestamp borrowDate = resultSet.getTimestamp("BORROW_DATE");
                Timestamp returnDate = resultSet.getTimestamp("RETURN_DATE");

                model.addRow(new Object[]{bookId, title, borrowDate, returnDate});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void show() {
        frame.setVisible(true);
    }




}
