package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private DatabaseManager databaseManager;

    // Example endpoint to add a book
    @PostMapping("/add")
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        databaseManager.addBook(book);
        return ResponseEntity.ok("Book added successfully");
    }

    // Additional endpoints for update, delete, get, etc.

}
