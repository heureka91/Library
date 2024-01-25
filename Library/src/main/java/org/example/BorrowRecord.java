package org.example;

import java.time.LocalDate;

public class BorrowRecord {
    private int bookId;
    private String bookTitle;
    private String borrowerName;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    // Default Constructor
    public BorrowRecord() {
    }

    // Parameterized Constructor
    public BorrowRecord(int bookId, String bookTitle, String borrowerName, LocalDate borrowDate, LocalDate returnDate) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowerName = borrowerName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    // Getters and Setters
    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        if (returnDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Return date cannot be before borrow date.");
        }
        this.returnDate = returnDate;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    // toString method for easy printing
    @Override
    public String toString() {
        return "BorrowRecord{" +
                "bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", borrowerName='" + borrowerName + '\'' +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
