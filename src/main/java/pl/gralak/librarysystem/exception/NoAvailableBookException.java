package pl.gralak.librarysystem.exception;

public class NoAvailableBookException extends RuntimeException
{
    public NoAvailableBookException()
    {
        super("There is no available book to rent");
    }
}
