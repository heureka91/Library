package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testDefaultConstructor() {
        Book book = new Book();
        assertNotNull(book, "A book objektumnak nem szabad null-nak lennie");
    }

    @Test
    void testParameterizedConstructor() {
        Book book = new Book(1, "A próféta", "Khalil Gibran", true);
        assertEquals(1, book.getId());
        assertEquals("A próféta", book.getTitle());
        assertEquals("Khalil Gibran", book.getAuthor());
        assertTrue(book.isAvailable());
    }

    @Test
    void testSettersAndGetters() {
        Book book = new Book();
        book.setId(2);
        book.setTitle("Az ötödik hegy");
        book.setAuthor("Paulo Coelho");
        book.setAvailable(false);

        assertEquals(2, book.getId());
        assertEquals("Az ötödik hegy", book.getTitle());
        assertEquals("Paulo Coelho", book.getAuthor());
        assertFalse(book.isAvailable());
    }

    @Test
    void testToString() {
        Book book = new Book(3, "Az Alkimista", "Paulo Coelho", true);
        String expectedString = "Book{id=3, cím='Az Alkimista', író='Paulo Coelho', elérhető=true}";
        assertEquals(expectedString, book.toString());
    }
}
