package org.example;

import org.junit.jupiter.api.Test;
import java.util.Date;
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
        Date borrowDate = new Date();
        Date returnDate = new Date();

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
        record.setBorrowerName("Kölcsönző Neve");
        record.setBorrowDate(new Date(1625097600000L)); // 2021. július 1.
        record.setReturnDate(new Date(1625184000000L)); // 2021. július 2.

        String resultString = record.toString();
        assertTrue(resultString.contains("Kölcsönző Neve"));
        assertTrue(resultString.contains("2021"));
        // További ellenőrzések, ha szükséges
    }
}

