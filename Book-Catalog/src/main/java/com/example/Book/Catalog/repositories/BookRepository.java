package com.example.Book.Catalog.repositories;

import com.example.Book.Catalog.schema.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {

    interface ShopBookView{
        Long getId();
        String getTitle();
        String getAuthor();
        int getYear();
    }

    Page<ShopBookView> findByGenreContainingIgnoreCase(String genre , PageRequest pageRequest);
}
