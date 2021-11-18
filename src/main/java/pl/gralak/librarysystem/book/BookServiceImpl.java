package pl.gralak.librarysystem.book;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gralak.librarysystem.exception.BookAlreadyExistsException;
import pl.gralak.librarysystem.exception.BookNotFoundException;
import pl.gralak.librarysystem.exception.MissingTitleOrAuthorException;
import pl.gralak.librarysystem.exception.SomeBooksAreRentedException;
import pl.gralak.librarysystem.record.Record;
import pl.gralak.librarysystem.record.RecordRepo;

import java.time.LocalDate;
import java.util.List;

import static pl.gralak.librarysystem.record.Action.ADDED;
import static pl.gralak.librarysystem.record.Action.DELETED;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService
{
    private final BookRepo bookRepo;
    private final RecordRepo recordRepo;

    @Override
    @Cacheable(cacheNames = "AllBooks")
    public List<Book> getAllBooks()
    {
        return bookRepo.findAll();
    }

    @Override
    @Cacheable(cacheNames = "AllBooksAuthor", key="#author")
    public List<Book> getAllBooksWithGivenAuthor(String author)
    {
        if(author == null || author.length() == 0)
        {
            throw new MissingTitleOrAuthorException();
        }
        return bookRepo.findAllWithGivenAuthor(author);
    }

    @Override
    @Cacheable(cacheNames = "AllBooksTitle", key="#title")
    public List<Book> getAllBooksWithGivenTitle(String title)
    {
        if(title == null || title.length() == 0)
        {
            throw new MissingTitleOrAuthorException();
        }
        return bookRepo.findAllWithGivenTitle(title);
    }

    @Override
    @Cacheable(cacheNames = "AllBooksRating", key="#rating")
    public List<Book> getAllBooksWithBetterRating(double rating)
    {
        return bookRepo.findAllWithBetterRating(rating);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value="AllBooks", allEntries = true),
            @CacheEvict(value="AllBooksTitle", key="#bookToAdd.title"),
            @CacheEvict(value="AllBooksAuthor", key="#bookToAdd.author"),
            @CacheEvict(value="AllBooksRating", key="#bookToAdd.rating")
            })
    public Book addBook(Book bookToAdd)
    {
        String title = bookToAdd.getTitle();
        String author = bookToAdd.getAuthor();
        if(title == null || title.length() == 0 || author == null || author.length() == 0)
        {
            throw new MissingTitleOrAuthorException();
        }

        Book book = bookRepo.findBookByTitleAndAuthor(title, author);
        if(book != null)
        {
            throw new BookAlreadyExistsException(title, author);
        }
        if(bookToAdd.getYear() > LocalDate.now().getYear())
        {
            throw new IllegalStateException("Given year is greater than current one");
        }

        recordRepo.save(new Record(ADDED, bookToAdd.getTitle(), bookToAdd.getAuthor(),
                bookToAdd.getNumberOfBooks(), LocalDate.now()));

        bookToAdd.setBooksAvailable(bookToAdd.getNumberOfBooks());
        return bookRepo.save(bookToAdd);
    }

    @Override
    public Book getBookById(Long id)
    {
        return bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value="AllBooks", allEntries = true),
            @CacheEvict(value="AllBooksTitle", key="#bookForUpdate.title"),
            @CacheEvict(value="AllBooksAuthor", key="#bookForUpdate.author"),
            @CacheEvict(value="AllBooksRating", key="#bookForUpdate.rating")
    })
    public Book updateBook(Book bookForUpdate)
    {
        String title = bookForUpdate.getTitle();
        String author = bookForUpdate.getAuthor();
        if(title == null || title.length() == 0 || author == null || author.length() == 0)
        {
            throw new MissingTitleOrAuthorException();
        }

        Book book = bookRepo.findBookByTitleAndAuthor(title, author);
        if(book == null)
        {
            throw new BookNotFoundException(title, author);
        }

        if(bookForUpdate.getYear() > LocalDate.now().getYear())
        {
            throw new IllegalStateException("Given year is greater than current one");
        }

        bookForUpdate.setId(book.getId());

        if((book.getNumberOfBooks() - book.getBooksAvailable()) > bookForUpdate.getNumberOfBooks())
        {
            throw new SomeBooksAreRentedException();
        }

        if(book.getNumberOfBooks() > bookForUpdate.getNumberOfBooks())
        {
            recordRepo.save(new Record(DELETED, bookForUpdate.getTitle(), bookForUpdate.getAuthor(),
                    book.getNumberOfBooks() - bookForUpdate.getNumberOfBooks(), LocalDate.now()));
            int available = book.getBooksAvailable() - (book.getNumberOfBooks() - bookForUpdate.getNumberOfBooks());
            bookForUpdate.setBooksAvailable(available);
        }
        else if (book.getNumberOfBooks() < bookForUpdate.getNumberOfBooks())
        {
            recordRepo.save(new Record(ADDED, bookForUpdate.getTitle(), bookForUpdate.getAuthor(),
                    bookForUpdate.getNumberOfBooks() - book.getNumberOfBooks(), LocalDate.now()));
            int available = book.getBooksAvailable() + (bookForUpdate.getNumberOfBooks() - book.getNumberOfBooks());
            bookForUpdate.setBooksAvailable(available);
        }
        return bookRepo.save(bookForUpdate);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value="AllBooks", allEntries = true),
            @CacheEvict(value="AllBooksTitle", key="#result.title"),
            @CacheEvict(value="AllBooksAuthor", key="#result.author"),
            @CacheEvict(value="AllBooksRating", key="#result.rating")
    })
    public Book deleteBook(Long id)
    {
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));

        if(book.getBooksAvailable() < book.getNumberOfBooks())
        {
            throw new SomeBooksAreRentedException();
        }

        recordRepo.save(new Record(DELETED, book.getTitle(), book.getAuthor(),
                book.getNumberOfBooks(), LocalDate.now()));

        bookRepo.deleteById(id);
        return book;
    }
}
