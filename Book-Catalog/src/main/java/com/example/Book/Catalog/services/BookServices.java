package com.example.Book.Catalog.services;

import com.example.Book.Catalog.dtos.BookRequest;
import com.example.Book.Catalog.dtos.BookResponse;
import com.example.Book.Catalog.dtos.RequestBookUpdate;
import com.example.Book.Catalog.repositories.BookRepository;
import com.example.Book.Catalog.schema.Book;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServices {

    private static final Logger log = LoggerFactory.getLogger(BookServices.class);
    @Autowired
    private BookRepository repository;

    public record searchRequest(String term , int page , int size){};

    public ResponseEntity<?> showAll(){
        List<Book> AllBooks = repository.findAll();
        return ResponseEntity.ok(AllBooks.stream().map(book -> new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getYear(), book.getGenre())).collect(Collectors.toList()));
    }

    @Cacheable(cacheNames = "bookById",key = "#id") //check the cache "bookById" first before performing the action ,cache the book by ID
    public BookResponse showById(Long id){
        log.info("DB HIT: loading book id="+id);
        Book requestBook = repository.findById(id).orElseThrow(()-> new RuntimeException("Book not found"));
        return new BookResponse(requestBook.getId(), requestBook.getTitle(),requestBook.getAuthor(),requestBook.getYear(),requestBook.getGenre());
    }

    @Cacheable(cacheNames = "bookSearch", key = "#request")
    public Page<BookRepository.ShopBookView> search(searchRequest request){
        log.info("DB HIT: searching term="+request.term()+" page="+request.page()+" size="+request.size());
        String q = (request.term() == null ? "": request.term());
        return repository.findByGenreContainingIgnoreCase(q, PageRequest.of(request.page(),request.size()));
    }

    @CachePut(cacheNames = "bookById", key = "#result.id")
    public BookResponse create(BookRequest request){
        Book bookEntity = new Book(request);
        repository.save(bookEntity);
        log.info("CREATE: id= "+bookEntity.getId()+" cached via @CachePut");

        return new BookResponse(bookEntity.getId(), bookEntity.getTitle(),bookEntity.getAuthor(), bookEntity.getYear(), bookEntity.getGenre());
    }

    @Caching(evict = {@CacheEvict(cacheNames = "bookSearch", allEntries = true)},put = {@CachePut(cacheNames = "bookById",key = "#id")})// @caching is used to pur two caching annotations together : what this does is that it clears the cached searches and then refreshes entries into the "bookById" cache using id as the key
    public BookResponse update(Long id , RequestBookUpdate updatedBook){
        Book exists = repository.findById(id).orElseThrow(()-> new RuntimeException("Book not found"));
        if(updatedBook.getTitle() != null){exists.setTitle(updatedBook.getTitle());}
        if(updatedBook.getAuthor() != null){exists.setAuthor(updatedBook.getAuthor());}
        if(updatedBook.getYear() != null){exists.setYear(updatedBook.getYear());}
        if(updatedBook.getGenre() != null){exists.setGenre(updatedBook.getGenre());}

        repository.save(exists);
        return new BookResponse(exists.getId(), exists.getTitle(), exists.getAuthor(), exists.getYear(), exists.getGenre());
    }

    @Caching(evict = {@CacheEvict(cacheNames = "bookById", key = "#id"),@CacheEvict(cacheNames = "bookSearch",allEntries = true)}) // clears both the "bookById" and "bookSearch" caches
    public ResponseEntity<?> delete(Long id ){
        Book requestedBook = repository.findById(id).orElseThrow(()-> new RuntimeException("Book not found"));
        repository.deleteById(requestedBook.getId());
        return ResponseEntity.ok("Deleted Book with id "+requestedBook.getId());
    }
}
