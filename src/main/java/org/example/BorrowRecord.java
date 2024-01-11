package org.example;

import java.util.Date;

public class BorrowRecord {
    private String borrowerName;
    private Date borrowDate;
    private Date returnDate;

    // Constructor
    public BorrowRecord() {
        // Default constructor
    }

    // Getters and Setters
    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    // toString method for easy printing
    @Override
    public String toString() {
        return "BorrowRecord{" +
                "borrowerName='" + borrowerName + '\'' +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
