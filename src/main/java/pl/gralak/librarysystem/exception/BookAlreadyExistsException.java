package pl.gralak.librarysystem.exception;

public class BookAlreadyExistsException extends RuntimeException
{
    public BookAlreadyExistsException(String title, String author)
    {
        super("Book with title: " + title + " and author: " + author + " already exists");
    }
}
