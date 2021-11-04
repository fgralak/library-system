package pl.gralak.librarysystem.exception;

public class SomeBooksAreRentedException extends RuntimeException
{
    public SomeBooksAreRentedException()
    {
        super("Some book are still rented by users");
    }
}
