package org.example;

import java.sql.*;

public class BookService {
    private final Connection connection;

    public BookService(Connection connection) {
        this.connection = connection;
    }

    public void addBook(String title, String author) throws SQLException {
        String insertQuery = "INSERT INTO BOOKS (TITLE, AUTHOR) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating book failed, no rows affected.");
            }
        }
    }

    public void borrowBook(int bookId, String userName) throws SQLException {
        String checkQuery = "SELECT * FROM BOOKS WHERE ID = ? AND AVAILABLE = TRUE";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, bookId);
            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next()) {
                String borrowQuery = "INSERT INTO BORROWINGS (BOOK_ID, USER_NAME, BORROW_DATE) VALUES (?, ?, CURRENT_TIMESTAMP)";
                try (PreparedStatement borrowStmt = connection.prepareStatement(borrowQuery)) {
                    connection.setAutoCommit(false); // Start transaction

                    borrowStmt.setInt(1, bookId);
                    borrowStmt.setString(2, userName);
                    borrowStmt.executeUpdate();

                    String updateBookQuery = "UPDATE BOOKS SET AVAILABLE = FALSE WHERE ID = ?";
                    try (PreparedStatement updateBookStmt = connection.prepareStatement(updateBookQuery)) {
                        updateBookStmt.setInt(1, bookId);
                        updateBookStmt.executeUpdate();
                    }

                    connection.commit(); // Commit transaction
                } catch (SQLException e) {
                    connection.rollback(); // Rollback transaction on error
                    throw e;
                } finally {
                    connection.setAutoCommit(true); // Reset auto-commit to default
                }
            } else {
                throw new SQLException("Book is not available or does not exist.");
            }
        }
    }

    public void returnBook(int bookId) throws SQLException {
        String returnQuery = "UPDATE BORROWINGS SET RETURN_DATE = CURRENT_TIMESTAMP WHERE BOOK_ID = ? AND RETURN_DATE IS NULL";
        try (PreparedStatement returnStmt = connection.prepareStatement(returnQuery)) {
            returnStmt.setInt(1, bookId);
            int affectedRows = returnStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Returning book failed, no rows affected.");
            }

            String updateBookQuery = "UPDATE BOOKS SET AVAILABLE = TRUE WHERE ID = ?";
            try (PreparedStatement updateBookStmt = connection.prepareStatement(updateBookQuery)) {
                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();
            }
        }
    }

    public void printAllBooks() throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM BOOKS")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String title = resultSet.getString("TITLE");
                String author = resultSet.getString("AUTHOR");
                boolean available = resultSet.getBoolean("AVAILABLE");

                Book book = new Book(id, title, author, available);
                System.out.println(book);
            }
        }
    }
    // A BookService osztályban
    public void printAllBorrowings() throws SQLException {
        String query = "SELECT B.BOOK_ID, BK.TITLE, B.USER_NAME, B.BORROW_DATE, B.RETURN_DATE FROM BORROWINGS B INNER JOIN BOOKS BK ON B.BOOK_ID = BK.ID";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int bookId = resultSet.getInt("BOOK_ID");
                String title = resultSet.getString("TITLE");
                String userName = resultSet.getString("USER_NAME");
                Timestamp borrowDate = resultSet.getTimestamp("BORROW_DATE");
                Timestamp returnDate = resultSet.getTimestamp("RETURN_DATE");

                System.out.println("Book ID: " + bookId + ", Title: " + title + ", Borrowed by: " + userName + ", Borrowed on: " + borrowDate + ", Returned on: " + returnDate);
            }
        }
    }

}
