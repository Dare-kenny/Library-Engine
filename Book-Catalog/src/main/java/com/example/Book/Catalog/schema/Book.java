package com.example.Book.Catalog.schema;

import com.example.Book.Catalog.dtos.BookRequest;
import com.example.Book.Catalog.dtos.BookResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank
    private String title;

    @Column
    @NotBlank
    private String author;

    @Column
    @NotNull
    private int year;

    @Column
    @NotBlank
    private String genre;

    public Book(BookRequest bookDto){
        this.title = bookDto.getTitle();
        this.author = bookDto.getAuthor();
        this.year = bookDto.getYear();
        this.genre = bookDto.getGenre();
    }

    public Book (BookResponse response){
        this.id = response.getId();
        this.title = response.getTitle();
        this.author = response.getAuthor();
        this.year = response.getYear();
        this.genre = response.getGenre();
    }


}
