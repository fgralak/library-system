package pl.gralak.librarysystem.book;

import java.util.List;

public interface BookService
{
    List<Book> getAllBooks();

    List<Book> getAllBooksWithGivenAuthor(String author);

    List<Book> getAllBooksWithGivenTitle(String title);

    List<Book> getAllBooksWithBetterRating(double rating);

    Book addBook(Book book);

    Book getBookById(Long id);

    Book updateBook(Book book);

    Book deleteBook(Long id);
}
