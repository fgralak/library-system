package pl.gralak.librarysystem.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gralak.librarysystem.exception.BookAlreadyExistsException;
import pl.gralak.librarysystem.exception.BookNotFoundException;
import pl.gralak.librarysystem.exception.MissingTitleOrAuthorException;
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
    public List<Book> getAllBooks()
    {
        return bookRepo.findAll();
    }

    @Override
    public List<Book> getAllBooksWithGivenAuthor(String author)
    {
        return bookRepo.findAllWithGivenAuthor(author);
    }

    @Override
    public List<Book> getAllBooksWithGivenTitle(String title)
    {
        return bookRepo.findAllWithGivenTitle(title);
    }

    @Override
    public List<Book> getAllBooksWithBetterRating(double rating)
    {
        return bookRepo.findAllWithBetterRating(rating);
    }

    @Override
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

        recordRepo.save(new Record(ADDED, bookToAdd.getTitle(), bookToAdd.getAuthor(),
                bookToAdd.getNumberOfBooks(), LocalDate.now()));

        return bookRepo.save(bookToAdd);
    }

    @Override
    public Book getBookById(Long id)
    {
        return bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    @Override
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
        bookForUpdate.setId(book.getId());

        if(book.getNumberOfBooks() > bookForUpdate.getNumberOfBooks())
        {
            recordRepo.save(new Record(DELETED, bookForUpdate.getTitle(), bookForUpdate.getAuthor(),
                    book.getNumberOfBooks() - bookForUpdate.getNumberOfBooks(), LocalDate.now()));
        }
        else if (book.getNumberOfBooks() < bookForUpdate.getNumberOfBooks())
        {
            recordRepo.save(new Record(ADDED, bookForUpdate.getTitle(), bookForUpdate.getAuthor(),
                    bookForUpdate.getNumberOfBooks() - book.getNumberOfBooks(), LocalDate.now()));
        }
        return bookRepo.save(bookForUpdate);
    }

    @Override
    public void deleteBook(Long id)
    {
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));

        recordRepo.save(new Record(DELETED, book.getTitle(), book.getAuthor(),
                book.getNumberOfBooks(), LocalDate.now()));

        bookRepo.deleteById(id);
    }
}
