package org.example;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class BorrowRecordTest {

    @Test
    void testDefaultConstructor() {
        BorrowRecord record = new BorrowRecord();
        assertNotNull(record, "A BorrowRecord objektumnak nem szabad null-nak lennie");
    }

    @Test
    void testSettersAndGetters() {
        BorrowRecord record = new BorrowRecord();
        LocalDate borrowDate = LocalDate.now();
        LocalDate returnDate = LocalDate.now().plusDays(1);

        record.setBorrowerName("Kölcsönző Neve");
        record.setBorrowDate(borrowDate);
        record.setReturnDate(returnDate);

        assertEquals("Kölcsönző Neve", record.getBorrowerName());
        assertEquals(borrowDate, record.getBorrowDate());
        assertEquals(returnDate, record.getReturnDate());
    }

    @Test
    void testToString() {
        BorrowRecord record = new BorrowRecord();
        LocalDate borrowDate = LocalDate.of(2021, 7, 1); // 2021. július 1.
        LocalDate returnDate = LocalDate.of(2021, 7, 2); // 2021. július 2.

        record.setBorrowerName("Kölcsönző Neve");
        record.setBorrowDate(borrowDate);
        record.setReturnDate(returnDate);

        String resultString = record.toString();
        assertTrue(resultString.contains("Kölcsönző Neve"));
        assertTrue(resultString.contains("2021"));
        // További ellenőrzések, ha szükséges
    }
    @Test
    void testParameterizedConstructor() {
        int expectedBookId = 1;
        String expectedBookTitle = "Test Book";
        String expectedBorrowerName = "Test Borrower";
        LocalDate expectedBorrowDate = LocalDate.of(2021, 1, 1);
        LocalDate expectedReturnDate = LocalDate.of(2021, 1, 10);

        BorrowRecord record = new BorrowRecord(expectedBookId, expectedBookTitle, expectedBorrowerName, expectedBorrowDate, expectedReturnDate);

        assertEquals(expectedBookId, record.getBookId(), "Book ID should match the provided value");
        assertEquals(expectedBookTitle, record.getBookTitle(), "Book title should match the provided value");
        assertEquals(expectedBorrowerName, record.getBorrowerName(), "Borrower name should match the provided value");
        assertEquals(expectedBorrowDate, record.getBorrowDate(), "Borrow date should match the provided value");
        assertEquals(expectedReturnDate, record.getReturnDate(), "Return date should match the provided value");
    }
}
