package pl.gralak.librarysystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.gralak.librarysystem.entity.Book;
import pl.gralak.librarysystem.service.BookServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookController
{
    private final BookServiceImpl bookServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks()
    {
        return new ResponseEntity<>(bookServiceImpl.getAllBooks(), HttpStatus.OK);
    }

    @GetMapping("/all-by-author")
    public ResponseEntity<List<Book>> getAllBooksWithGivenAuthor(@RequestParam String author)
    {
        return new ResponseEntity<>(bookServiceImpl.getAllBooksWithGivenAuthor(author), HttpStatus.OK);
    }

    @GetMapping("/all-by-title")
    public ResponseEntity<List<Book>> getAllBooksWithGivenTitle(@RequestParam String title)
    {
        return new ResponseEntity<>(bookServiceImpl.getAllBooksWithGivenTitle(title), HttpStatus.OK);
    }

    @GetMapping("/all-by-rating")
    public ResponseEntity<List<Book>> getAllBooksWithBetterRating(@RequestParam int rating)
    {
        return new ResponseEntity<>(bookServiceImpl.getAllBooksWithBetterRating(rating), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Book> getBookById(@RequestParam Long id)
    {
        return new ResponseEntity<>(bookServiceImpl.getBookById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book)
    {
        return new ResponseEntity<>(bookServiceImpl.addBook(book), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Book> updateBook(@RequestBody Book book)
    {
        return new ResponseEntity<>(bookServiceImpl.updateBook(book), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteBook(@RequestParam Long id)
    {
        bookServiceImpl.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
