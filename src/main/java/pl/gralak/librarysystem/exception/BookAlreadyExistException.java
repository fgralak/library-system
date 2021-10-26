package pl.gralak.librarysystem.exception;

public class BookAlreadyExistException extends RuntimeException
{
    public BookAlreadyExistException(String title, String author)
    {
        super("Book with title: " + title + " and author: " + author + " already exists");
    }
}
