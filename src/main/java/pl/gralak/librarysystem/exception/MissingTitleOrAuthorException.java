package pl.gralak.librarysystem.exception;

public class MissingTitleOrAuthorException extends RuntimeException
{
    public MissingTitleOrAuthorException()
    {
        super("Value of title and/or author is null or empty");
    }
}
