package pl.gralak.librarysystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gralak.librarysystem.entity.Book;
import pl.gralak.librarysystem.exception.BookAlreadyExistException;
import pl.gralak.librarysystem.exception.BookNotFoundException;
import pl.gralak.librarysystem.exception.MissingTitleOrAuthorException;
import pl.gralak.librarysystem.repository.BookRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService
{
    private final BookRepo bookRepo;

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
            throw new BookAlreadyExistException(title, author);
        }
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
        return bookRepo.save(bookForUpdate);
    }

    @Override
    public void deleteBook(Long id)
    {
        bookRepo.deleteById(id);
    }
}
