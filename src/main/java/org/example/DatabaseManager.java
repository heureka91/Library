package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    private static final String USER = "sa";
    private static final String PASS = "";


    public static void initializeDB() {
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS USERS (" +
                "USER_ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "USER_NAME VARCHAR(255) UNIQUE);";

        String createBooksTableSQL = "CREATE TABLE IF NOT EXISTS BOOKS (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "TITLE VARCHAR(255), " +
                "AUTHOR VARCHAR(255), " +
                "AVAILABLE BOOLEAN DEFAULT TRUE);";

        String createBorrowingsTableSQL = "CREATE TABLE IF NOT EXISTS BORROWINGS (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "BOOK_ID INT, " +
                "USER_ID INT, " +
                "BORROW_DATE TIMESTAMP, " +
                "RETURN_DATE TIMESTAMP, " +
                "FOREIGN KEY (BOOK_ID) REFERENCES BOOKS(ID), " +
                "FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID));";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTableSQL);
            stmt.execute(createBooksTableSQL);
            stmt.execute(createBorrowingsTableSQL);
            System.out.println("Tables 'USERS', 'BOOKS', and 'BORROWINGS' have been created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }


}
