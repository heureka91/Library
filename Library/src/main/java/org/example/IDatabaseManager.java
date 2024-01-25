package org.example;

import java.util.List;

public interface IDatabaseManager {
    boolean addBook(Book book);
    List<Book> getBooks();
    boolean borrowBook(int bookId, String borrowerName);
    boolean returnBook(int bookId);
    List<BorrowRecord> getBorrowingHistory();
}
