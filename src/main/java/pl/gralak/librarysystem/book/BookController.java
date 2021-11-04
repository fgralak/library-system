package pl.gralak.librarysystem.book;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/book")
public class BookController
{
    private final BookServiceImpl bookServiceImpl;

    @GetMapping("/all")
    public String getAllBooks(Model model)
    {
        model.addAttribute("listOfBooks", bookServiceImpl.getAllBooks());
        return "book/book-list";
    }

    @GetMapping("/all-by-author")
    public String getAllBooksWithGivenAuthor(@RequestParam String author, Model model)
    {
        model.addAttribute("listOfBooks", bookServiceImpl.getAllBooksWithGivenAuthor(author));
        return "book/book-list";
    }

    @GetMapping("/all-by-title")
    public String getAllBooksWithGivenTitle(@RequestParam String title, Model model)
    {
        model.addAttribute("listOfBooks", bookServiceImpl.getAllBooksWithGivenTitle(title));
        return "book/book-list";
    }

    @GetMapping("/all-by-rating")
    public String getAllBooksWithBetterRating(@RequestParam double rating, Model model)
    {
        model.addAttribute("listOfBooks", bookServiceImpl.getAllBooksWithBetterRating(rating));
        return "book/book-list";
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
