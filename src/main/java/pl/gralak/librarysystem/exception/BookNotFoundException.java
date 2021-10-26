package pl.gralak.librarysystem.exception;

public class BookNotFoundException extends RuntimeException
{
    public BookNotFoundException(Long id)
    {
        super("Book with given id: " + id + " cannot be found");
    }

    public BookNotFoundException(String title, String author)
    {
        super("Book with title: " + title + " and author: " + author + " cannot be found");
    }
}
