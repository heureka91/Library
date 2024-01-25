package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.h2.tools.RunScript;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    private DatabaseManager dbManager;

    @BeforeEach
    void setUp() throws Exception {
        dbManager = new DatabaseManager();
        Connection conn = dbManager.getConnection();
        RunScript.execute(conn, new StringReader("DROP TABLE IF EXISTS BORROWS;"));
        RunScript.execute(conn, new StringReader("DROP TABLE IF EXISTS BOOKS;"));
        dbManager.initializeDB();
    }

    @AfterEach
    void tearDown() throws Exception {
        Connection conn = dbManager.getConnection();
        RunScript.execute(conn, new StringReader("DROP TABLE IF EXISTS BORROWS;"));
        RunScript.execute(conn, new StringReader("DROP TABLE IF EXISTS BOOKS;"));
    }

    @Test
    void testInitializeDB() throws Exception {
        dbManager.initializeDB();

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rsBooks = stmt.executeQuery(
                    "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'BOOKS'");
            assertTrue(rsBooks.next(), "The BOOKS table should be created");

            ResultSet rsBorrows = stmt.executeQuery(
                    "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'BORROWS'");
            assertTrue(rsBorrows.next(), "The BORROWS table should be created");
        }
    }

    @Test
    void testAddBook() throws Exception {
        Book book = new Book("Test Book", "Test Author", true);
        assertTrue(dbManager.addBook(book));

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM BOOKS WHERE TITLE = 'Test Book'");
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("total"), "There should be one book with the given title in the database");
        }
    }

    @Test
    void testBorrowBook() throws Exception {
        Book book = new Book("Borrowable Book", "Author", true);
        assertTrue(dbManager.addBook(book));
        assertTrue(dbManager.borrowBook(book.getId(), "Borrower"));

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT AVAILABLE FROM BOOKS WHERE ID = " + book.getId());
            assertTrue(rs.next());
            assertFalse(rs.getBoolean("AVAILABLE"), "The book should be marked as unavailable");
        }
    }

    @Test
    void testReturnBook() throws Exception {
        Book book = new Book("Returnable Book", "Author", true);
        assertTrue(dbManager.addBook(book));
        assertTrue(dbManager.borrowBook(book.getId(), "Borrower"));

        assertTrue(dbManager.returnBook(book.getId()));

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT AVAILABLE FROM BOOKS WHERE ID = " + book.getId());
            assertTrue(rs.next());
            assertTrue(rs.getBoolean("AVAILABLE"), "The book should be marked as available");
        }
    }

    @Test
    void testGetBorrowingHistory() throws Exception {
        Book book = new Book("History Book", "Author", true);
        assertTrue(dbManager.addBook(book));
        assertTrue(dbManager.borrowBook(book.getId(), "Borrower"));
        assertTrue(dbManager.returnBook(book.getId()));

        List<BorrowRecord> history = dbManager.getBorrowingHistory();
        assertFalse(history.isEmpty(), "Borrowing history should not be empty");

        boolean found = false;
        for (BorrowRecord record : history) {
            if (record.getBookId() == book.getId() && "Borrower".equals(record.getBorrowerName())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "The borrowing record for the added book should be in the history");
    }

    @Test
    void testGetBooks() throws Exception {
        assertTrue(dbManager.addBook(new Book("Test Book 1", "Author 1", true)));
        assertTrue(dbManager.addBook(new Book("Test Book 2", "Author 2", false)));

        List<Book> books = dbManager.getBooks();

        assertNotNull(books, "Books list should not be null");
        assertEquals(2, books.size(), "Books list should contain two entries");

        Book firstBook = books.stream().filter(b -> "Test Book 1".equals(b.getTitle())).findFirst().orElse(null);
        assertNotNull(firstBook);
        assertEquals("Author 1", firstBook.getAuthor());
        assertTrue(firstBook.isAvailable());

        Book secondBook = books.stream().filter(b -> "Test Book 2".equals(b.getTitle())).findFirst().orElse(null);
        assertNotNull(secondBook);
        assertEquals("Author 2", secondBook.getAuthor());
        assertFalse(secondBook.isAvailable());
    }
}


