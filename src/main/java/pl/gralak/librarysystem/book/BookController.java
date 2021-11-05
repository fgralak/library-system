package pl.gralak.librarysystem.book;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.gralak.librarysystem.appuser.AppUserService;
import pl.gralak.librarysystem.exception.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/book")
public class BookController
{
    private final BookServiceImpl bookServiceImpl;
    private final AppUserService appUserService;

    @GetMapping("/manage-books")
    public String manageBooks()
    {
        return "book/manage-books";
    }

    @GetMapping("/all")
    public String getAllBooks(Model model)
    {
        model.addAttribute("listOfBooks", bookServiceImpl.getAllBooks());
        model.addAttribute("role", getRole());
        return "book/book-list";
    }

    @GetMapping("/all-by-author")
    public String getAllBooksWithGivenAuthor(@RequestParam String author, Model model)
    {
        model.addAttribute("listOfBooks", bookServiceImpl.getAllBooksWithGivenAuthor(author));
        model.addAttribute("role", getRole());
        return "book/book-list";
    }

    @GetMapping("/all-by-title")
    public String getAllBooksWithGivenTitle(@RequestParam String title, Model model)
    {
        model.addAttribute("listOfBooks", bookServiceImpl.getAllBooksWithGivenTitle(title));
        model.addAttribute("role", getRole());
        return "book/book-list";
    }

    @GetMapping("/all-by-rating")
    public String getAllBooksWithBetterRating(@RequestParam double rating, Model model)
    {
        model.addAttribute("listOfBooks", bookServiceImpl.getAllBooksWithBetterRating(rating));
        model.addAttribute("role", getRole());
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

    @GetMapping("/new-book-form")
    public String showNewBookForm(Model model)
    {
        Book book = new Book();
        model.addAttribute("book", book);
        return "book/new-book-form";
    }

    @PostMapping("/create-new-book")
    public String createNewEmployee(@ModelAttribute("book") Book book, RedirectAttributes redirectAttributes)
    {
        try{
            bookServiceImpl.addBook(book);
        } catch(BookAlreadyExistsException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Book with title: " + book.getTitle() + " and author: " + book.getAuthor() + " already exists");
            return "redirect:/book/new-book-form";
        } catch(MissingTitleOrAuthorException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of title and/or author was null or empty");
            return "redirect:/book/new-book-form";
        }
        redirectAttributes.addFlashAttribute("success",
                "Book created!");
        return "redirect:/book/manage-books";
    }

    @GetMapping("/edit-book-form/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model)
    {
        model.addAttribute("book", bookServiceImpl.getBookById(id));
        return "book/edit-book-form";
    }

    @PostMapping("/edit-book/{id}")
    public String editBook(@PathVariable Long id, @ModelAttribute("book") Book book,
                           RedirectAttributes redirectAttributes)
    {
        try{
            bookServiceImpl.updateBook(book);
        } catch(MissingTitleOrAuthorException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of title and/or author was null or empty");
            return "redirect:/book/edit-book-form/{id}";
        } catch (SomeBooksAreRentedException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Cannot update book with given id because new number of book was lower than number of rented books");
            return "redirect:/book/manage-books";
        }

        redirectAttributes.addFlashAttribute("success",
                "Book updated!");
        return "redirect:/book/manage-books";
    }

    @GetMapping("/rent-book-form/{id}")
    public String showRentBookForm(@PathVariable Long id, Model model)
    {
        model.addAttribute("username", "");
        return "book/rent-book-form";
    }

    @PostMapping("/rent-book/{id}")
    public String rentBook(@PathVariable Long id, @ModelAttribute("username") String username,
                           RedirectAttributes redirectAttributes)
    {
        try{
            appUserService.rentBook(id, username);
        } catch(UserNotFoundException | TooManyBooksRentedException e)
        {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/book/rent-book-form/{id}";
        } catch(NoAvailableBookException e)
        {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/book/manage-books";
        }

        redirectAttributes.addFlashAttribute("success",
                "Book rented!");
        return "redirect:/book/manage-books";
    }

    @GetMapping("/return-book/{id}")
    public String returnBook(@PathVariable Long id, @RequestParam("username") String username,
                           RedirectAttributes redirectAttributes)
    {
        appUserService.returnBook(id, username);

        redirectAttributes.addFlashAttribute("success",
                "Book returned!");
        return "redirect:/book/manage-books";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes)
    {
        try
        {
            bookServiceImpl.deleteBook(id);
        } catch (BookNotFoundException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Book with given id does not exist");
            return "redirect:/book/manage-books";
        } catch (SomeBooksAreRentedException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Cannot delete book with given id because one or more copies are already rented");
            return "redirect:/book/manage-books";
        }
        redirectAttributes.addFlashAttribute("success",
                "Book deleted!");
        return "redirect:/book/manage-books";
    }

    private String getRole()
    {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))) ? "ROLE_EMPLOYEE" : "ROLE_USER";
    }
}
