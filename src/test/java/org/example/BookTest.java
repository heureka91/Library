package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    public void testBookGettersAndSetters() {
        // Arrange
        Book book = new Book();
        book.setId(1);
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setAvailable(true);

        // Assert
        assertEquals(1, book.getId());
        assertEquals("Test Title", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertTrue(book.isAvailable());
    }

    @Test
    public void testBookConstructor() {
        // Arrange and Act
        Book book = new Book(1, "Test Title", "Test Author", true);

        // Assert
        assertEquals(1, book.getId());
        assertEquals("Test Title", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertTrue(book.isAvailable());
    }

    @Test
    public void testToString() {
        // Arrange
        Book book = new Book(1, "Test Title", "Test Author", true);

        // Act
        String result = book.toString();

        // Assert
        assertEquals("Book{id=1, cím='Test Title', író='Test Author', elérhető=true}", result);
    }
}