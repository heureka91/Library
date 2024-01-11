package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static void initializeDB() {
        // SQL utasítások a táblák létrehozására
        String createBooksTableSQL = "CREATE TABLE IF NOT EXISTS BOOKS (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "TITLE VARCHAR(255), " +
                "AUTHOR VARCHAR(255), " +
                "AVAILABLE BOOLEAN);";

        String createBorrowsTableSQL = "CREATE TABLE IF NOT EXISTS BORROWS (" +
                "BORROW_ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "BOOK_ID INT, " +
                "BORROWER_NAME VARCHAR(255), " +
                "BORROW_DATE DATE, " +
                "RETURN_DATE DATE, " +
                "FOREIGN KEY (BOOK_ID) REFERENCES BOOKS(ID));";

        // További táblák létrehozására vonatkozó SQL utasítások

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // Táblák létrehozása
            stmt.execute(createBooksTableSQL);
            stmt.execute(createBorrowsTableSQL);
            // Többi tábla létrehozása

            System.out.println("Tables are created if they did not already exist.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean returnBook(int bookId) {
        String updateBorrowsSQL = "UPDATE BORROWS SET RETURN_DATE = CURRENT_DATE() WHERE BOOK_ID = ? AND RETURN_DATE IS NULL;";
        String updateBooksSQL = "UPDATE BOOKS SET AVAILABLE = TRUE WHERE ID = ?;";

        try (Connection conn = getConnection();
             PreparedStatement pstBorrows = conn.prepareStatement(updateBorrowsSQL);
             PreparedStatement pstBooks = conn.prepareStatement(updateBooksSQL)) {

            // Update the BORROWS table to set the return date
            pstBorrows.setInt(1, bookId);
            int rowsAffectedBorrows = pstBorrows.executeUpdate();

            // Update the BOOKS table to reflect that the book is now available
            pstBooks.setInt(1, bookId);
            int rowsAffectedBooks = pstBooks.executeUpdate();

            // Ensure both operations are successful
            return rowsAffectedBorrows > 0 && rowsAffectedBooks > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    public static List<BorrowRecord> getBorrowingHistory(int bookId) {
        List<BorrowRecord> history = new ArrayList<>();
        String sql = "SELECT BORROWER_NAME, BORROW_DATE, RETURN_DATE FROM BORROWS WHERE BOOK_ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setBorrowerName(rs.getString("BORROWER_NAME"));
                record.setBorrowDate(rs.getDate("BORROW_DATE"));
                record.setReturnDate(rs.getDate("RETURN_DATE"));
                history.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

}

