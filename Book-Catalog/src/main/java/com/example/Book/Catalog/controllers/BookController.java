package com.example.Book.Catalog.controllers;

import com.example.Book.Catalog.dtos.BookRequest;
import com.example.Book.Catalog.dtos.BookResponse;
import com.example.Book.Catalog.dtos.RequestBookUpdate;
import com.example.Book.Catalog.repositories.BookRepository;
import com.example.Book.Catalog.services.BookServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookController {

    @Autowired
    private BookServices bookService;

    @GetMapping("/showAll")
    public ResponseEntity<?> showBooks(){
        return bookService.showAll();
    }

    @GetMapping("/requestBook/{id}")
    public ResponseEntity<BookResponse> RequestBook(@PathVariable Long id){
        return ResponseEntity.ok(bookService.showById(id));
    }

    @GetMapping("/search/{term}/{page}/{size}")
    public Page<BookRepository.ShopBookView> searchBooks(@PathVariable String term, @PathVariable int page , @PathVariable int size){
        return bookService.search(term, page, size);
    }

    @PostMapping("/create_book")
    public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest requestedBook){
        return ResponseEntity.ok(bookService.create(requestedBook));
    }

    @PutMapping("/update_book/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id , @RequestBody RequestBookUpdate requestedBook){
        return ResponseEntity.ok(bookService.update(id,requestedBook));
    }
    @DeleteMapping("/delete_book/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id){
        return bookService.delete(id);
    }
}
