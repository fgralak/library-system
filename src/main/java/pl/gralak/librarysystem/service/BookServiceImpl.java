package pl.gralak.librarysystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.gralak.librarysystem.entity.Book;
import pl.gralak.librarysystem.repository.BookRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
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
    public Book addBook(Book book)
    {
        return bookRepo.save(book);
    }

    @Override
    public Book getBookById(Long id)
    {
        return bookRepo.getById(id);
    }

    @Override
    public Book updateBook(Book book)
    {
        return bookRepo.save(book);
    }

    @Override
    public void deleteBook(Long id)
    {
        bookRepo.deleteById(id);
    }
}
