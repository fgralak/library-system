package pl.gralak.librarysystem.exception;

public class TooManyBooksRentedException extends RuntimeException
{
    public TooManyBooksRentedException()
    {
        super("User has already rented 5 books. Need to return a book to rent another");
    }
}
