package com.example.Book.Catalog.dtos;


import com.example.Book.Catalog.schema.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRequest {
    private String title;
    private String author;
    private int year;
    private String genre;

    public BookRequest(Book book){
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.year = book.getYear();
        this.genre = book.getGenre();
    }
}
