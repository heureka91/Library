package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements IDatabaseManager {
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    private static final String USER = "sa";
    private static final String PASS = "";

    public DatabaseManager() {
        initializeDB();
    }
    // Publikus getConnection metódus
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    static void initializeDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            String sqlCreateBooks =
                    "CREATE TABLE IF NOT EXISTS BOOKS (" +
                            "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                            "TITLE VARCHAR(255), " +
                            "AUTHOR VARCHAR(255), " +
                            "AVAILABLE BOOLEAN);";
            stmt.execute(sqlCreateBooks);

            String sqlCreateBorrows =
                    "CREATE TABLE IF NOT EXISTS BORROWS (" +
                            "BORROW_ID INT AUTO_INCREMENT PRIMARY KEY, " +
                            "BOOK_ID INT, " +
                            "BORROWER_NAME VARCHAR(255), " +
                            "BORROW_DATE DATE, " +
                            "RETURN_DATE DATE, " +
                            "FOREIGN KEY (BOOK_ID) REFERENCES BOOKS(ID));";
            stmt.execute(sqlCreateBorrows);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addBook(Book book) {
        String sql = "INSERT INTO BOOKS (TITLE, AUTHOR, AVAILABLE) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setBoolean(3, book.isAvailable());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1)); // Frissíti a könyv azonosítóját
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT ID, TITLE, AUTHOR, AVAILABLE FROM BOOKS";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("ID"));
                book.setTitle(rs.getString("TITLE"));
                book.setAuthor(rs.getString("AUTHOR"));
                book.setAvailable(rs.getBoolean("AVAILABLE"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public boolean borrowBook(int bookId, String borrowerName) {
        String sqlBorrow = "INSERT INTO BORROWS (BOOK_ID, BORROWER_NAME, BORROW_DATE) VALUES (?, ?, CURRENT_DATE())";
        String sqlUpdateBook = "UPDATE BOOKS SET AVAILABLE = FALSE WHERE ID = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmtBorrow = conn.prepareStatement(sqlBorrow);
             PreparedStatement pstmtUpdateBook = conn.prepareStatement(sqlUpdateBook)) {

            conn.setAutoCommit(false);

            pstmtUpdateBook.setInt(1, bookId);
            int updateCount = pstmtUpdateBook.executeUpdate();

            if (updateCount > 0) {
                pstmtBorrow.setInt(1, bookId);
                pstmtBorrow.setString(2, borrowerName);
                int borrowCount = pstmtBorrow.executeUpdate();

                if (borrowCount > 0) {
                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean returnBook(int bookId) {
        String sqlReturn = "UPDATE BORROWS SET RETURN_DATE = CURRENT_DATE() WHERE BOOK_ID = ? AND RETURN_DATE IS NULL";
        String sqlUpdateBook = "UPDATE BOOKS SET AVAILABLE = TRUE WHERE ID = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmtReturn = conn.prepareStatement(sqlReturn);
             PreparedStatement pstmtUpdateBook = conn.prepareStatement(sqlUpdateBook)) {

            conn.setAutoCommit(false);

            pstmtReturn.setInt(1, bookId);
            int returnCount = pstmtReturn.executeUpdate();

            if (returnCount > 0) {
                pstmtUpdateBook.setInt(1, bookId);
                int updateCount = pstmtUpdateBook.executeUpdate();

                if (updateCount > 0) {
                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<BorrowRecord> getBorrowingHistory() {
        List<BorrowRecord> history = new ArrayList<>();
        String sql = "SELECT B.BORROW_ID, B.BOOK_ID, BK.TITLE, B.BORROWER_NAME, B.BORROW_DATE, B.RETURN_DATE " +
                "FROM BORROWS B INNER JOIN BOOKS BK ON B.BOOK_ID = BK.ID";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setBookId(rs.getInt("BOOK_ID"));
                record.setBookTitle(rs.getString("TITLE"));
                record.setBorrowerName(rs.getString("BORROWER_NAME"));
                record.setBorrowDate(rs.getDate("BORROW_DATE").toLocalDate());
                Date returnDate = rs.getDate("RETURN_DATE");
                if (returnDate != null) {
                    record.setReturnDate(returnDate.toLocalDate());
                }
                history.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }


}
